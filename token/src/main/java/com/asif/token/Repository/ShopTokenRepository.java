package com.asif.token.Repository;

import com.asif.token.Entity.ShopToken;
import com.asif.token.Entity.Shops;
import com.asif.token.Entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopTokenRepository extends JpaRepository<ShopToken,Long> {

    Optional<ShopToken> findByUserAndShop(Users user, Shops shop);
    List<ShopToken> findByUser(Users user);
    List<ShopToken> findByShop(Shops shop);
}
