package com.xinshan.dao.extend.category;

import com.xinshan.model.Category;
import com.xinshan.model.CategoryAttribute;
import com.xinshan.model.extend.category.CategoryExtend;

import java.util.List;

/**
 * Created by mxt on 16-10-17.
 */
public interface CategoryExtendMapper {

    void createCategoryAttribute(CategoryAttribute categoryAttribute);
    void createCategory(Category category);
    List<CategoryExtend> categoryList();
}
