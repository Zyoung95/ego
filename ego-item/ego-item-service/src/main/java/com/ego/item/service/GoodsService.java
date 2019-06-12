package com.ego.item.service;

import com.ego.commom.pojo.PageResult;
import com.ego.item.BO.SpuBO;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.SpuDetail;

import java.util.List;


public interface GoodsService {
    PageResult<SpuBO> page(String key, Boolean saleable, Integer page, Integer rows);

    void save(SpuBO spuBO);

    SpuDetail findDetailById(Long id);

    void update(SpuBO spuBO);

    List<Sku> getSkuById(Long id);

    void deleteById(Long id);

    SpuBO queryGoodsById(Long spuId);
}
