package com.example.login_app.Service;

import com.example.login_app.DtoClass.DtoApiResponse;
import com.example.login_app.Entity.Transaction;
import com.example.login_app.Reposite.TransactionReposit;
import com.example.login_app.Util.JwtUtil;
import com.example.login_app.Reposite.Reposit;
import com.example.login_app.Entity.UserTable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginApp {
    @Autowired
    public Reposit repo;

    @Autowired
    public  TransactionReposit trepo;

    @Autowired
    public JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;


    public DtoApiResponse signup(UserTable user) {
        if (user.getMobile() == null || user.getMobile().length() != 10) {
            return new DtoApiResponse("error","INVALID NUMBER",null);
        }
        if (repo.findById(user.getMobile()).isPresent()) {
            return new DtoApiResponse("error","USER ALREADY REGISTERED",null);
        }
        user.setBalance(0.0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserTable savedUser=repo.save(user);
        return new DtoApiResponse("success","SIGNUP SUCCESSFUL",savedUser.getName());

    }

    public DtoApiResponse login(String mobile, String password) {
        UserTable user = repo.findById(mobile).orElse(null);
        if (user == null) {
            return new DtoApiResponse("error","INVALID NUMBER",null);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new DtoApiResponse("error","INVALID PASSWORD",null);
        }
        String token = jwtUtil.generateToken(mobile);
        return new DtoApiResponse("success","LOGIN SUCCESSFUL",token);

    }

    public DtoApiResponse balance(String token) {
        if(!jwtUtil.validateToken(token)){
            return new DtoApiResponse("error","INVALID OR EXPIRE TOKEN",null);
        }
        String mobile = jwtUtil.extractMobile(token);
        Double balance = repo.balance(mobile);
        return new DtoApiResponse("success","BALANCE:",balance);

    }


    @Transactional
    public DtoApiResponse deposit(String token, double amount) {
        if(!jwtUtil.validateToken(token)){
            return new DtoApiResponse("error","INVALID OR EXPIRE TOKEN",null);
        }
        String mobile = jwtUtil.extractMobile(token);
        int row = repo.deposit(mobile, amount);
        if (row > 0) {
            Double balance = repo.balance(mobile);
            Transaction t=new Transaction();
            t.setMobile(mobile);
            t.setType("DEPOSIT");
            t.setAmount(amount);
            t.setTimestamp(LocalDateTime.now());
            trepo.save(t);
            return new DtoApiResponse("success","DEPOSITED NEW BALANCE:",balance);
        } else {
            return new DtoApiResponse("error","USER NOT FOUND",null);
        }

    }


    @Transactional
    public DtoApiResponse withdraw(String token, double amount) {
        if(!jwtUtil.validateToken(token)){
            return new DtoApiResponse("error","INVALID OR EXPIRE TOKEN",null);
        }
        String mobile = jwtUtil.extractMobile(token);
        Double balance = repo.balance(mobile);
        if (balance == null) {
            return new DtoApiResponse("error","USER NOT FOUND",null);
        }
        if (amount > balance) {
            return new DtoApiResponse("error","LOW CURRENT BALANCE:",balance);
        }
        int row = repo.withdraw(mobile, amount);
        if (row > 0) {
            Double new_balance = repo.balance(mobile);
            Transaction t=new Transaction();
            t.setMobile(mobile);
            t.setType("WITHDRAW");
            t.setAmount(amount);
            t.setTimestamp(LocalDateTime.now());
            trepo.save(t);
            return new DtoApiResponse("success","WITHDRAWAL NEW BALANCE",new_balance);
        } else {
            return new DtoApiResponse("error","WITHDRAW FAILED",null);
        }
    }

    @Transactional
    public DtoApiResponse send(String token, String receiverMobile, double amount) {
        if(!jwtUtil.validateToken(token)){
            return new DtoApiResponse("error","INVALID OR EXPIRE TOKEN",null);
        }
        String senderMobile = jwtUtil.extractMobile(token);
        Double senderbalance = repo.balance(senderMobile);
        Double receverBalance = repo.balance(receiverMobile);
        if (senderbalance == null || receverBalance == null) {
            return new DtoApiResponse("error","USER NOT FOUND",null);
        }
        if (amount > senderbalance) {
            return new DtoApiResponse("error","LOW CURRENT BALANCE",senderbalance);
        }
        repo.withdraw(senderMobile, amount);
        repo.deposit(receiverMobile, amount);

        Double newsenderbalance = repo.balance(senderMobile);
        Double newreceverbalance = repo.balance(receiverMobile);

        String sendername = repo.name(senderMobile);
        String recevername = repo.name(receiverMobile);
        Transaction t=new Transaction();
        t.setMobile(receiverMobile);
        t.setType("SENT");
        t.setAmount(amount);
        t.setReceiver(recevername);
        t.setTimestamp(LocalDateTime.now());
        trepo.save(t);

        return new DtoApiResponse("success","SENT SUCCESSFUL TO",recevername);
    }

    public DtoApiResponse delete(String token) {
        if(!jwtUtil.validateToken(token)){
            return new DtoApiResponse("error","INVALID OR EXPIRE TOKEN",null);
        }
        String mobile = jwtUtil.extractMobile(token);
        UserTable user = repo.findById(mobile).orElse(null);
        if (user == null) {
            return new DtoApiResponse("error","USER NOT FOUND",null);
        } else {
            repo.delete(user);
            return new DtoApiResponse("error","ACCOUNT DELETED",user.getName());
        }
    }

    public DtoApiResponse History(String token){
        if(!jwtUtil.validateToken(token)){
            return new DtoApiResponse("error","INVALID OR EXPIRE TOKEN",null);
        }
        String mobile= jwtUtil.extractMobile(token);
        List<Transaction> transactions=trepo.findByMobile(mobile);
        if(transactions.isEmpty()){
            return new DtoApiResponse("success","NO TRANSACTION FOUND",null);
        }
        return new DtoApiResponse("success","TRANSACTION HISTORY",transactions);
    }

    public List<UserTable> richUser() {
        return repo.richUser();
    }

    public List<UserTable> poorUser() {
        return repo.poorUser();
    }
}