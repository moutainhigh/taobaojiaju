package com.xinshan.controller.commodity;

import com.alibaba.fastjson.JSON;
import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.model.Commodity;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.service.CommodityService;
import com.xinshan.utils.*;
import com.xinshan.utils.qrcode.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 17-5-5.
 */
@Controller
public class CommodityQRCodeController {
    @Autowired
    private CommodityService commodityService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/qrcodeInit")
    public void commodityQrcodeInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(commoditySearchOption);
        List<CommodityExtend> list = commodityService.commodityList(commoditySearchOption);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            CommodityExtend commodityExtend = list.get(i);
            String qrcode = commodityExtend.getQrcode();
            if (qrcode == null || qrcode.equals("")) {
                qrcode = CommodityComponent.qrcode(commodityExtend);
                commodityExtend.setQrcode(qrcode);
                commodityService.updateCommodityQrcode(commodityExtend);
                map.put(commodityExtend.getCommodity_code(), qrcode);
            }
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 请求二维码
     * @param request
     * @param response
     * @throws IOException
     *
     */
    @RequestMapping("/commodity/commodity/qrcode")
    public void commodityQrcode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestUtils.getRequestUtils().printHeaders(request);
        int commodity_id = Integer.parseInt(request.getParameter("commodity_id"));
        Commodity commodity = commodityService.getCommodityById(commodity_id);
        String qrcode = commodity.getQrcode();
        if (qrcode == null) {
            qrcode = CommodityComponent.qrcode(commodity);
            commodity.setQrcode(qrcode);
            commodityService.updateCommodity(commodity);
        }
        if (qrcode != null && !qrcode.equals("")) {
            String domain = CommonUtils.domain;
            domain = "http://" + domain;
            String url = domain + "/wap/commodityDetail.html" + "?code="+qrcode;
            System.out.println(url);
            String pathDir = CommonUtils.QRCODE_DIR;
            File file = new File(pathDir + "/" + qrcode + ".png");
            if (!file.exists()) {
                file = QRCodeUtil.createQRCode(pathDir, qrcode+"", url, 600, 600);
            }
            OutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[4096];
            int size = 0;
            if(file.exists()){
                FileInputStream fileInputStream = new FileInputStream(file);
                while ((size=fileInputStream.read(b))!=-1) {
                    outputStream.write(b, 0, size);
                }
            }
            outputStream.flush();
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/qrcodeCommodity")
    public void commodityMsg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String qrcode = request.getParameter("code");
        CommodityExtend commodityByQRCode = commodityService.getCommodityByQRCode(qrcode);
        if (commodityByQRCode != null && commodityByQRCode.getCommodity_status() == 1) {
            ResponseUtil.sendSuccessResponse(request, response, commodityByQRCode);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "商品已下架"));
        }
    }
}
