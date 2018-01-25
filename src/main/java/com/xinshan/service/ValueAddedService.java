package com.xinshan.service;

import com.xinshan.components.pay.PayComponents;
import com.xinshan.components.pay.PayReturnComponents;
import com.xinshan.dao.ValueAddedCardBookingMapper;
import com.xinshan.dao.ValueAddedCardMapper;
import com.xinshan.dao.extend.activity.ValueAddedExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ValueAddedCardBookingExtend;
import com.xinshan.model.extend.activity.ValueAddedCardExtend;
import com.xinshan.pojo.activity.ValueAddedCardSearchOption;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 17-4-13.
 */
@Service
public class ValueAddedService {
    @Autowired
    private ValueAddedExtendMapper valueAddedExtendMapper;
    @Autowired
    private ValueAddedCardMapper valueAddedCardMapper;
    @Autowired
    private ValueAddedCardBookingMapper valueAddedCardBookingMapper;

    @Transactional
    public void createValueAddedCardBooking(ValueAddedCardBooking valueAddedCardBooking) {
        valueAddedExtendMapper.createValueAddedCardBooking(valueAddedCardBooking);
    }

    @Transactional
    public ValueAddedCard createValueAddedCard(String value_added_card_code, int activity_id, Employee employee) {
        ValueAddedCard valueAddedCard = new ValueAddedCard();
        valueAddedCard.setActivity_id(activity_id);
        valueAddedCard.setValue_added_card_code(value_added_card_code);
        valueAddedCard.setValue_added_create_employee_code(employee.getEmployee_code());
        valueAddedCard.setValue_added_create_employee_name(employee.getEmployee_name());
        valueAddedCard.setValue_added_create_date(DateUtil.currentDate());
        valueAddedCard.setCard_enable(1);
        valueAddedCard.setCard_return_status(0);//1已领取，2已退还
        valueAddedCard.setGift_status(0);
        valueAddedCard.setReturn_gift_status(0);
        valueAddedCard.setReturn_amount(new BigDecimal("0"));
        valueAddedCard.setPay_amount(new BigDecimal("0"));
        valueAddedExtendMapper.createValueAddedCard(valueAddedCard);
        return valueAddedCard;
    }

    /**
     * 领取增值卡
     * @param valueAddedCardBookingExtend
     * @param valueAddedCard
     */
    @Transactional
    public void takeCard(ValueAddedCardBookingExtend valueAddedCardBookingExtend, ValueAddedCard valueAddedCard, BigDecimal pay_amount, Employee employee) {
        OrderPay orderPay = PayComponents.pay(pay_amount, employee, PayComponents.pay_source_cash, PayComponents.pay_type_value_card);
        valueAddedCard.setPay_ids(String.valueOf(orderPay.getOrder_pay_id()));
        valueAddedCard.setUser_id(valueAddedCardBookingExtend.getUser_id());
        valueAddedCard.setCard_return_status(1);//领取状态
        valueAddedCard.setGift_status(1);
        valueAddedCard.setTake_date(DateUtil.currentDate());
        valueAddedCard.setPay_amount(pay_amount);
        valueAddedCardMapper.updateByPrimaryKey(valueAddedCard);
        valueAddedCardBookingExtend.setBooking_date(DateUtil.currentDate());
        valueAddedCardBookingExtend.setBooking_status(1);
        valueAddedCardBookingExtend.setValue_added_card_id(valueAddedCard.getValue_added_card_id());
        valueAddedCardBookingMapper.updateByPrimaryKey(valueAddedCardBookingExtend);
    }

    @Transactional
    public void recycling(ValueAddedCard valueAddedCard, Employee employee) {
        BigDecimal return_amount = valueAddedCard.getReturn_amount();
        OrderPayReturn orderPayReturn = PayReturnComponents.payReturn(return_amount, null, employee,
                PayReturnComponents.pay_return_source_cash, PayReturnComponents.pay_return_type_value_added);
        valueAddedCard.setPay_return_ids(String.valueOf(orderPayReturn.getOrder_pay_return_id()));
        valueAddedCardMapper.updateByPrimaryKey(valueAddedCard);
    }
    @Transactional
    public void updateValueAddedCard(ValueAddedCard valueAddedCard) {
        valueAddedCardMapper.updateByPrimaryKey(valueAddedCard);
    }

    public void valueAddedCardOrder(String valueAddedCardCode, Order order) {
        ValueAddedCard valueAddedCard = getValueAddedCardByCode(valueAddedCardCode);
        valueAddedCard.setOrder_id(order.getOrder_id());
        valueAddedCardMapper.updateByPrimaryKey(valueAddedCard);
    }

    public ValueAddedCardBookingExtend getBookingById(int value_added_card_booking_id) {
        ValueAddedCardSearchOption valueAddedCardSearchOption = new ValueAddedCardSearchOption();
        valueAddedCardSearchOption.setValue_added_card_booking_id(value_added_card_booking_id);
        List<ValueAddedCardBookingExtend> list = valueAddedCardBookings(valueAddedCardSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public ValueAddedCardExtend getValueAddedCardByCode(String valueAddedCardCode) {
        ValueAddedCardSearchOption valueAddedCardSearchOption = new ValueAddedCardSearchOption();
        valueAddedCardSearchOption.setValue_added_card_code(valueAddedCardCode);
        List<ValueAddedCardExtend> list = valueAddedCards(valueAddedCardSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public List<ValueAddedCardBookingExtend> valueAddedCardBookings(ValueAddedCardSearchOption valueAddedCardSearchOption) {
        return valueAddedExtendMapper.valueAddedCardBookings(valueAddedCardSearchOption);
    }

    public Integer countValueAddedCardBookings(ValueAddedCardSearchOption valueAddedCardSearchOption) {
        return valueAddedExtendMapper.countValueAddedCardBookings(valueAddedCardSearchOption);
    }

    public List<ValueAddedCardExtend> valueAddedCards(ValueAddedCardSearchOption valueAddedCardSearchOption) {
        return valueAddedExtendMapper.valueAddedCards(valueAddedCardSearchOption);
    }

    public Integer countValueAddedCard(ValueAddedCardSearchOption valueAddedCardSearchOption) {
        return valueAddedExtendMapper.countValueAddedCard(valueAddedCardSearchOption);
    }
}
