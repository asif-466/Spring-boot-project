package com.asif.token.Repository;


import com.asif.token.Entity.Shops;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shops,Long> {
}
