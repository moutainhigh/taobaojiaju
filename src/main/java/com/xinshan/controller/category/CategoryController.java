package com.xinshan.controller.category;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.category.CategoryComponent;
import com.xinshan.model.Category;
import com.xinshan.model.extend.category.CategoryExtend;
import com.xinshan.service.CategoryService;
import com.xinshan.utils.RequestUtils;
import com.xinshan.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mxt on 16-10-17.
 */
@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/category/addCategory","/supplier/category/updateCategory"})
    public void createCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postData = RequestUtils.getRequestUtils().postData(request);
        CategoryExtend categoryExtend = JSONObject.parseObject(postData, CategoryExtend.class);
        if (categoryExtend.getCategory_id() == null) {
            categoryService.createCategory(categoryExtend);
        }else {
            categoryService.updateCategory(categoryExtend);
        }
        ResponseUtil.sendSuccessResponse(request, response, postData);
        CategoryComponent.clear();
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/category/categoryList","/supplier/commodity/categoryList"})
    public void categoryList(HttpServletRequest request, HttpServletResponse response)throws IOException {
        if (request.getParameter("category_id") == null || request.getParameter("category_id").equals("")) {
            ResponseUtil.sendSuccessResponse(request, response, CategoryComponent.getCategoryList());
        } else {
            int category_id = Integer.parseInt(request.getParameter("category_id"));
            CategoryExtend categoryExtend = CategoryComponent.getCategoryById(category_id);
            if (categoryExtend == null) {
                ResponseUtil.sendSuccessResponse(request, response, CategoryComponent.getCategoryList());
            }else {
                if (categoryExtend.getCategory_parent_id() != null && categoryExtend.getCategory_parent_id() != 0) {
                    Category category = CategoryComponent.getCategoryById(categoryExtend.getCategory_parent_id());
                    if (category != null) {
                        categoryExtend.setParentCategoryName(category.getCategory_name());
                    }
                }
                ResponseUtil.sendSuccessResponse(request, response, categoryExtend);
            }
        }
    }
}
