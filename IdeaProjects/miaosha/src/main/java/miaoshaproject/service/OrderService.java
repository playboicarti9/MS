package miaoshaproject.service;

import miaoshaproject.error.BussinessException;
import miaoshaproject.service.model.OrderModel;

/**
 * @author huang
 * @data 19:11:02
 * @description
 */
public interface OrderService {

    OrderModel creatOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BussinessException;
}
