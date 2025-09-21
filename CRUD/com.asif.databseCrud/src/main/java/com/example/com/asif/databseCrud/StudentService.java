package com.example.com.asif.databseCrud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    @Autowired
    private Repository repo;

    public StudentTable create(StudentTable obj){
         return repo.save(obj);
    }

    public List<StudentTable> getall(){
        return repo.findAll();
    }

    public StudentTable update(StudentTable obj){
        if(repo.existsById(obj.getRoll())){
            return repo.save(obj);
        }
        return null;

    }
    public String delete(int roll){
        if(repo.existsById(roll)){
            repo.deleteById(roll);
            return "stu deleted";
        }
        return "stu not found";
    }

    public StudentTable readbyroll(int roll) {
         return repo.findById(roll).orElse(null);

        }
    }


