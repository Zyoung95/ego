package com.ego.item.service.impl;

import com.ego.commom.PageResult;
import com.ego.item.mapper.BrandMapper;
import com.ego.item.pojo.Brand;
import com.ego.item.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult<Brand> page(Integer page, Integer pageSize, Boolean descending, String sortBy, String key) {
        PageHelper.startPage(page,pageSize);
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNoneBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        if(StringUtils.isNoneBlank(sortBy)){
            //order by   注意添加空格
            example.setOrderByClause(sortBy+(descending?" desc":" asc"));
        }
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        return new PageResult<Brand>(pageInfo.getTotal(),pageInfo.getResult());
    }
}
