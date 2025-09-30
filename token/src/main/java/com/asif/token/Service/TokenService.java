package com.asif.token.Service;

import com.asif.token.Dto.DtoApiResponse;
import com.asif.token.Entity.Shops;
import com.asif.token.Entity.Users;
import com.asif.token.JwtUtil.TokenUtil;
import com.asif.token.Repository.ShopRepository;
import com.asif.token.Repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
    public class TokenService {
        @Autowired
        public TokenRepository repo;

        @Autowired
        public ShopRepository shopRepo;

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
            return new DtoApiResponse("success", null, token);
        }

        public DtoApiResponse createShop(String token, Shops shop){
            String mobile= jwtUtil.extractMobile(token);
            Users owner=repo.findByMobile(mobile).orElse(null);
            if(owner==null || !"OWNER".equals(owner.getRole())){
                return new DtoApiResponse("error","only owner can create shop",null);
            }
            shop.setOWNER(owner);
           shopRepo.save(shop);
            return new DtoApiResponse("success","shop create successful", shop.getShopName());
        }

        public DtoApiResponse takeToken(String token, Long shopId){
            String mobile= jwtUtil.extractMobile(token);
            Users user=repo.findByMobile(mobile).orElse(null);
            if(user==null || (!"CUSTOMER".equals(user.getRole()) && !"OWNER".equals(user.getRole()))) {
                return new DtoApiResponse("error","only customer and shopOwner can take token",null);
            }
            Shops shop=shopRepo.findById(shopId).orElse(null);
            if(shop == null){
                return new DtoApiResponse("error","shop not found",null);
            }

            int newToken=shop.getCurrentToken()+1;
            shop.setCurrentToken(newToken);
            shopRepo.save(shop);

            int waitTime=(newToken -1) * shop.getTimePerCustomer();
            Map<String,Object> tokenData=new HashMap<>();
            tokenData.put("tokenNo",newToken);
            tokenData.put("estimatedWaitTime",waitTime);
            return new DtoApiResponse("success","token issued successful",tokenData);
        }

        public DtoApiResponse getAllShops(String token){
            String mobile= jwtUtil.extractMobile(token);
            Users user=repo.findByMobile(mobile).orElse(null);
            if(user==null || (!"CUSTOMER".equals(user.getRole()) && !"OWNER".equals(user.getRole()))) {
                return new DtoApiResponse("error","only customer and shopOwner can see shop",null);
            }
            List<Shops> shops=shopRepo.findAll();
            return new DtoApiResponse("success","list of shops",shops);
        }

        public DtoApiResponse getShop(String token,Long id){
            String mobile= jwtUtil.extractMobile(token);
            Users user=repo.findByMobile(mobile).orElse(null);
            if(user==null || (!"CUSTOMER".equals(user.getRole()) && !"OWNER".equals(user.getRole()))) {
                return new DtoApiResponse("error","only customer and shopOwner can get shop",null);
            }
            Shops shop=shopRepo.findById(id).orElse(null);
            if(shop==null){
                return new DtoApiResponse("error","shop not found",null);
            }
            return new DtoApiResponse("success","shop found", shop);
        }

    }




