package com.example.com.asif.databseCrud;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class StudentTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int roll;
   private String name;

   public StudentTable(){}
   public StudentTable(String name){
        this.name=name;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
