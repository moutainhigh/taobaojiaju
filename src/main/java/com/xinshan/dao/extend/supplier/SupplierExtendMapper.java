package com.xinshan.dao.extend.supplier;

import com.xinshan.model.SellType;
import com.xinshan.model.Supplier;
import com.xinshan.model.SupplierSeries;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.model.extend.supplier.SupplierSeriesExtend;
import com.xinshan.pojo.supplier.SupplierSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-14.
 */
public interface SupplierExtendMapper {

    void createSupplier(Supplier supplier);

    List<SupplierExtend> supplierList(SupplierSearchOption supplierSearchOption);

    Integer countSupplier(SupplierSearchOption supplierSearchOption);

    void createSellType(SellType sellType);

    void createSeries(SupplierSeries supplierSeries);

    List<SupplierSeriesExtend> seriesList(SupplierSearchOption supplierSearchOption);

    Integer countSeries(SupplierSearchOption supplierSearchOption);

    SellType getSellTypeByName(String sell_type_name);

    List<String> supplierContacts(SupplierSearchOption supplierSearchOption);

    Integer countSupplierContracts(SupplierSearchOption supplierSearchOption);
}
