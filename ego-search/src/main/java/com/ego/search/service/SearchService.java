package com.ego.search.service;


import com.ego.commom.PageResult;
import com.ego.item.BO.SpuBO;
import com.ego.item.pojo.Sku;
import com.ego.search.bo.SearchRequest;
import com.ego.search.client.BrandClient;
import com.ego.search.client.CategoryClient;
import com.ego.search.client.GoodsClient;
import com.ego.search.dao.GoodsRepository;
import com.ego.search.pojo.Goods;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;


import java.util.*;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsRepository goodsRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    public Goods buildGoods(SpuBO spuBO) {
        Goods goods = new Goods();
        try {
            List<String> cnameList = categoryClient.queryNameByIds(Arrays.asList(spuBO.getCid1(), spuBO.getCid2(), spuBO.getCid3())).getBody();
            //对象转json
            String cnames = StringUtils.join(cnameList,",");
            String bname = brandClient.getNameByBid(spuBO.getBrandId()).getBody();
            goods.setAll(spuBO.getTitle()+"  "+cnames+"  "+bname);
            goods.setBrandId(spuBO.getBrandId());
            goods.setCid1(spuBO.getCid1());
            goods.setCid2(spuBO.getCid2());
            goods.setCid3(spuBO.getCid3());
            goods.setCreateTime(spuBO.getCreateTime());
            goods.setId(spuBO.getId());
            List<Sku> skuList = goodsClient.getSkuById(spuBO.getId()).getBody();
            List<Long> prices = new ArrayList();
            skuList.forEach(sku -> {
                prices.add(sku.getPrice());
            });
            goods.setPrice(prices);
            //集合转json
            String skus = objectMapper.writeValueAsString(skuList);
            goods.setSkus(skus);
            //map
            Map<String,Object> specs = new HashMap<>();
            String specifications = goodsClient.findDetailById(spuBO.getId()).getBody().getSpecifications();

            List<Map<String,Object>> specList = objectMapper.readValue(specifications,new TypeReference<List<Map<String,Object>>>(){});
            specList.forEach(spec ->{
                List<Map<String,Object>>  params = (List<Map<String,Object>>)spec.get("params");
                params.forEach(param -> {
                    if((boolean)param.get("global")){
                        if((boolean)param.get("searchable")){
                            specs.put(param.get("k").toString(),param.get("v"));
                        }
                    }
                });
            });
            goods.setSpecs(specs);
            goods.setSubTitle(spuBO.getSubTitle());
        } catch (Exception e) {
            log.error("spu转goods发生错误:{}",e.getMessage());
        }
        return goods;
    }

    public PageResult<Goods> search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        Integer page = searchRequest.getPage();
        if (StringUtils.isBlank(key)) {
            return  null;
        }
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        //指定字段查询
        searchQuery.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle"}, null));
        //分词查询 all
        searchQuery.withQuery(QueryBuilders.matchQuery("all",key));
        //分页查询
        searchQuery.withPageable(PageRequest.of(page-1,searchRequest.getSize()));
        Page<Goods> pageInfo = goodsRepository.search(searchQuery.build());
        return new PageResult<>(pageInfo.getTotalElements(),Long.valueOf(pageInfo.getTotalPages()),pageInfo.getContent());
    }
}
