package com.xinshan.dao.extend.specialRightSell;

import com.xinshan.model.SpecialRightSell;
import com.xinshan.model.extend.specialRightSell.SpecialRightSellExtend;
import com.xinshan.pojo.specialRightSell.SpecialRightSellSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-20.
 */
public interface SpecialRightSellExtendMapper {

    void createSpecialRightSell(SpecialRightSell specialRightSell);

    List<SpecialRightSellExtend> specialRightSells(SpecialRightSellSearchOption specialRightSellSearchOption);

    Integer countSpecialRightSell(SpecialRightSellSearchOption specialRightSellSearchOption);
}
