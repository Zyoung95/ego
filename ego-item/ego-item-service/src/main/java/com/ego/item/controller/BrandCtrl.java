package com.ego.item.controller;


import com.ego.commom.pojo.PageResult;
import com.ego.item.pojo.Brand;
import com.ego.item.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<Void> save(Brand brand, @RequestParam("cids")List<Long> cids){
        brandService.save(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> update(Brand brand, @RequestParam("cids")List<Long> cids){
        brandService.update(brand,cids);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteById(@RequestParam("id") Long id){
        brandService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> ListByCid(@PathVariable("cid") Long cid){
        List<Brand> list = brandService.ListByCid(cid);
        if(list==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
    @GetMapping("/bname")
    public ResponseEntity<String> getNameByBid(@RequestParam("bid") Long bid){
        Brand brand = brandService.getById(bid);
        String bname = brand.getName();
        if(StringUtils.isBlank(bname)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bname);
    }
    @GetMapping("/bid/{bids}")
    public ResponseEntity<List<Brand>> queryListByIds(@RequestParam("bids") List<Long> bids){
        List<Brand> list = brandService.queryListByIds(bids);
        if(list==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
    @GetMapping("/brand")
    public ResponseEntity<Brand> getBrandByBid(@RequestParam("bid") Long bid){
        Brand brand = brandService.getById(bid);
        if(brand==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }
}
