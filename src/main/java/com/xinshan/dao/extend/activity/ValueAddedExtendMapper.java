package com.xinshan.dao.extend.activity;

import com.xinshan.model.ValueAddedCard;
import com.xinshan.model.ValueAddedCardBooking;
import com.xinshan.model.extend.activity.ValueAddedCardBookingExtend;
import com.xinshan.model.extend.activity.ValueAddedCardExtend;
import com.xinshan.pojo.activity.ValueAddedCardSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-13.
 */
public interface ValueAddedExtendMapper {

    void createValueAddedCard(ValueAddedCard valueAddedCard);

    void createValueAddedCardBooking(ValueAddedCardBooking valueAddedCardBooking);

    List<ValueAddedCardBookingExtend> valueAddedCardBookings(ValueAddedCardSearchOption valueAddedCardSearchOption);

    Integer countValueAddedCardBookings(ValueAddedCardSearchOption valueAddedCardSearchOption);

    List<ValueAddedCardExtend> valueAddedCards(ValueAddedCardSearchOption valueAddedCardSearchOption);

    Integer countValueAddedCard(ValueAddedCardSearchOption valueAddedCardSearchOption);
}
