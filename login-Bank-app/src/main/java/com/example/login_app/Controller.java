package com.example.login_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class Controller {

    public LoginApp loginApp;

    @Autowired
    public Controller(LoginApp loginApp) {
        this.loginApp = loginApp;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserTable userTable) {
        return loginApp.signup(userTable);
    }

    @GetMapping("/login/{mobile}/{password}")
    public String login(@PathVariable String mobile, @PathVariable String password) {
        return loginApp.login(mobile, password);
    }

    @GetMapping("/balance/{mobile}")
    public String balance(@PathVariable String mobile) {
        return loginApp.balance(mobile);
    }

    @GetMapping("/deposit/{mobile}/{amount}")
    public String deposit(@PathVariable String mobile, @PathVariable double amount) {
        return loginApp.deposit(mobile, amount);
    }

    @GetMapping("/withdraw/{mobile}/{amount}")
    public String withdraw(@PathVariable String mobile, @PathVariable double amount) {
        return loginApp.withdraw(mobile, amount);
    }
    @GetMapping("/send/{sender}/{receiver}/{amount}")
    public String sendMoney(@PathVariable String sender,@PathVariable String receiver,@PathVariable double amount) {
        return loginApp.send(sender, receiver, amount);
    }

}