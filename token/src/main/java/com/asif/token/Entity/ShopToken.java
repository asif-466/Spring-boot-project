package com.asif.token.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shops shop;

    @Column(nullable = false)
    private int tokenNo;

    @Column(nullable = false)
    private int estimatedWaitTime;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


}
