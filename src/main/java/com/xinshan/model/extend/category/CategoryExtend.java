package com.xinshan.model.extend.category;

import com.xinshan.model.Category;
import com.xinshan.model.CategoryAttribute;

import java.util.List;

/**
 * Created by mxt on 16-10-17.
 */
public class CategoryExtend extends Category {
    private List<CategoryAttribute> attributes;
    private List<CategoryExtend> list;

    private String parentCategoryName;

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public List<CategoryExtend> getList() {
        return list;
    }

    public void setList(List<CategoryExtend> list) {
        this.list = list;
    }

    public List<CategoryAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<CategoryAttribute> attributes) {
        this.attributes = attributes;
    }
}
