package com.asif.token.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shops {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String shopType;

    @Column(nullable = false)
    private String shopName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int timePerCustomer;

    @Column(nullable = false, name = "open_time")
    private String openTime;

    @Column( nullable = false, name = "city")
    private String city;

    @Column(nullable = false, name = "close_time")
    private String closeTime;


    private int totalToken = 0;
    private int currentToken = 1;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Users owner;
}
