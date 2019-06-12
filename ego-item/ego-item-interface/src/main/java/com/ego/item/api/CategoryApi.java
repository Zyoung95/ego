package com.ego.item.api;

import com.ego.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.CacheRequest;
import java.util.List;

@RequestMapping("/category")
public interface CategoryApi {
    @GetMapping("/list")
    public ResponseEntity<List<Category>> getListByParentId(@RequestParam("pid") Long parentId);

    @GetMapping("/bid/{id}")
    public ResponseEntity<List<Category>> findById(@PathVariable("id") Long id);

    @PostMapping("/add")
    public ResponseEntity<String> addCategory(@RequestBody Category category);

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteById(@RequestParam Long id);

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestParam Long id,@RequestParam String name);
    @GetMapping("/names")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("cids") List<Long> cids);

    @GetMapping("/category")
    public ResponseEntity<List<Category>>  queryCategoryByIds(@RequestParam("cids") List<Long> cids);
}
