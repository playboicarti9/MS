package miaoshaproject.service.model;

import java.math.BigDecimal;

/**
 * @author huang
 * @data 18:57:14
 * @description
 */
public class OrderModel {
    private Integer userId;

    //若非空则以秒杀商品方式下单
    private Integer promoId;
    //购买商品的单价.若promoId非空则表示秒杀商品价格
    private BigDecimal itemPrice;
    private String id;
    private Integer itemId;
    private Integer amount;
    private BigDecimal orderPrice;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }
}
