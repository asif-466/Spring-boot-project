package com.example.login_app.Controller;

import com.example.login_app.DtoClass.DtoApiResponse;
import com.example.login_app.DtoClass.DtoLoginRequest;
import com.example.login_app.Service.LoginApp;
import com.example.login_app.Entity.UserTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/Bank")
public class Controller {

    @Autowired
    public LoginApp service;

    @PostMapping("/signup")
    public DtoApiResponse signup(@RequestBody UserTable user) {
        return service.signup(user);
    }

    @PostMapping("/login")
    public DtoApiResponse login(@RequestBody DtoLoginRequest request) {
        return service.login(request.mobile, request.password);
    }

    @GetMapping("/balance")
    public DtoApiResponse balance(Authentication authentication) {
        String mobile=authentication.getName();
        return service.balance(mobile);
    }

    @PutMapping("/deposit/{amount}")
    public DtoApiResponse deposit(Authentication authentication, @PathVariable double amount) {
        String mobile=authentication.getName();
        return service.deposit(mobile, amount);
    }

    @PutMapping("/withdraw/{amount}")
    public DtoApiResponse withdraw(Authentication authentication, @PathVariable double amount) {
        String mobile=authentication.getName();
        return service.withdraw(mobile, amount);
    }

    @PutMapping("/send/{receiver}/{amount}")
    public DtoApiResponse send(Authentication authentication, @PathVariable String receiver, @PathVariable double amount) {
        String mobile=authentication.getName();
        return service.send(mobile, receiver, amount);
    }

    @DeleteMapping("/delete")
    public DtoApiResponse delete(Authentication authentication) {
        String mobile=authentication.getName();
        return service.delete(mobile);
    }

    @GetMapping("/history")
    public DtoApiResponse History(Authentication authentication){
        String mobile=authentication.getName();
        return  service.History(mobile);
    }

    @GetMapping("/richUser")
    public List<UserTable> richUser() {
        return service.richUser();
    }

    @GetMapping("/poorUser")
    public List<UserTable> poorUser() {
        return service.poorUser();
    }
}