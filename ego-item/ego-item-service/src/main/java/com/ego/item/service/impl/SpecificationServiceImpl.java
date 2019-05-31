package com.ego.item.service.impl;

import com.ego.item.mapper.SpecificationMapper;
import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Specification;
import com.ego.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    public SpecificationMapper specificationMapper;

    @Transactional(readOnly = true)
    @Override
    public Specification findById(Long id) {
        return specificationMapper.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void save(Specification specification) {
        specificationMapper.insertSelective(specification);
    }

    @Transactional
    @Override
    public void update(Specification specification) {
        specificationMapper.updateByPrimaryKeySelective(specification);
    }


}
