package com.ego.item.controller;

import com.ego.commom.pojo.PageResult;
import com.ego.item.BO.SpuBO;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.SpuDetail;
import com.ego.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsCtrl {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBO>> page(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam("saleable") Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){
        PageResult<SpuBO> list = goodsService.page(key, saleable, page, rows);
        if(list==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpuBO spuBO){
        goodsService.save(spuBO);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> findDetailById(@PathVariable("id") Long id){
        SpuDetail spuDetail = goodsService.findDetailById(id);
        if(spuDetail==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> getSkuById(@RequestParam Long id){
        List<Sku> list = goodsService.getSkuById(id);
        return ResponseEntity.ok(list);
    }
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody SpuBO spuBO){
        goodsService.update(spuBO);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteById(@RequestParam Long id){
        goodsService.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/spuBO/{spuId}")
    public ResponseEntity<SpuBO> queryGoodsById(@PathVariable("spuId") Long spuId){
        SpuBO spuBO = goodsService.queryGoodsById(spuId);
        if(spuBO==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuBO);
    }
}
