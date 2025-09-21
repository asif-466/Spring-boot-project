package com.example.com.asif.databseCrud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stu")
public class StudentController {
    @Autowired
    public StudentService service;

    @PostMapping
    public StudentTable create(@RequestBody StudentTable obj) {
        return service.create(obj);
    }

    @GetMapping
    public List<StudentTable> getall() {
        return service.getall();
    }

    @PutMapping("/{roll}")
    public StudentTable update(@PathVariable int roll,@RequestBody StudentTable obj) {
        obj.setRoll(roll);
        return service.update(obj);
    }

    @DeleteMapping("/{roll}")
    public String delete(@PathVariable int roll) {
        return service.delete(roll);
    }

    @GetMapping("/{roll}")
    public StudentTable readbyroll(@PathVariable int roll) {
        return service.readbyroll(roll);
    }

}
