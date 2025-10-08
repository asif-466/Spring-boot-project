package com.asif.token.Controller;

import com.asif.token.Dto.DtoApiResponse;
import com.asif.token.Dto.DtoLoginRequest;
import com.asif.token.Entity.Shops;
import com.asif.token.Entity.Users;
import com.asif.token.Repository.TokenRepository;
import com.asif.token.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/Token")
public class TokenController {

    @Autowired
    private TokenRepository repo;

    @Autowired
    private TokenService service;


    @PostMapping("/signup")
    public DtoApiResponse signup(@RequestBody Users user) {
        return service.signup(user);
    }


    @PostMapping("/login")
    public DtoApiResponse login(@RequestBody DtoLoginRequest request) {
        return service.login(request.mobile, request.password);
    }


    @PostMapping("/createShop")
    public DtoApiResponse createShop(Authentication authentication, @RequestBody Shops shop) {
        String mobile = authentication.getName();
        return service.createShop(mobile, shop);
    }


    @PostMapping("/takeToken/{shopId}")
    public DtoApiResponse takeToken(Authentication authentication, @PathVariable Long shopId) {
        String mobile = authentication.getName();
        return service.takeToken(mobile, shopId);
    }


    @GetMapping("/getAllShops")
    public DtoApiResponse getAllShops(Authentication authentication) {
        String mobile = authentication.getName();
        return service.getAllShops(mobile);
    }


    @GetMapping("/getShop/{id}")
    public DtoApiResponse getShop(Authentication authentication, @PathVariable Long id) {
        String mobile = authentication.getName();
        return service.getShop(mobile, id);
    }


    @GetMapping("/showMyTokens")
    public DtoApiResponse showMyTokens(Authentication authentication) {
        String mobile = authentication.getName();
        return service.showMyTokens(mobile);
    }


    @GetMapping("/myShops")
    public DtoApiResponse myShops(Authentication authentication) {
        String mobile = authentication.getName();
        return service.myShops(mobile);
    }

    @PostMapping("/completeToken/{shopId}")
    public DtoApiResponse completeToken(Authentication authentication, @PathVariable Long shopId) {
        String mobile = authentication.getName();
        return service.completeToken(mobile, shopId);
    }

}
