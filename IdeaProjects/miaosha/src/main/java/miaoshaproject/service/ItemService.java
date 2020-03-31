package miaoshaproject.service;

import miaoshaproject.error.BussinessException;
import miaoshaproject.service.model.ItemModel;

import java.util.List;

/**
 * @author huang
 * @data 10:42:41
 * @description
 */
public interface ItemService {
    //创建商品
    ItemModel creatItem(ItemModel itemModel) throws BussinessException;

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId,Integer amount)throws BussinessException;

    //商品销量增加
    void increaseSales(Integer itemId,Integer amount)throws BussinessException;

}
