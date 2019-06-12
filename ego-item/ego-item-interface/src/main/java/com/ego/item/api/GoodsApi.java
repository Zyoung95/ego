package com.ego.item.api;

import com.ego.commom.pojo.PageResult;
import com.ego.item.BO.SpuBO;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
public interface GoodsApi {

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBO>> page(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam("saleable") Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    );
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpuBO spuBO);

    /**
     * 根据spuId获取SpuDetail
     * @param id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> findDetailById(@PathVariable("id") Long id);

    /**
     * 根据spuId获取sku
     * @param id
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> getSkuById(@RequestParam Long id);
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody SpuBO spuBO);
    @DeleteMapping
    public ResponseEntity<Void> deleteById(@RequestParam Long id);
    @GetMapping("/spuBO/{spuId}")
    public ResponseEntity<SpuBO> queryGoodsById(@PathVariable("spuId") Long spuId);
}
