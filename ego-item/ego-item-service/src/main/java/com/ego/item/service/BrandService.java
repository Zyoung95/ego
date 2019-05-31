package com.ego.item.service;

import com.ego.commom.PageResult;
import com.ego.item.pojo.Brand;

import java.util.List;

public interface BrandService {
    PageResult<Brand> page(Integer page, Integer pageSize, Boolean descending, String sortBy, String key);

    void save(Brand brand, List<Long> cids);

    void deleteById(Long id);

    void update(Brand brand, List<Long> cids);

    Brand getById(Long brandId);

    List<Brand> ListByCid(Long cid);
}
