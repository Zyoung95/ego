package ego.goods.web.controller;

import ego.goods.web.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/item")
public class GoodsCtrl {

    @Autowired
    private GoodsService goodsService;
    @GetMapping("/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model){
        //加载数据
        Map<String, Object> modelMap = this.goodsService.loadModel(id);
        //把数据放入模型中
        model.addAllAttributes(modelMap);

        return "item";
    }
}
