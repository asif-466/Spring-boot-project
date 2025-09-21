package com.example.login_app;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Reposit extends JpaRepository<UserTable,String> {
    @Query(value = "select * from user_table where balance >=500", nativeQuery = true)
    List<UserTable> richUser();

    @Query(value = "select * from user_table where balance <=500", nativeQuery = true)
    List<UserTable> poorUser();


    @Query(value = "select balance from user_table where mobile=:mobile", nativeQuery = true)
    Double balance(@Param("mobile") String mobile);

    @Modifying
    @Transactional
    @Query (value = "update user_table set balance =balance + :amount where mobile=:mobile",nativeQuery = true)
    int deposit(@Param("mobile")String mobile,@Param("amount") Double amount);

    @Modifying
    @Transactional
    @Query (value = "update user_table set balance =balance - :amount where mobile=:mobile",nativeQuery = true)
    int withdraw(@Param("mobile")String mobile,@Param("amount") Double amount);

    @Query(value = "select name from user_table where mobile=:mobile",nativeQuery = true)
    String name(@Param("mobile")String mobile);




}
