package com.ego.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Table;

@Data
@Table(name = "tb_category")
public class Category {

    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
    private Long parentId;
    private Boolean isParent;
    private Integer sort;

    /*@Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", isParent=" + isParent +
                ", sort=" + sort +
                '}';
    }*/
}

