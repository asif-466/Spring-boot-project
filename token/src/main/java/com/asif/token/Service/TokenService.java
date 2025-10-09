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

import java.time.LocalTime;
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
        String token = jwtUtil.generateToken(user.getMobile());
        return new DtoApiResponse("success", "login successful", token);
    }


    public DtoApiResponse createShop(String mobile, Shops shop) {
        Users owner = repo.findByMobile(mobile).orElse(null);
        if (owner == null) {
            return new DtoApiResponse("error", "Invalid user", null);
        }
        if(shop.getCloseTime() != null && !shop.getCloseTime().isEmpty()){
            try {
                LocalTime.parse(shop.getCloseTime());
            }catch (Exception e){
                return new DtoApiResponse("error","Invalid close time format.Use HH:mm(e.g. 21:30)",null);
            }
        }
        shop.setOwner(owner);
        shopRepo.save(shop);
        return new DtoApiResponse("success", "Shop created successfully", shop.getShopName());
    }

    public DtoApiResponse takeToken(String mobile, Long shopId) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null) return new DtoApiResponse("error", "Invalid user", null);

        Shops shop = shopRepo.findById(shopId).orElse(null);
        if (shop == null) return new DtoApiResponse("error", "Shop not found", null);

        if(shop.getCloseTime() !=null){
            LocalTime now=LocalTime.now();
            LocalTime closeTime=LocalTime.parse(shop.getCloseTime());
            if(now.isAfter(closeTime)){
                return new DtoApiResponse("error","Shop is closed",null);
            }
        }
        Optional<ShopToken> existingToken = shopTokenRepo.findByUserAndShop(user, shop);
        if (existingToken.isPresent()) {
            return new DtoApiResponse("error", "You already have a token in this shop", null);
        }


        if (shop.getCurrentToken() < 1) {
            shop.setCurrentToken(1);
        }


        int newTokenNo = shop.getTotalToken() + 1;
        shop.setTotalToken(newTokenNo);
        shopRepo.save(shop);

        int currentToken = shop.getCurrentToken();
        if (currentToken < 1) currentToken = 1;

        int waitTime = 0;
        if (newTokenNo > currentToken) {
            waitTime = (newTokenNo - currentToken) * shop.getTimePerCustomer();
        }


        ShopToken token = new ShopToken();
        token.setUser(user);
        token.setShop(shop);
        token.setTokenNo(newTokenNo);
        shopTokenRepo.save(token);

        Map<String, Object> data = new HashMap<>();
        data.put("shopName", shop.getShopName());
        data.put("tokenNo", newTokenNo);
        data.put("estimatedWaitTime", waitTime);
        data.put("currentToken", shop.getCurrentToken());
        data.put("nextTokenToComplete", shop.getCurrentToken());
        data.put("closeTime",shop.getCloseTime());

        return new DtoApiResponse("success", "Token issued successfully", data);
    }


    public DtoApiResponse getAllShops(String mobile) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null) {
            return new DtoApiResponse("error", "Invalid user", null);
        }
        List<Shops> shops = shopRepo.findAll();
        return new DtoApiResponse("success", "List of shops", shops);
    }


    public DtoApiResponse showMyTokens(String mobile) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null) {
            return new DtoApiResponse("error", "Invalid user", null);
        }

        List<ShopToken> tokens = shopTokenRepo.findByUser(user);
        if (tokens.isEmpty()) {
            return new DtoApiResponse("success", "You have no tokens", null);
        }



        List<Map<String, Object>> tokenList = new ArrayList<>();
        for (ShopToken token : tokens) {
            Shops shop = token.getShop();


            int waitTime = (token.getTokenNo() - shop.getCurrentToken()) * shop.getTimePerCustomer();
            if (waitTime < 0) waitTime = 0;

            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("shopId", shop.getId());
            tokenData.put("shopName", shop.getShopName());
            tokenData.put("shopType", shop.getShopType());
            tokenData.put("address", shop.getAddress());
            tokenData.put("yourTokenNo", token.getTokenNo());
            tokenData.put("currentToken", shop.getCurrentToken());
            tokenData.put("estimatedWaitTime", waitTime);
            tokenData.put("closeTime",shop.getCloseTime());
            LocalTime now = LocalTime.now();
            if (shop.getCloseTime() != null && now.isAfter(LocalTime.parse(shop.getCloseTime()))) {
                tokenData.put("status", "expired");
            } else {
                tokenData.put("status", "active");
            }

            tokenList.add(tokenData);

        }

        return new DtoApiResponse("success", "Your tokens", tokenList);
    }

    public DtoApiResponse myShops(String mobile) {
        Users owner = repo.findByMobile(mobile).orElse(null);
        if (owner == null) {
            return new DtoApiResponse("error", "Invalid user", null);
        }

        List<Shops> shops = shopRepo.findByOwner(owner);
        if (shops.isEmpty()) {
            return new DtoApiResponse("success", "You have no shops", null);
        }

        List<Map<String, Object>> shopList = new ArrayList<>();
        for (Shops shop : shops) {
            int pending = shop.getTotalToken() - shop.getCurrentToken();
            int waitTime = pending * shop.getTimePerCustomer();

            Map<String, Object> shopData = new HashMap<>();
            shopData.put("id", shop.getId());
            shopData.put("shopName", shop.getShopName());
            shopData.put("shopType", shop.getShopType());
            shopData.put("address", shop.getAddress());
            shopData.put("currentToken", shop.getCurrentToken());
            shopData.put("totalToken", shop.getTotalToken());
            shopData.put("timePerCustomer", shop.getTimePerCustomer());
            shopData.put("estimatedWaitTime", waitTime);
            shopData.put("closeTime",shop.getCloseTime());

            shopList.add(shopData);
        }
        return new DtoApiResponse("success", "Your shops", shopList);
    }


    public DtoApiResponse getShop(String mobile, Long id) {
        Users user = repo.findByMobile(mobile).orElse(null);
        if (user == null) {
            return new DtoApiResponse("error", "Invalid user", null);
        }

        Shops shop = shopRepo.findById(id).orElse(null);
        if (shop == null) {
            return new DtoApiResponse("error", "Shop not found", null);
        }

        int pending = shop.getTotalToken() - shop.getCurrentToken();
        int waitTime = pending * shop.getTimePerCustomer();

        Map<String, Object> shopData = new HashMap<>();
        shopData.put("id", shop.getId());
        shopData.put("shopName", shop.getShopName());
        shopData.put("shopType", shop.getShopType());
        shopData.put("address", shop.getAddress());
        shopData.put("currentToken", shop.getCurrentToken());
        shopData.put("totalToken", shop.getTotalToken());
        shopData.put("timePerCustomer", shop.getTimePerCustomer());
        shopData.put("estimatedWaitTime", waitTime);
        shopData.put("closeTime",shop.getCloseTime());

        return new DtoApiResponse("success", "Shop found", shopData);
    }
    public DtoApiResponse completeToken(String mobile, Long shopId) {
        Users owner = repo.findByMobile(mobile).orElse(null);
        if (owner == null) {
            return new DtoApiResponse("error", "Invalid user", null);
        }

        Shops shop = shopRepo.findById(shopId).orElse(null);
        if (shop == null) {
            return new DtoApiResponse("error", "Shop not found", null);
        }

        if (!shop.getOwner().getId().equals(owner.getId())) {
            return new DtoApiResponse("error", "You are not the owner of this shop", null);
        }


        if (shop.getCurrentToken() > shop.getTotalToken()) {
            return new DtoApiResponse("error", "No pending tokens to complete", null);
        }


        shop.setCurrentToken(shop.getCurrentToken() + 1);
        shopRepo.save(shop);

        int pending = shop.getTotalToken() - shop.getCurrentToken();
        int waitTime = pending * shop.getTimePerCustomer();
        if (waitTime < 0) waitTime = 0;

        Map<String, Object> data = new HashMap<>();
        data.put("shopName", shop.getShopName());
        data.put("currentToken", shop.getCurrentToken());
        data.put("nextTokenToComplete", shop.getCurrentToken());
        data.put("pendingTokens", pending);
        data.put("estimatedWaitTime", waitTime);
        data.put("closeTime",shop.getCloseTime());

        return new DtoApiResponse("success", "Token marked as completed", data);
    }

}
