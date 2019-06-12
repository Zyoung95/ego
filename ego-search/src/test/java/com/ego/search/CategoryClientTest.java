package com.ego.search;

import com.ego.commom.pojo.PageResult;
import com.ego.item.BO.SpuBO;
import com.ego.search.client.CategoryClient;
import com.ego.search.client.GoodsClient;
import com.ego.search.dao.GoodsRepository;
import com.ego.search.pojo.Goods;
import com.ego.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EgoSearchService.class)
public class CategoryClientTest {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private SearchService searchService;
    @Test
    public void testQueryCategories() {
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(1L, 2L, 3L)).getBody();
        names.forEach(System.out::println);
    }

    @Test
    public void create() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void save() {

        int page = 1;
        int size= 0;
        do{
            PageResult<SpuBO> result = goodsClient.page("", true, page++, 10).getBody();
            List listGoods = new ArrayList();
            result.getItems().forEach(spuBO -> {
                listGoods.add(searchService.buildGoods(spuBO));
            });
            size = listGoods.size();
            //System.out.println(size);
            goodsRepository.saveAll(listGoods);
        }while(size==10);
    }

    @Test
    public void demo() {
        Scanner scanner = new Scanner(System.in);
        int n = 0;
        while(n==-1){
            System.out.println("请输入想要打印菱形的行数：");
            n = scanner.nextInt();
            for (int i = 0; i < n; i++) {
                for (int k = 0; k < n-1-i; k++) {
                    System.out.print(" ");
                }
                for (int j = 0; j < 2*i+1; j++) {
                    System.out.print("*");
                }
                System.out.println();
            }
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k <= i; k++) {
                    System.out.print(" ");
                }
                for (int j = 0; j < 2*n-2*i; j++) {
                    System.out.print("*");
                }
                System.out.println();
            }
        }
    }
}
