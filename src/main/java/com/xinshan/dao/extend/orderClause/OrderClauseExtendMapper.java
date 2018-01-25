package com.xinshan.dao.extend.orderClause;

import com.xinshan.model.OrderClause;
import com.xinshan.pojo.orderClause.OrderClauseSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-12-28.
 */
public interface OrderClauseExtendMapper {
    void createOrderClause(OrderClause orderClause);

    List<OrderClause> orderClauseList(OrderClauseSearchOption orderClauseSearchOption);
}
