package com.ego.item.service;

import com.ego.item.pojo.Category;

import java.util.List;

public interface CategoryService {
    List getCategory(Long parentId);

    int save(Category category);

    int deleteById(Long id);

    //int update(Category category);

    int update(Long id, String name);
}
