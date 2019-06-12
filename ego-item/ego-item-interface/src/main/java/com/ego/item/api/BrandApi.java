package com.ego.item.api;

import com.ego.commom.pojo.PageResult;
import com.ego.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.CacheRequest;
import java.util.List;

@RequestMapping("/brand")
public interface BrandApi {
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> page(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
            @RequestParam(value = "descending",defaultValue = "true") Boolean descending,
            @RequestParam(value = "sort") String sortBy,
            @RequestParam(value = "key") String key
    );

    @PostMapping
    public ResponseEntity<Void> save(Brand brand, @RequestParam("cids") List<Long> cids);

    @PutMapping
    public ResponseEntity<Void> update(Brand brand, @RequestParam("cids")List<Long> cids);

    @DeleteMapping
    public ResponseEntity<Void> deleteById(@RequestParam("id") Long id);

    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> ListByCid(@PathVariable("cid") Long cid);

    @GetMapping("/bname")
    public ResponseEntity<String> getNameByBid(@RequestParam("bid") Long bid);

    @GetMapping("/bid/{bids}")
    public ResponseEntity<List<Brand>> queryListByIds(@RequestParam("bids") List<Long> bids);

    @GetMapping("/brand")
    public ResponseEntity<Brand> getBrandByBid(@RequestParam("bid") Long bid);
}
