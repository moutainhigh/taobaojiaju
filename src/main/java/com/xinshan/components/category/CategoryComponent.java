package com.xinshan.components.category;

import com.xinshan.model.Category;
import com.xinshan.model.extend.category.CategoryExtend;
import com.xinshan.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mxt on 16-10-17.
 */
@Component
public class CategoryComponent {

    private final static Lock lock = new ReentrantLock();
    private static CategoryService categoryService;

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        CategoryComponent.categoryService = categoryService;
    }

    private static List<CategoryExtend> categoryList = new ArrayList<>();//商品分类目录
    private static List<CategoryExtend> categories = new ArrayList<>();//商品费类列表

    public static void clear(){
        lock.lock();
        try {
            categoryList.clear();
            categories.clear();
        }finally {
            lock.unlock();
        }
    }

    public static List<CategoryExtend> getCategoryList() {
        if (categoryList == null || categoryList.isEmpty()) {
            init();
        }
        return categoryList;
    }

    private static List<CategoryExtend> getCategories() {
        if (categories == null || categories.isEmpty()) {
            initCategories();
        }
        return categories;
    }

    private static void initCategories(){
        lock.lock();
        try {
            categories = categoryService.categoryList();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    private static void init() {
        lock.lock();
        try {
            List<CategoryExtend> list = initCategoryList();
            CategoryComponent.categoryList = list;
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    private static List<CategoryExtend> initCategoryList() {
        List<CategoryExtend> list = getCategories();
        List<CategoryExtend> categoryExtends = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CategoryExtend category = list.get(i);
            if (category.getCategory_parent_id() == 0) {
                category.setList(childCategory(category, list));
                categoryExtends.add(category);
            }
        }
        Collections.sort(categoryExtends, new CategoryComponent.categorySort());
        return categoryExtends;
    }

    private static List<CategoryExtend> childCategory(Category category, List<CategoryExtend> list) {
        List<CategoryExtend> categories = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CategoryExtend categoryExtend = list.get(i);
            if (categoryExtend.getCategory_parent_id() == category.getCategory_id()) {
                categoryExtend.setList(childCategory(categoryExtend, list));
                categories.add(categoryExtend);
            }
        }
        if (categories.isEmpty()) {
            return null;
        }
        Collections.sort(categories, new CategoryComponent.categorySort());
        return categories;
    }

    public static CategoryExtend getCategoryById(int category_id) {
        List<CategoryExtend> list = getCategories();
        for (int i = 0; i < list.size(); i++) {
            CategoryExtend category = list.get(i);
            if (category.getCategory_id() == category_id) {
                /*if (category.getCategory_parent_id() != null && category.getCategory_parent_id() != 0) {
                    category.setParentCategory(getCategoryById(category.getCategory_parent_id()));
                }*/
                return category;
            }
        }
        return null;
    }

    public static CategoryExtend getCategoryName(String category_name) {
        List<CategoryExtend> list = getCategories();
        for (int i = 0; i < list.size(); i++) {
            CategoryExtend category = list.get(i);
            if (category.getCategory_name().equals(category_name)) {
                return category;
            }
        }
        return null;
    }

    private static class categorySort implements Comparator<Category>{
        @Override
        public int compare(Category o1, Category o2) {
            if (o1 == null) {
                return -1;
            }else if (o2 == null) {
                return 1;
            }else {
                return o1.getCategory_sort() - o2.getCategory_sort();
            }
        }
    }
}
