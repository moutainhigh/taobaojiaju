package com.xinshan.controller.supplier;

import com.xinshan.model.City;
import com.xinshan.model.District;
import com.xinshan.model.Province;
import com.xinshan.service.ProvinceService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.SearchOptionUtil;
import com.xinshan.pojo.supplier.SupplierSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 16-10-14.
 */
@Controller
public class ProvinceController {
    @Autowired
    private ProvinceService provinceService;
    /**
     * 添加省份
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/createProvince")
    public void createProvince(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Province province = Request2ModelUtils.covert(Province.class, request);
        if (province.getProvince_id() == null) {
            provinceService.createProvince(province);
        }else {
            provinceService.updateProvince(province);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 添加市
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/createCity")
    public void createCity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        City city = Request2ModelUtils.covert(City.class, request);
        if (city.getCity_id() == null){
            provinceService.createCity(city);
        }else {
            provinceService.updateCity(city);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 添加区
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/createCityDistrict")
    public void createCityDistrict(HttpServletRequest request, HttpServletResponse response) throws IOException {
        District district = Request2ModelUtils.covert(District.class, request);
        if (district.getDistrict_id() == null) {
            provinceService.createDistrict(district);
        }else {
            provinceService.updateDistrict(district);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 删除
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/deleteProvince")
    public void deleteProvince(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int province_id = Integer.parseInt(request.getParameter("province_id"));
        provinceService.deleteProvince(province_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }
    /**
     * 删除
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/deleteCity")
    public void deleteCity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int city_id = Integer.parseInt(request.getParameter("city_id"));
        provinceService.deleteCity(city_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }
    /**
     * 删除
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/deleteDistrict")
    public void deleteDistrict(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int district_id = Integer.parseInt(request.getParameter("district_id"));
        provinceService.deleteDistrict(district_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/provinceDetail")
    public void provinceDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int province_id = Integer.parseInt(request.getParameter("province_id"));
        ResponseUtil.sendSuccessResponse(request, response,provinceService.getProvinceById(province_id));
    }
    /**
     * 详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/cityDetail")
    public void cityDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int city_id = Integer.parseInt(request.getParameter("city_id"));
        ResponseUtil.sendSuccessResponse(request, response, provinceService.getCityById(city_id));
    }
    /**
     * 详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/districtDetail")
    public void districtDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int district_id = Integer.parseInt(request.getParameter("district_id"));
        ResponseUtil.sendSuccessResponse(request, response, provinceService.getDistrictById(district_id));
    }

    @RequestMapping("/supplier/supplier/provinceList")
    public void provinceList(HttpServletResponse response, HttpServletRequest request) throws IOException {
        SupplierSearchOption supplierSearchOption = Request2ModelUtils.covert(SupplierSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(supplierSearchOption);
        List<Province> list = provinceService.provinceList(supplierSearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    @RequestMapping("/supplier/supplier/cityList")
    public void cityList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SupplierSearchOption supplierSearchOption = Request2ModelUtils.covert(SupplierSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(supplierSearchOption);
        List<City> list = provinceService.cityList(supplierSearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    @RequestMapping("/supplier/supplier/districtList")
    public void districtList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SupplierSearchOption supplierSearchOption = Request2ModelUtils.covert(SupplierSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(supplierSearchOption);
        List<District> list = provinceService.districtList(supplierSearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }



}
