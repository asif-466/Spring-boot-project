package com.example.login_app.Controller;

import com.example.login_app.DtoClass.DtoLoginRequest;
import com.example.login_app.DtoClass.DtoSignupRequest;
import com.example.login_app.Service.LoginApp;
import com.example.login_app.Entity.UserTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Bank")
public class Controller {

    @Autowired
    public LoginApp service;

    @PostMapping("/signup")
    public DtoSignupRequest signup(@RequestBody UserTable obj) {
        return service.signup(obj);
    }

    @PostMapping("/login")
    public String login(@RequestBody DtoLoginRequest request) {
        return service.login(request.mobile, request.password);
    }

    @GetMapping("/balance")
    public String balance(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        return service.balance(token);
    }

    @PutMapping("/deposit/{amount}")
    public String deposit(@RequestHeader("Authorization") String token, @PathVariable double amount) {
        token = token.replace("Bearer ", "");
        return service.deposit(token, amount);
    }

    @PutMapping("/withdraw/{amount}")
    public String withdraw(@RequestHeader("Authorization") String token, @PathVariable double amount) {
        token = token.replace("Bearer ", "");
        return service.withdraw(token, amount);
    }

    @PutMapping("/send/{receiver}/{amount}")
    public String sendMoney(@RequestHeader("Authorization") String token, @PathVariable String receiver, @PathVariable double amount) {
        token = token.replace("Bearer ", "");
        return service.send(token, receiver, amount);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        return service.delete(token);
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