package com.asif.token.Repository;

import com.asif.token.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Users,Long> {
    boolean existsByMobile(String mobile);
        Optional<Users> findByMobile(String mobile);
}
