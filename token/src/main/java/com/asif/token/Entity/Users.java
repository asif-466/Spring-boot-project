package com.asif.token.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TokenUser")
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false)
    private String password;


}
