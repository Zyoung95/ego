package com.ego.item.controller;


import com.ego.item.pojo.Category;
import com.ego.item.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryCtrl {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getListByParentId(@RequestParam("pid") Long parentId){
        List<Category> list = categoryService.getCategory(parentId);
        if(list==null||list.size()==0){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/bid/{id}")
    public ResponseEntity<List<Category>> findById(@PathVariable("id") Long id){
        List<Category> list = categoryService.findById(id);
        if(list==null||list.size()==0){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCategory(@RequestBody Category category){
        //System.out.println(category.getName()+category.getParentId());
        int num = categoryService.save(category);
        /*if(num==0){
            ResponseEntity.
        }*/
        return ResponseEntity.ok("添加成功");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteById(@RequestParam Long id){
        int num = categoryService.deleteById(id);

        return ResponseEntity.ok("删除成功");
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestParam Long id,@RequestParam String name){
        int num = categoryService.update(id,name);

        return ResponseEntity.ok("更新成功");
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("cids") List<Long> cids){
        List<Category> list = categoryService.queryNameByIds(cids);
        List names = new ArrayList();
        list.forEach(category -> {
            names.add(category.getName());
        });
        if(list==null||list.size()==0){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
    @GetMapping("/category")
    public ResponseEntity<List<Category>>  queryCategoryByIds(@RequestParam("cids") List<Long> cids){
        List<Category> list = categoryService.queryNameByIds(cids);
        if(list==null||list.size()==0){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

}
