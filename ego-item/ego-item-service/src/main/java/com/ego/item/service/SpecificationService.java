package com.ego.item.service;

import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Specification;

import java.util.List;

public interface SpecificationService {
    Specification findById(Long id);

    void save(Specification specification);

    void update(Specification specification);


}
