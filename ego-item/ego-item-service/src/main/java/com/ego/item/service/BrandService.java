package com.ego.item.service;

import com.ego.commom.PageResult;
import com.ego.item.pojo.Brand;

public interface BrandService {
    PageResult<Brand> page(Integer page, Integer pageSize, Boolean descending, String sortBy, String key);

}
