package com.example.login_app.Reposite;

import com.example.login_app.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionReposit extends JpaRepository<Transaction,Long> {
    List<Transaction> findByMobile(String mobile);
}
