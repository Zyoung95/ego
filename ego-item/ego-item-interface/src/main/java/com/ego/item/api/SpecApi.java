package com.ego.item.api;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/spec")
public interface SpecApi {

    @GetMapping("/{id}")
    public ResponseEntity<String> querySpecificationByCategoryId(@PathVariable("id") Long id);

}
