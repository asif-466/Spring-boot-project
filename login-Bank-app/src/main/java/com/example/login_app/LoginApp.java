package com.example.login_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginApp {
    private final Reposit repo;

    @Autowired
    public LoginApp(Reposit repo) {
        this.repo = repo;
    }

    public String signup(UserTable userTable) {
        if (userTable.getMobile() == null || userTable.getMobile().length() != 10) {
            return "Invalid Mobile Number";
        }
        if (repo.findByMobile(userTable.getMobile()) != null) {
            return "User already registered!";
        }
        userTable.setBalance(0.0);
        repo.save(userTable);
        return "Signup Successfully " + userTable.getName();
    }

    public String login(String mobile, String password) {
        UserTable userTable = repo.findByMobile(mobile);
        if (userTable == null) {
            return "User Not Found";
        }
        if (userTable.getPassword().equals(password)) {
            return "LOGIN SUCCESSFUL: " + userTable.getName();
        } else {
            return "INVALID PASSWORD:";
        }
    }

    public String balance(String mobile) {
        UserTable userTable = repo.findByMobile(mobile);
        if (userTable == null) {
            return "user not found";
        }
        return "Balance: " + userTable.getBalance();
    }

    public String deposit(String mobile, double amount) {
        UserTable userTable = repo.findByMobile(mobile);
        if (userTable == null) {
            return "user not found";
        }
        double bal = userTable.getBalance() + amount;
        userTable.setBalance(bal);
        repo.save(userTable);
        return "Deposited: " + amount + " | New Balance: " + bal;
    }

    public String withdraw(String mobile, double amount) {
        UserTable userTable = repo.findByMobile(mobile);
        if (userTable == null) {
            return "user not found";
        }
        double bal = userTable.getBalance();
        if (bal < amount) {
            return "low balance";
        }
        bal -= amount;
        userTable.setBalance(bal);
        repo.save(userTable);
        return "Withdrawn: " + amount + " | New Balance: " + bal;
    }

    public String send(String senderMobile, String receiverMobile, double amount) {
        UserTable sender = repo.findByMobile(senderMobile);
        UserTable receiver = repo.findByMobile(receiverMobile);

        if (sender == null || receiver == null) return "User Not Found!";
        if (sender.getBalance() < amount) return "low Balance!";

        sender.setBalance(sender.getBalance() - amount);
        repo.save(sender);

        receiver.setBalance(receiver.getBalance() + amount);
        repo.save(receiver);

        return "Sent " + amount + " to " + receiver.getName();
    }

}