package com.asif.token.Controller;

import com.asif.token.Dto.DtoApiResponse;
import com.asif.token.Dto.DtoLoginRequest;
import com.asif.token.Entity.Shops;
import com.asif.token.Entity.Users;
import com.asif.token.Repository.TokenRepository;
import com.asif.token.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/Token")
public class TokenController {
    @Autowired
    private TokenRepository repo;

    @Autowired
    private  TokenService service;

    @PostMapping("/signup")
    public DtoApiResponse signup(@RequestBody Users user){
        return service.signup(user);

    }
    @PostMapping("/login")
    public DtoApiResponse login(@RequestBody DtoLoginRequest request){
        return service.login(request.mobile,request.password);
    }

    @PostMapping("/createShop")
    public DtoApiResponse createShop(@RequestHeader("Authorization") String token, @RequestBody Shops shop){
        token=token.replace("Bearer" ,"").trim();
        return service.createShop(token,shop);
    }

    @PostMapping("/takeToken/{shopId}")
    public DtoApiResponse takeToken(@RequestHeader("Authorization") String token, @PathVariable Long shopId){
        token=token.replace("Bearer" , "").trim();
        return service.takeToken(token,shopId);
    }

    @GetMapping("/getAllShops")
    public DtoApiResponse getAllShops(@RequestHeader("AUthorization") String token){
        token=token.replace("Bearer" , "").trim();
        return  service.getAllShops(token);
    }

    @GetMapping("/getShop/{id}")
    public DtoApiResponse getShop( @RequestHeader ("Authorization") String token, @PathVariable Long id){
        token=token.replace("Bearer ", "").trim();
        return service.getShop(token,id);
    }

}
