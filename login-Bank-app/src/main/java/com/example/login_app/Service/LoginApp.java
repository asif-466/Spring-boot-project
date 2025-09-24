package com.example.login_app.Service;

import com.example.login_app.DtoClass.DtoSignupRequest;
import com.example.login_app.Util.JwtUtil;
import com.example.login_app.Reposite.Reposit;
import com.example.login_app.Entity.UserTable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginApp {
    @Autowired
    public Reposit repo;

    @Autowired
    public JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    public DtoSignupRequest signup(UserTable obj) {
        if (obj.getMobile() == null || obj.getMobile().length() != 10) {
            throw new RuntimeException("INVALID NUMBER");
        }
        if (repo.findById(obj.getMobile()).isPresent()) {
            throw new RuntimeException("USER ALREADY REGISTERED");
        }
        obj.setBalance(0.0);
        obj.setPassword(passwordEncoder.encode(obj.getPassword()));
        repo.save(obj);
        return new DtoSignupRequest(obj.getName(),obj.getMobile(),obj.getBalance());

    }

    public String login(String mobile, String password) {
        UserTable user = repo.findById(mobile).orElse(null);
        if (user == null) {
            return "INVALID MOBILE NUMBER";
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "INVALID PASSWORD ";
        }
        String token = jwtUtil.generateToken(mobile);
        return "Login Successful! TOKEN: " + token;

    }

    public String balance(String token) {
        if(!jwtUtil.validateToken(token)){
            return "INVALID OR EXPIRE TOKEN";
        }
        String mobile = jwtUtil.extractMobile(token);
        Double balance = repo.balance(mobile);
        return "BALANCE: " + balance;
    }


    @Transactional
    public String deposit(String token, double amount) {
        if(!jwtUtil.validateToken(token)){
            return "INVALID OR EXPIRE TOKEN";
        }
        String mobile = jwtUtil.extractMobile(token);
        int row = repo.deposit(mobile, amount);
        if (row > 0) {
            Double balance = repo.balance(mobile);
            return "Deposited: " + amount + " | New Balance: " + balance;
        } else {
            return "user not found";
        }

    }


    @Transactional
    public String withdraw(String token, double amount) {
        if(!jwtUtil.validateToken(token)){
            return "INVALID OR EXPIRE TOKEN";
        }
        String mobile = jwtUtil.extractMobile(token);
        Double balance = repo.balance(mobile);
        if (balance == null) {
            return "user not found";
        }
        if (amount > balance) {
            return "low balance! Available  " + balance;
        }
        int row = repo.withdraw(mobile, amount);
        if (row > 0) {
            Double new_balance = repo.balance(mobile);
            return "Withdrawal: " + amount + " | New Balance: " + new_balance;
        } else {
            return "withdrawal failed";
        }
    }

    @Transactional
    public String send(String token, String receiverMobile, double amount) {
        if(!jwtUtil.validateToken(token)){
            return "INVALID OR EXPIRE TOKEN";
        }
        String senderMobile = jwtUtil.extractMobile(token);
        Double senderbalance = repo.balance(senderMobile);
        Double receverBalance = repo.balance(receiverMobile);
        if (senderbalance == null || receverBalance == null) {
            return "user not found";
        }
        if (amount > senderbalance) {
            return "low balance!Available" + senderbalance;
        }
        repo.withdraw(senderMobile, amount);
        repo.deposit(receiverMobile, amount);

        Double newsenderbalance = repo.balance(senderMobile);
        Double newreceverbalance = repo.balance(receiverMobile);

        String sendername = repo.name(senderMobile);
        String recevername = repo.name(receiverMobile);

        return "sent " + amount + " from " + sendername + " to " + recevername;
    }

    public String delete(String token) {
        if(!jwtUtil.validateToken(token)){
            return "INVALID OR EXPIRE TOKEN";
        }
        String mobile = jwtUtil.extractMobile(token);
        UserTable obj = repo.findById(mobile).orElse(null);
        if (obj == null) {
            return "user not found";
        } else {
            repo.delete(obj);
            return "user deleted " + obj.getName();
        }
    }

    public List<UserTable> richUser() {
        return repo.richUser();
    }

    public List<UserTable> poorUser() {
        return repo.poorUser();
    }
}