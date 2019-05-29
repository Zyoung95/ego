package com.ego.item.service.impl;

import com.ego.item.service.CategoryService;
import com.ego.item.mapper.CategoryMapper;
import com.ego.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl  implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List getCategory(Long parentId) {
        Category category = new Category();
        category.setParentId(parentId);
        List<Category> list = categoryMapper.select(category);
        return list;
    }

    @Transactional
    @Override
    public int save(Category category) {
        return categoryMapper.insert(category);
    }

    @Transactional
    @Override
    public int deleteById(Long id) {
        Category category = new Category();
        category.setId(id);
        int num = categoryMapper.delete(category);
        /*category = categoryMapper.selectOne(category);
        int num = categoryMapper.deleteByPrimaryKey(id);*/

        return num;
    }

    @Transactional
    @Override
    public int update(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category = categoryMapper.selectOne(category);
        category.setName(name);
        int num = categoryMapper.updateByPrimaryKey(category);
        //int num = categoryMapper.updateByPrimaryKeySelective(category);
        return num;
    }

    /*@Transactional
    @Override
    public int update(Category category) {
        int num = categoryMapper.updateByPrimaryKey(category);
        return num;
    }*/
}
