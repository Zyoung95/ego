package com.ego.search.service;


import com.ego.commom.pojo.PageResult;
import com.ego.commom.utils.NumberUtils;
import com.ego.item.BO.SpuBO;
import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Category;
import com.ego.item.pojo.Sku;
import com.ego.search.bo.SearchRequest;
import com.ego.search.bo.SearchResponse;
import com.ego.search.client.BrandClient;
import com.ego.search.client.CategoryClient;
import com.ego.search.client.GoodsClient;
import com.ego.search.client.SpecClient;
import com.ego.search.dao.GoodsRepository;
import com.ego.search.pojo.Goods;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.stats.InternalStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private SpecClient specClient;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

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
                    if((boolean)param.get("searchable")){
                        //关于options
                        //specs.put(param.get("k").toString(),param.get("v"));
                        if (param.get("v") != null){
                            specs.put(param.get("k").toString(), param.get("v"));
                        }else if (param.get("options") != null){
                            specs.put(param.get("k").toString(), param.get("options"));
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
        //构建查询条件
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        //指定字段查询
        searchQuery.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle"}, null));
        //分词查询
        //searchQuery.withQuery(QueryBuilders.matchQuery("all",key));
        QueryBuilder queryBuilder = this.buildBasicQueryWithFilter(searchRequest);
        searchQuery.withQuery(queryBuilder);
        //分页查询
        searchQuery.withPageable(PageRequest.of(page-1,searchRequest.getSize()));
        //聚合
        searchQuery.addAggregation(AggregationBuilders.terms("分类").field("cid3"));
        searchQuery.addAggregation(AggregationBuilders.terms("品牌").field("brandId"));



        AggregatedPage<Goods> pageInfo = (AggregatedPage)goodsRepository.search(searchQuery.build());


        List<Category> categories = getCategoryAggResult(pageInfo);
        List<Brand> brands = getBrandAggResult(pageInfo);


        //聚合查询其他规格参数
        List<Map<String,Object>> specs = null;
        if(categories!=null&&categories.size()>0)
        {
            specs = getSpecs(categories.get(0),queryBuilder);
        }


        return new SearchResponse(pageInfo.getTotalElements(),Long.valueOf(pageInfo.getTotalPages()),pageInfo.getContent(),categories,brands,specs);
    }

    /**
     * 构建带过滤条件的基本查询
     * @param searchRequest
     * @return
     */
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest searchRequest) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));
        //过滤条件构造器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        //整理过滤条件
        Map<String,String> filter = searchRequest.getFilter();
        for (Map.Entry<String,String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String regex = "^(\\d+\\.?\\d*)-(\\d+\\.?\\d*)$";
            if (!"key".equals(key)) {
                if ("price".equals(key)){
                    if (!value.contains("元以上")) {
                        String[] nums = StringUtils.substringBefore(value, "元").split("-");
                        filterQueryBuilder.must(QueryBuilders.rangeQuery(key).gte(Double.valueOf(nums[0]) * 100).lt(Double.valueOf(nums[1]) * 100));
                    }else {
                        String num = StringUtils.substringBefore(value,"元以上");
                        filterQueryBuilder.must(QueryBuilders.rangeQuery(key).gte(Double.valueOf(num)*100));
                    }
                }else {
                    if (value.matches(regex)) {
                        Double[] nums = NumberUtils.searchNumber(value, regex);
                        //数值类型进行范围查询   lt:小于  gte:大于等于
                        filterQueryBuilder.must(QueryBuilders.rangeQuery("specs." + key).gte(nums[0]).lt(nums[1]));
                    } else {
                        //商品分类和品牌要特殊处理
                        if (key.equals("分类"))
                        {
                            key = "cid3";
                        }
                        else if(key.equals("品牌"))
                        {
                            key = "brandId";
                        }
                        else{
                            key = "specs." + key + ".keyword";
                        }
                        //字符串类型，进行term查询
                        filterQueryBuilder.must(QueryBuilders.termQuery(key, value));
                    }
                }
            } else {
                break;
            }
        }
        //添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }
    /**
     * 查询其他规格参数以及选项
     * @param category
     * @param queryBuilder
     * @return
     */
    private List<Map<String, Object>> getSpecs(Category category, QueryBuilder queryBuilder) {

        List<Map<String, Object>> result = null;
        try
        {
            //1.根据类别id查询到对应的规格参数
            String specJson = specClient.querySpecificationByCategoryId(category.getId()).getBody();
            //将json -> 对象List<Map<String,Object>>
            List<Map<String,Object>> specMap = objectMapper.readValue(specJson,new TypeReference<List<Map<String,Object>>>(){});

            //2.区分出字符 & 数字(单位) 参数
            Set<String> strSpecs = new HashSet<>();
            //k:参数名字  v:单位
            Map<String, String> numSpecs = new HashMap<>();
            specMap.forEach(param->{
                List<Map<String,Object>> specs = (List<Map<String,Object>>)param.get("params");
                specs.forEach(spec->{
                    if((boolean)spec.get("searchable"))
                    {
                        String k = (String)spec.get("k");
                        //是否是数字
                        if(spec.get("numerical")!=null&&(boolean)spec.get("numerical"))
                        {
                            numSpecs.put(k,(String)spec.get("unit"));
                        }
                        else
                        {
                            strSpecs.add(k);
                        }
                    }
                });
            });


            //3.字符-->terms聚合
            NativeSearchQueryBuilder searchQueryBuilder= new NativeSearchQueryBuilder();
            //加上查询条件
            searchQueryBuilder.withQuery(queryBuilder);

            searchQueryBuilder.withPageable(PageRequest.of(1,1));
            strSpecs.forEach(spec->{
                searchQueryBuilder.addAggregation(AggregationBuilders.terms(spec).field("specs." + spec + ".keyword"));
            });

            //4.数字-->阶梯聚合  间隔
            Map<String, Double> numIntervalMap = getNumIntervalMap(numSpecs);
            numSpecs.forEach((k,v)->{
                searchQueryBuilder.addAggregation(AggregationBuilders.histogram(k).field("specs."+k).interval(numIntervalMap.get(k)).minDocCount(1));
            });
            //5.查询聚合结果
            Map<String, Aggregation> aggResult = elasticsearchTemplate.query(searchQueryBuilder.build(), searchResponse -> searchResponse.getAggregations().asMap());

            //6.解析聚合结果(数字型需要单独处理0->0-100)
            result = parseAggResult(aggResult,strSpecs,numSpecs,numIntervalMap);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 解析聚合结果(包括数字和字符)
     * @param aggResult
     * @param strSpecs
     * @param numSpecs
     * @param numIntervalMap
     * @return
     */
    private List<Map<String, Object>> parseAggResult(Map<String, Aggregation> aggResult, Set<String> strSpecs, Map<String, String> numSpecs, Map<String, Double> numIntervalMap) {
        List<Map<String, Object>> result = new ArrayList<>();

        //解析字符型
        strSpecs.forEach(spec->{
            Map<String, Object> map = new HashMap<>();
            StringTerms aggregation = (StringTerms) aggResult.get(spec);
            List<String> options = aggregation.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            map.put("k", spec);
            map.put("options", options);

            result.add(map);
        });
        //解析数字型
        numSpecs.forEach((spec,unit)->{
            Map<String, Object> map = new HashMap<>();
            InternalHistogram aggregation = (InternalHistogram)aggResult.get(spec);
//            0 400 800 -->  0-400  400-800  800-1200
            List<String> list = aggregation.getBuckets().stream().map(bucket -> {
                Double begin = (Double) bucket.getKey();
                Double end = begin + numIntervalMap.get(spec);
                //判断是否是整型
                if (NumberUtils.isInt(begin) && NumberUtils.isInt(end)) {
                    return begin + "-" + end;
                } else {
                    //保留一位小数点   1.8
                    return NumberUtils.scale(begin, 1) + "-" + NumberUtils.scale(end, 1);
                }
            }).collect(Collectors.toList());

            map.put("k", spec);
            map.put("options",list);
            map.put("unit",unit);

            result.add(map);
        });
        return result;
    }

    /**
     * 获取数字型参数的间隔
     * @param numSpecs
     * @return
     */
    private Map<String, Double> getNumIntervalMap(Map<String, String> numSpecs) {

        Map<String, Double> result = new HashMap<>();

        //去es中查询到每个数字参数的 min,max,sum
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
//        searchQueryBuilder.withPageable(PageRequest.of(1, 1));
        searchQueryBuilder.withQuery(QueryBuilders.termQuery("cid3","76")).withSourceFilter(new FetchSourceFilter(new String[]{""},null)).withPageable(PageRequest.of(0,1));

        //聚合
        numSpecs.keySet().forEach(spec->{
            searchQueryBuilder.addAggregation(AggregationBuilders.stats(spec).field("specs."+spec));
        });

        Map<String, Aggregation> aggMap = elasticsearchTemplate.query(searchQueryBuilder.build(), searchResponse -> searchResponse.getAggregations().asMap());

        //单独计算每个数字参数的间隔
        numSpecs.keySet().forEach(spec->{
            InternalStats aggregation = (InternalStats)aggMap.get(spec);
            Double interval = NumberUtils.getInterval(aggregation.getMin(),aggregation.getMax(),aggregation.getSum());
            result.put(spec,interval);
        });
        return result;
    }

    /**
     * 获取品牌聚合结果
     * @param pageInfo
     * @return
     */
    private List<Brand> getBrandAggResult(AggregatedPage<Goods> pageInfo) {
        LongTerms agg = (LongTerms) pageInfo.getAggregation("品牌");
        List<Long> ids = agg.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        return brandClient.queryListByIds(ids).getBody();
    }

    /**
     * 获取分类聚合结果
     * @param pageInfo
     * @return
     */
    private List<Category> getCategoryAggResult(AggregatedPage<Goods> pageInfo) {
        LongTerms agg = (LongTerms)pageInfo.getAggregation("分类");
        List<Long> ids = agg.getBuckets().stream().map(bucket -> (Long)bucket.getKey()).collect(Collectors.toList());
        //到微服务中通过ids查询到List<Category>
        List<String> cnames = categoryClient.queryNameByIds(ids).getBody();
        //原子操作(保证不被其他线程干扰)
        AtomicInteger i = new AtomicInteger();

        return ids.stream().map(id->{
            Category category = new Category();
            category.setId(id);
            category.setName(cnames.get(i.getAndIncrement()));
            return category;
        }).collect(Collectors.toList());

    }
}
