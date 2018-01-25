package com.xinshan.service;

import com.xinshan.components.category.CategoryComponent;
import com.xinshan.dao.CategoryAttributeMapper;
import com.xinshan.dao.CategoryMapper;
import com.xinshan.dao.extend.category.CategoryExtendMapper;
import com.xinshan.model.CategoryAttribute;
import com.xinshan.model.extend.category.CategoryExtend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-10-17.
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryExtendMapper categoryExtendMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryAttributeMapper categoryAttributeMapper;

    @Transactional
    public void createCategory(CategoryExtend category) {
        categoryExtendMapper.createCategory(category);
        List<CategoryAttribute> list = category.getAttributes();
        for (int i = 0; i < list.size(); i++) {
            CategoryAttribute attribute = list.get(i);
            attribute.setCategory_id(category.getCategory_id());
            categoryExtendMapper.createCategoryAttribute(attribute);
        }
    }

    @Transactional
    public void updateCategory(CategoryExtend categoryExtend) {
        categoryMapper.updateByPrimaryKey(categoryExtend);
        CategoryExtend category = CategoryComponent.getCategoryById(categoryExtend.getCategory_id());
        List<CategoryAttribute> list = category.getAttributes();
        for (int i = 0; i < list.size(); i++) {
            CategoryAttribute attribute = list.get(i);
            categoryAttributeMapper.deleteByPrimaryKey(attribute.getCategory_attribute_id());
        }
        list = categoryExtend.getAttributes();
        for (int i = 0; i < list.size(); i++) {
            CategoryAttribute attribute = list.get(i);
            attribute.setCategory_id(category.getCategory_id());
            categoryExtendMapper.createCategoryAttribute(attribute);
        }
    }

    public List<CategoryExtend> categoryList() {
        return categoryExtendMapper.categoryList();
    }
}
