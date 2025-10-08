package com.asif.token.Repository;


import com.asif.token.Entity.Shops;
import com.asif.token.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shops,Long> {
    List<Shops> findByOwner(Users owner);
}
