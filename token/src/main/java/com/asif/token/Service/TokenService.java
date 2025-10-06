package com.asif.token.Service;

import com.asif.token.Dto.DtoApiResponse;
import com.asif.token.Entity.ShopToken;
import com.asif.token.Entity.Shops;
import com.asif.token.Entity.Users;
import com.asif.token.JwtUtil.TokenUtil;
import com.asif.token.Repository.ShopRepository;
import com.asif.token.Repository.ShopTokenRepository;
import com.asif.token.Repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TokenService {
    @Autowired
    public TokenRepository repo;

    @Autowired
    public ShopRepository shopRepo;

    @Autowired
    private ShopTokenRepository shopTokenRepo;


    @Autowired
    public TokenUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;


    public DtoApiResponse signup(Users user) {
        if (repo.existsByMobile(user.getMobile())) {
            return new DtoApiResponse("error", "USER ALREADY REGISTERED", user.getMobile());
        }
        if (user.getMobile().length() != 10) {
            return new DtoApiResponse("error", "Invalid Mobile Number", user.getMobile());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);
        return new DtoApiResponse("success", "SIGNUP SUCCESSFUL", user.getName());
    }

    public DtoApiResponse login(String mobile, String password) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null) {
            return new DtoApiResponse("error", "INVALID NUMBER", mobile);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new DtoApiResponse("error", "INVALID PASSWORD", password);
        }
        String token = jwtUtil.generateToken(user.getMobile(), user.getRole());
        return new DtoApiResponse("success","login successful", token);
    }

    public DtoApiResponse createShop(String mobile, Shops shop) {
        Users owner = repo.findByMobile(mobile).orElse(null);
        if (owner == null || !"ROLE_OWNER".equals(owner.getRole())) {
            return new DtoApiResponse("error", "only owner can create shop", null);
        }
        shop.setOWNER(owner);
        shopRepo.save(shop);
        return new DtoApiResponse("success", "shop create successful", shop.getShopName());
    }

    public DtoApiResponse takeToken(String mobile, Long shopId) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null || (!"ROLE_CUSTOMER".equals(user.getRole()) && !"ROLE_OWNER".equals(user.getRole()))) {
            return new DtoApiResponse("error", "only customer and shopOwner can take token", null);
        }
        Shops shop = shopRepo.findById(shopId).orElse(null);
        if (shop == null) {
            return new DtoApiResponse("error", "shop not found", null);
        }

        Optional<ShopToken> existingToken = shopTokenRepo.findByUserAndShop(user, shop);
        if (existingToken.isPresent()) {
            return new DtoApiResponse("error", "you already have a token in this shop", null);
        }

        int newToken = shop.getCurrentToken() + 1;
        shop.setCurrentToken(newToken);
        shopRepo.save(shop);

        int waitTime = (newToken - 1) * shop.getTimePerCustomer();
        ShopToken shopToken = new ShopToken();
        shopToken.setUser(user);
        shopToken.setShop(shop);
        shopToken.setTokenNo(newToken);
        shopTokenRepo.save(shopToken);
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("shopId", shop.getId());
        tokenData.put("tokenNo", newToken);
        tokenData.put("estimatedWaitTime", waitTime);
        return new DtoApiResponse("success", "token issued successful", tokenData);
    }

    public DtoApiResponse getAllShops(String mobile) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null || (!"ROLE_CUSTOMER".equals(user.getRole()) && !"ROLE_OWNER".equals(user.getRole()))) {
            return new DtoApiResponse("error", "only customer and shopOwner can see shop", null);
        }
        List<Shops> shops = shopRepo.findAll();
        return new DtoApiResponse("success", "list of shops", shops);
    }



    public DtoApiResponse showMyTokens(String mobile) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null || (!"ROLE_CUSTOMER".equals(user.getRole()) && !"ROLE_OWNER".equals(user.getRole()))) {
            return new DtoApiResponse("error", "only customer and shopOwner can see tokens", null);
        }

        List<ShopToken> tokens = shopTokenRepo.findByUser(user);

        if (tokens.isEmpty()) {
            return new DtoApiResponse("success", "you have no tokens", null);
        }

        List<Map<String, Object>> tokenList = new ArrayList<>();

        for (ShopToken token : tokens) {
            Shops shop = token.getShop();
            int waitTime = (token.getTokenNo() - 1) * shop.getTimePerCustomer();
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("shopId", shop.getId());
            tokenData.put("shopName", shop.getShopName());
            tokenData.put("shopType", shop.getShopType());
            tokenData.put("address", shop.getAddress());
            tokenData.put("yourTokenNo", token.getTokenNo());
            tokenData.put("estimatedWaitTime", waitTime);

            tokenList.add(tokenData);
        }

        return new DtoApiResponse("success", "yourTokens", tokenList);
    }

    public DtoApiResponse myShops(String mobile) {
        Users owner = repo.findByMobile(mobile).orElse(null);
        if (owner == null || !"ROLE_OWNER".equals(owner.getRole())) {
            return new DtoApiResponse("error", "only owner can see their shops", null);
        }

        List<Shops> shops = shopRepo.findByOWNER(owner);

        List<Map<String, Object>> shopList = new ArrayList<>();

        for (Shops shop : shops) {
            int waitTime = shop.getCurrentToken() * shop.getTimePerCustomer();

            Map<String, Object> shopData = new HashMap<>();
            shopData.put("id", shop.getId());
            shopData.put("shopName", shop.getShopName());
            shopData.put("shopType", shop.getShopType());
            shopData.put("address", shop.getAddress());
            shopData.put("currentToken", shop.getCurrentToken());
            shopData.put("timePerCustomer", shop.getTimePerCustomer());
            shopData.put("estimatedWaitTime", waitTime);

            shopList.add(shopData);
        }

        return new DtoApiResponse("success", "yourShops", shopList);
    }

    public DtoApiResponse getShop(String mobile, Long id) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null) {
            return new DtoApiResponse("error", "invalid users", null);
        }
        Shops shop = shopRepo.findById(id).orElse(null);
        if (shop == null) {
            return new DtoApiResponse("error", "shop not found", null);
        }
        int waitTime = shop.getCurrentToken() * shop.getTimePerCustomer();
        Map<String, Object> shopData = new HashMap<>();
        shopData.put("id", shop.getId());
        shopData.put("shopName", shop.getShopName());
        shopData.put("shopType", shop.getShopType());
        shopData.put("address", shop.getAddress());
        shopData.put("currentToken", shop.getCurrentToken());
        shopData.put("timePerCustomer", shop.getTimePerCustomer());
        shopData.put("estimatedWaitTime", waitTime);
        return new DtoApiResponse("success", "shop found", shopData);
    }


}




