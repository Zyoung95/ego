package com.ego.item.controller;


import com.ego.commom.PageResult;
import com.ego.item.pojo.Brand;
import com.ego.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brand")
public class BrandCtrl {

    @Autowired
    private BrandService brandService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> page(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
            @RequestParam(value = "descending",defaultValue = "true") Boolean descending,
            @RequestParam(value = "sort") String sortBy,
            @RequestParam(value = "key") String key
    ){
        PageResult<Brand> result = brandService.page(page,pageSize,descending,sortBy,key);
        if(result==null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
