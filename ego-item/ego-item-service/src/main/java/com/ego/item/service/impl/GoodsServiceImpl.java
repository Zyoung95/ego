package com.ego.item.service.impl;

import com.ego.commom.PageResult;
import com.ego.item.BO.SpuBO;
import com.ego.item.mapper.SkuMapper;
import com.ego.item.mapper.SpuDetailMapper;
import com.ego.item.mapper.SpuMapper;
import com.ego.item.mapper.StockMapper;
import com.ego.item.pojo.*;
import com.ego.item.service.BrandService;
import com.ego.item.service.CategoryService;
import com.ego.item.service.GoodsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Transactional(readOnly = true)
    @Override
    public PageResult<SpuBO> page(String key, Boolean saleable, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%").orLike("subTitle","%"+key+"%");
        }
        if(saleable!=null){
            criteria.orEqualTo("saleable",saleable);
        }
        Page<Spu> pageInfo = (Page<Spu>) spuMapper.selectByExample(example);

        List<SpuBO> spuBOList = pageInfo.getResult().stream().map(spu -> {
            SpuBO spuBO = new SpuBO();
            //属性拷贝
            BeanUtils.copyProperties(spu,spuBO);
            List<Category> categoryList = categoryService.queryNameByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            List<String> names = categoryList.stream().map(category -> category.getName()).collect(Collectors.toList());
            spuBO.setCategoryName(StringUtils.join(names,"/"));
            Brand brand = brandService.getById(spu.getBrandId());
            spuBO.setBrandName(brand.getName());
            return spuBO;
        }).collect(Collectors.toList());

        return new PageResult<SpuBO>(pageInfo.getTotal(),spuBOList);
    }

    @Transactional
    @Override
    public void save(SpuBO spuBO) {
        spuBO.setSaleable(true);
        spuBO.setValid(true);
        spuBO.setCreateTime(new Date());
        spuBO.setLastUpdateTime(spuBO.getCreateTime());
        spuMapper.insert(spuBO);
        SpuDetail spuDetail = spuBO.getSpuDetail();
        spuDetail.setSpuId(spuBO.getId());
        spuDetailMapper.insertSelective(spuDetail);
        if(spuBO.getSkus()!=null){
            spuBO.getSkus().forEach(sku -> {
                sku.setSpuId(spuBO.getId());
                sku.setCreateTime(spuBO.getCreateTime());
                sku.setLastUpdateTime(spuBO.getCreateTime());
                skuMapper.insertSelective(sku);
                Stock stock = sku.getStock();
                stock.setSkuId(sku.getId());
                stockMapper.insertSelective(stock);
            });
        }
    }

    @Transactional
    @Override
    public SpuDetail findDetailById(Long id) {
        return spuDetailMapper.selectByPrimaryKey(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sku> getSkuById(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        List list = new ArrayList();
        if(skus!=null){
            for (Sku sku1 : skus) {
                Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());
                sku1.setStock(stock);
                list.add(sku1);
            }
        }
        return list;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        spuDetailMapper.deleteByPrimaryKey(id);
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        if(sku!=null){
            for (Sku sku1 : skus) {
                Long skuId = sku1.getId();
                stockMapper.deleteByPrimaryKey(skuId);
                skuMapper.deleteByPrimaryKey(skuId);
            }
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void update(SpuBO spuBO) {
        Date date = new Date();
        spuBO.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuBO);
        spuDetailMapper.updateByPrimaryKeySelective(spuBO.getSpuDetail());
        List<Sku> skus = spuBO.getSkus();
        if(skus!=null){
            for (Sku sku : skus) {
                if(sku.getId()==null){
                    sku.setSpuId(spuBO.getId());
                    sku.setCreateTime(date);
                    sku.setLastUpdateTime(date);
                    skuMapper.insertSelective(sku);
                    Stock stock = sku.getStock();
                    stock.setSkuId(sku.getId());
                    stockMapper.insertSelective(stock);
                }else{
                    sku.setSpuId(spuBO.getId());
                    sku.setLastUpdateTime(date);
                    skuMapper.updateByPrimaryKeySelective(sku);
                    Stock stock = sku.getStock();
                    stock.setSkuId(sku.getId());
                    stockMapper.updateByPrimaryKeySelective(stock);
                }
            }
        }
    }
}
