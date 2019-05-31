package com.ego.item.mapper;

import com.ego.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface BrandMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    void save(@Param("bid") Long bid,@Param("cid") Long cid);

    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    void deleteCategoryBrandById(@Param("bid") Long bid);

    @Select("select b.id,b.name,b.image,b.letter from tb_brand b where id in (select cb.brand_id from tb_category_brand cb where cb.category_id = #{cid})")
    List<Brand> ListByCid(@Param("cid") Long cid);
}
