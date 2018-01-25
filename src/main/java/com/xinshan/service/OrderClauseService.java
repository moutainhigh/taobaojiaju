package com.xinshan.service;

import com.xinshan.dao.OrderClauseMapper;
import com.xinshan.dao.extend.orderClause.OrderClauseExtendMapper;
import com.xinshan.model.OrderClause;
import com.xinshan.pojo.orderClause.OrderClauseSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-12-28.
 */
@Service
public class OrderClauseService {
    @Autowired
    private OrderClauseExtendMapper orderClauseExtendMapper;
    @Autowired
    private OrderClauseMapper orderClauseMapper;
    @Transactional
    public void createOrderClause(OrderClause orderClause) {
        orderClauseExtendMapper.createOrderClause(orderClause);
    }

    public List<OrderClause> orderClauseList(OrderClauseSearchOption orderClauseSearchOption) {
        return orderClauseExtendMapper.orderClauseList(orderClauseSearchOption);
    }

    public OrderClause getClauseByType(int order_clause_type) {
        OrderClauseSearchOption orderClauseSearchOption = new OrderClauseSearchOption();
        orderClauseSearchOption.setOrder_clause_type(order_clause_type);
        List<OrderClause> list = orderClauseList(orderClauseSearchOption);
        if (list != null && list.size() == 1){
            return list.get(0);
        }
        return null;
    }
    @Transactional
    public void updateOrderClause(OrderClause orderClause) {
        orderClauseMapper.updateByPrimaryKey(orderClause);
    }
}
