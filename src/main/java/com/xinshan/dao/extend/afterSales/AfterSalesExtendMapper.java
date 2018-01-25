package com.xinshan.dao.extend.afterSales;

import com.xinshan.model.AfterSales;
import com.xinshan.model.AfterSalesCommodity;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.afterSales.AfterSalesExtend;
import com.xinshan.pojo.afterSales.AfterSalesSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-12-15.
 */
public interface AfterSalesExtendMapper {

    void createAfterSales(AfterSales afterSales);
    void createAfterSalesCommodity(AfterSalesCommodity afterSalesCommodity);

    List<Integer> afterSalesIds(AfterSalesSearchOption afterSalesSearchOption);
    Integer countAfterSales(AfterSalesSearchOption afterSalesSearchOption);

    List<AfterSalesExtend> afterSalesList(AfterSalesSearchOption afterSalesSearchOption);
    List<AfterSalesCommodityExtend> afterSalesCommodities(AfterSalesSearchOption afterSalesSearchOption);

}
