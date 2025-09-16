package com.example.HashmapData;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

@RestController
@RequestMapping("/emp")
public class Controller {

    private HashMap<Integer, Table> empDb = new HashMap<>();

    @PostMapping
    public String addemp(@RequestBody Table emp) {
        empDb.put(emp.getId(), emp);
        return "emp added";
    }

    @GetMapping
    public Collection<Table> getAllEmp() {
        return empDb.values();
    }

    @GetMapping("/{id}")
    public Table getEmp(@PathVariable int id) {
        return empDb.get(id);
    }

    @PutMapping("/{id}")
    public String update(@PathVariable int id, @RequestBody Table emp) {
        if (empDb.containsKey(id)) {
            empDb.put(id, emp);
            return "emp updated";
        } else {
            return "emp not found";

        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        if (empDb.containsKey(id)) {
            empDb.remove(id);
            return "emp deleted";
        } else {
            return "emp not found";
        }

    }
}
