package com.ego.item.mapper;


import com.ego.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface CategoryMapper extends Mapper<Category> , SelectByIdListMapper<Category,Long> {

    @Select("select category_id from tb_category_brand where brand_id = #{id}")
    List<Long> selectCategoryById(@Param("id") Long id);


}
