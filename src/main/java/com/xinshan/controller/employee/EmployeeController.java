package com.xinshan.controller.employee;

import com.xinshan.components.app.PermitComponents;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.employee.EmployeeExtend;
import com.xinshan.service.EmployeeService;
import com.xinshan.utils.*;
import com.xinshan.pojo.employee.EmployeeSearchOption;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mxt on 16-10-9.
 */
@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工导入
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/employee/employeeImport")
    public void employeeImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Iterator iterator = multiRequest.getFileNames();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MultipartFile file = multiRequest.getFile(key);
                if (file != null) {
                    String filename = file.getOriginalFilename();
                    String[] s = filename.split("\\.");
                    String ext = s[s.length - 1];
                    if (ext.equals("xls")) {//
                        //开始处理文件
                        if (employeeImport(file, employee)) {
                            ResponseUtil.sendSuccessResponse(request, response);
                            UploadUtil.saveFile(file, filename, employee, request.getRequestURI());
                        }else {
                            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "导入失败"));
                        }
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "需要导入xls文件"));
                    }
                }
            }
        }
    }

    /**
     *
     * @param file
     * @param employee
     * @return
     */
    public boolean employeeImport(MultipartFile file, Employee employee) {
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<Employee> list = new ArrayList<>();
            for (int i = 1; i < rows; i++) {
                Cell[] cell = sheet.getRow(i);
                String employee_code = cell[0].getContents().trim();
                String employee_name = cell[1].getContents().trim();
                String phone_number = cell[2].getContents().trim();
                Employee e = new Employee();
                e.setEmployee_code(employee_code);
                e.setEmployee_name(employee_name);
                e.setEmployee_status(1);
                e.setPhone_number(phone_number);
                e.setPosition_id(0);
                e.setEmployee_password(EncryptionUtils.encryption("111111"));
                list.add(e);
            }
            employeeService.employeeImport(list);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加编辑人员
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/sys/employee/create", "/sys/employee/update"})
    public void createEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = Request2ModelUtils.covert(Employee.class, request);
        if (employee.getEmployee_status() == null) {
            employee.setEmployee_status(1);
        }
        if (employee.getEmployee_id() == null) {
            Employee e = employeeService.getEmployeeByCode(employee.getEmployee_code());
            if (e != null) {
                ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "用户名重复"));
                return;
            }
            if (employee.getEmployee_password() != null && !employee.getEmployee_password().equals("")) {
                employee.setEmployee_password(EncryptionUtils.encryption(employee.getEmployee_password()));
            }else {
                employee.setEmployee_password(EncryptionUtils.encryption("111111"));
            }
            employeeService.createEmployee(employee);
        }else {
            Employee e = employeeService.getEmployeeByCode1(employee.getEmployee_code());
            String roleIds = e.getRole_ids();
            String roleIds1 = employee.getRole_ids();
            String password = e.getEmployee_password();
            String password1 = employee.getEmployee_password();
            Request2ModelUtils.covertObj(e, employee);
            if (password1 != null) {//未修该密码
                if (password1.equals("")) {
                    e.setEmployee_password(password);
                }else {
                    e.setEmployee_password(EncryptionUtils.encryption(password1));
                }
            }else {
                e.setEmployee_password(password);
            }
            employeeService.updateEmployee(e);
            if (!roleIds.equals(roleIds1)) {
                PermitComponents.clear(e.getEmployee_code());
            }
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 人员详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/employee/employeeDetail")
    public void employeeDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String employee_code = request.getParameter("employee_code");
        EmployeeExtend employee = employeeService.getEmployeeByCode(employee_code);
        ResponseUtil.sendSuccessResponse(request, response, employee);
    }

    /**
     * 人员列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/sys/employee/list", "/order/order/employeeList", "/user/user/employeeList"})
    public void employeeList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        EmployeeSearchOption employeeSearchOption = Request2ModelUtils.covert(EmployeeSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(employeeSearchOption);
        List<EmployeeExtend> list = employeeService.employeeList(employeeSearchOption);
        Integer count = employeeService.countEmployee(employeeSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, employeeSearchOption);
    }

    /**
     * 用户修改密码
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/employee/password")
    public void employeePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String employee_code = employee.getEmployee_code();
        employee = employeeService.getEmployeeByCode1(employee_code);
        if (!EncryptionUtils.encryption(oldPassword).equals(employee.getEmployee_password())) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "密码不正确"));
            return;
        }
        employee.setEmployee_password(EncryptionUtils.encryption(newPassword));
        employeeService.updateEmployee(employee);
        ResponseUtil.sendSuccessResponse(request, response);
        request.getSession().removeAttribute("employee");
    }

}
