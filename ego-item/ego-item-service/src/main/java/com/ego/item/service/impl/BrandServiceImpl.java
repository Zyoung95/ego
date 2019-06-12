package com.ego.item.service.impl;

import com.ego.commom.pojo.PageResult;
import com.ego.item.mapper.BrandMapper;
import com.ego.item.pojo.Brand;
import com.ego.item.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Transactional(readOnly = true)
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

    @Transactional
    @Override
    public void save(Brand brand, List<Long> cids) {
        brandMapper.insert(brand);
        if(cids!=null){
            for (Long cid : cids) {
                brandMapper.save(brand.getId(),cid);
            }
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        /*Brand brand = new Brand();
        brand.setId(id);
        brandMapper.delete(brand);*/
        brandMapper.deleteByPrimaryKey(id);
        brandMapper.deleteCategoryBrandById(id);
    }

    @Transactional
    @Override
    public void update(Brand brand, List<Long> cids) {
        brandMapper.updateByPrimaryKeySelective(brand);
        brandMapper.deleteCategoryBrandById(brand.getId());
        if(cids!=null){
            for (Long cid : cids) {
                brandMapper.save(brand.getId(),cid);
            }
        }
    }

    @Transactional
    @Override
    public Brand getById(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Brand> ListByCid(Long cid) {
        return brandMapper.ListByCid(cid);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Brand> queryListByIds(List<Long> bids) {
        return brandMapper.selectByIdList(bids);
    }
}
