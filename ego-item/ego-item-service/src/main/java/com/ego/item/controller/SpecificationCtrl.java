package com.ego.item.controller;

import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Specification;
import com.ego.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecificationCtrl {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable("id") Long id){
        Specification specification = specificationService.findById(id);
        if(specification==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specification.getSpecifications());
    }

    @PostMapping
    public ResponseEntity<Void> save(Specification specification){
        specificationService.save(specification);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(Specification specification){
        specificationService.update(specification);
        return ResponseEntity.ok().build();
    }


}
