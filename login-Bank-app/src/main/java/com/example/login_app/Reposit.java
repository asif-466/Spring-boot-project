package com.example.login_app;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Reposit extends JpaRepository<UserTable,String> {
     UserTable findByMobile(String mobile);
}
