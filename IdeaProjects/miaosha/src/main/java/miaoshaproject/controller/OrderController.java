package miaoshaproject.controller;

import miaoshaproject.error.BussinessException;
import miaoshaproject.error.EmBusinessError;
import miaoshaproject.response.CommonReturnType;
import miaoshaproject.service.OrderService;
import miaoshaproject.service.model.OrderModel;
import miaoshaproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huang
 * @data 21:03:28
 * @description
 */
@Controller("order")
@RequestMapping("/order")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class OrderController extends BaseController{
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    //封装下单请求
    @RequestMapping(value = "/createorder",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType creatOrder(@RequestParam(name = "itemId")Integer itemId,
                         @RequestParam(name = "amount")Integer amount,
                         @RequestParam(name = "promoId",required = false)Integer promoId
                                       ) throws BussinessException {
       Boolean isLogin = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
       if (isLogin==null || !isLogin.booleanValue()){
           throw new BussinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登陆");
       }

        //获取登陆信息
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel =  orderService.creatOrder(userModel.getId(),itemId,promoId,amount);
        return CommonReturnType.create(null);
    }
}
