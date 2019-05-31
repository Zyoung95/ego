package com.ego.item.service.impl;

import com.ego.item.service.CategoryService;
import com.ego.item.mapper.CategoryMapper;
import com.ego.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl  implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
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
        /*Category category = new Category();
        category.setId(id);
        int num = categoryMapper.delete(category);
        category = categoryMapper.selectOne(category);*/
        int num = categoryMapper.deleteByPrimaryKey(id);

        return num;
    }

    @Transactional
    @Override
    public int update(Long id, String name) {
        /*Category category = new Category();
        category.setId(id);*/
        Category category = categoryMapper.selectByPrimaryKey(id);
        //category = categoryMapper.selectOne(category);
        category.setName(name);
        int num = categoryMapper.updateByPrimaryKey(category);
        //int num = categoryMapper.updateByPrimaryKeySelective(category);
        return num;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> findById(Long id) {
        List<Category> list = new ArrayList();
        List<Long> cids = categoryMapper.selectCategoryById(id);
        for (Long cid : cids) {
            Category category = new Category();
            category.setId(cid);
            category = categoryMapper.selectOne(category);
            list.add(category);
        }
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> queryNameByIds(List<Long> cids) {
        List<Category> list = categoryMapper.selectByIdList(cids);
        return list;
    }

    /*@Transactional
    @Override
    public int update(Category category) {
        int num = categoryMapper.updateByPrimaryKey(category);
        return num;
    }*/
}
