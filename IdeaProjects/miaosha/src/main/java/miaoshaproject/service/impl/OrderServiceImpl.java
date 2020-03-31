package miaoshaproject.service.impl;

import miaoshaproject.dao.OrderDOMapper;
import miaoshaproject.dao.SequenceDOMapper;
import miaoshaproject.dataobject.OrderDO;
import miaoshaproject.dataobject.SequenceDO;
import miaoshaproject.error.BussinessException;
import miaoshaproject.error.EmBusinessError;
import miaoshaproject.service.ItemService;
import miaoshaproject.service.OrderService;
import miaoshaproject.service.UserService;
import miaoshaproject.service.model.ItemModel;
import miaoshaproject.service.model.OrderModel;
import miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author huang
 * @data 19:13:52
 * @description
 */
@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private SequenceDOMapper sequenceDOMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;


    @Autowired
    private OrderDOMapper orderDOMapper;

    @Override
    @Transactional
    public OrderModel creatOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BussinessException {
        //1.校验下单状态，商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel ==null){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }

        UserModel userModel = userService.getUserByID(userId);
        if (userModel==null){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
        }
        if (amount <= 0 || amount > 99){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }
        //校验活动信息
        if (promoId!=null){
            if (promoId.intValue()!=itemModel.getPromoModel().getId()){
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
            }else if (itemModel.getPromoModel().getStatus().intValue()!=2){
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
            }
        }


        //2.落单减库存
        boolean result = itemService.decreaseStock(itemId,amount);
        if (!result){
            throw new BussinessException(EmBusinessError.STOCK_ONT_ENOUGH);
        }
        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setAmount(amount);
        orderModel.setItemId(itemId);
        if (promoId!=null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        //生成交易流水号
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);
        //商品的销量
        itemService.increaseSales(itemId,amount);
        //4.返回前端
        return orderModel;
    }




    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generateOrderNo(){
        //订单号有16位，前8位为时间信息，年月日
        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-",",");
        stringBuilder.append(nowDate);
        //中间六位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrenntValue();
        sequenceDO.setCurrenntValue(sequenceDO.getCurrenntValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0;i<6-sequenceStr.length();i++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);


        //最后2位为分库分表位,暂时写死
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if (orderModel ==null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
