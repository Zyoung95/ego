package com.ego.search.controller;


import com.ego.commom.pojo.PageResult;
import com.ego.search.bo.SearchRequest;
import com.ego.search.pojo.Goods;
import com.ego.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SearchCtrl {

    @Autowired
    private SearchService searchService;

    @PostMapping("/page")
    public ResponseEntity<PageResult<Goods>> page(@RequestBody SearchRequest searchRequest)
    {
        PageResult<Goods> result = searchService.search(searchRequest);
        if(result==null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
