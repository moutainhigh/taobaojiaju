package com.xinshan.service;

import com.xinshan.dao.UserShoppingCommodityMapper;
import com.xinshan.dao.UserShoppingMapper;
import com.xinshan.dao.extend.user.UserShoppingExtendMapper;
import com.xinshan.model.Employee;
import com.xinshan.model.UserShopping;
import com.xinshan.model.UserShoppingCommodity;
import com.xinshan.model.extend.user.UserShoppingCommodityExtend;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.pojo.user.UserSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.websocket.server.ServerOrderSyncHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by mxt on 17-7-31.
 */
@Service
public class UserShoppingService {
    @Autowired
    private UserShoppingExtendMapper userShoppingExtendMapper;
    @Autowired
    private UserShoppingMapper userShoppingMapper;
    @Autowired
    private UserShoppingCommodityMapper userShoppingCommodityMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @Transactional
    public void syncCreateUserShopping(UserShoppingExtend userShoppingExtend) {
        userShoppingExtendMapper.createUserShopping(userShoppingExtend);
        List<UserShoppingCommodityExtend> userShoppingCommodities = userShoppingExtend.getUserShoppingCommodities();
        createUserShoppingCommodity(userShoppingExtend, userShoppingCommodities);
    }

    private void createUserShoppingCommodity(UserShoppingExtend userShoppingExtend, List<UserShoppingCommodityExtend> userShoppingCommodities) {
        for (int i = 0; i < userShoppingCommodities.size(); i++) {
            UserShoppingCommodityExtend userShoppingCommodityExtend = userShoppingCommodities.get(i);
            if (userShoppingCommodityExtend.getCommodity_id() != null) {
                userShoppingCommodityExtend.setUser_shopping_id(userShoppingExtend.getUser_shopping_id());
                userShoppingExtendMapper.createUserShoppingCommodity(userShoppingCommodityExtend);
            }
        }
    }


    @Transactional
    public void syncUpdateUserShopping(UserShoppingExtend userShoppingExtend) {
        UserShopping userShopping = userShoppingMapper.selectByPrimaryKey(userShoppingExtend.getUser_shopping_id());
        if (userShopping == null) {
            syncCreateUserShopping(userShoppingExtend);
            return;
        }
        userShoppingExtendMapper.updateUserShopping(userShoppingExtend);
        userShoppingExtendMapper.deleteUserShoppingCommodity(userShoppingExtend.getUser_shopping_id());
        List<UserShoppingCommodityExtend> userShoppingCommodities = userShoppingExtend.getUserShoppingCommodities();
        createUserShoppingCommodity(userShoppingExtend, userShoppingCommodities);
    }

    @Transactional
    public void createUserShopping(UserShoppingExtend userShopping, Employee employee) {
        if (userShopping.getInto_store_date() == null) {
            userShopping.setInto_store_date(DateUtil.currentDate());
        }
        userShopping.setEmployee_code(employee.getEmployee_code());
        userShopping.setEmployee_name(employee.getEmployee_name());
        userShopping.setConvert_users(0);
        userShopping.setGenerate_order(0);
        userShoppingExtendMapper.createUserShopping(userShopping);
        String commodityIds = userShopping.getCommodityIds();
        Set<Integer> strings = SplitUtils.splitToSet(commodityIds, ",");
        Iterator<Integer> iterator = strings.iterator();
        while (iterator.hasNext()) {
            Integer commodity_id = iterator.next();
            createUserShoppingCommodity(commodity_id, userShopping.getUser_shopping_id());
        }
    }

    private void createUserShoppingCommodity(int commodity_id, int user_shopping_id) {
        UserShoppingCommodity userShoppingCommodity = new UserShoppingCommodity();
        userShoppingCommodity.setUser_shopping_id(user_shopping_id);
        userShoppingCommodity.setCommodity_id(commodity_id);
        userShoppingCommodity.setShopping_commodity_enable(1);
        userShoppingExtendMapper.createUserShoppingCommodity(userShoppingCommodity);
    }

    @Transactional
    public void updateUserShopping(UserShoppingExtend userShopping, Employee employee) {
        if (userShopping.getEmployee_code() == null) {
            userShopping.setEmployee_code(employee.getEmployee_code());
            userShopping.setEmployee_name(employee.getEmployee_name());
        }
        userShoppingExtendMapper.updateUserShopping(userShopping);
        userShoppingExtendMapper.deleteUserShoppingCommodity(userShopping.getUser_shopping_id());
        String commodityIds = userShopping.getCommodityIds();
        Set<Integer> strings = SplitUtils.splitToSet(commodityIds, ",");
        Iterator<Integer> iterator = strings.iterator();
        while (iterator.hasNext()) {
            Integer commodity_id = iterator.next();
            createUserShoppingCommodity(commodity_id, userShopping.getUser_shopping_id());
        }
    }

    public List<UserShoppingExtend> userShoppingList(List<Integer> userShoppingIds) {
        if (userShoppingIds == null || userShoppingIds.size() == 0) {
            return new ArrayList<>();
        }
        UserSearchOption userSearchOption = new UserSearchOption();
        userSearchOption.setUserShoppingIds(userShoppingIds);
        List<UserShoppingExtend> list = userShoppingExtendMapper.userShoppingList(userSearchOption);
        return list;
    }

    public UserShoppingExtend getUserShoppingById(int user_shopping_id) {
        List<Integer> list = new ArrayList<>();
        list.add(user_shopping_id);
        List<UserShoppingExtend> userShoppingExtends = userShoppingList(list);
        if (userShoppingExtends != null && userShoppingExtends.size() == 1) {
            return userShoppingExtends.get(0);
        }
        return null;
    }

    public List<Integer> userShoppingIds(UserSearchOption userSearchOption) {
        return userShoppingExtendMapper.userShoppingIds(userSearchOption);
    }

    public Integer countUserShopping(UserSearchOption userSearchOption) {
        return userShoppingExtendMapper.countUserShopping(userSearchOption);
    }

    @Transactional
    public void createOrder(UserShoppingExtend userShoppingExtend) {
        userShoppingExtend.setGenerate_order(1);
        userShoppingMapper.updateByPrimaryKey(userShoppingExtend);

        //TODO 生成订单
        ServerOrderSyncHandler.getServerSyncHandler().sendUserShoppingCreateOrder(userShoppingExtend);
    }

    @Transactional
    public void createUser(UserShoppingExtend userShoppingExtend) {
        userShoppingExtend.setConvert_users(1);
        userShoppingMapper.updateByPrimaryKey(userShoppingExtend);

        //TODO 生成用户

    }
}
