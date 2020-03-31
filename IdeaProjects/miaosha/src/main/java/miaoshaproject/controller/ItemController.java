package miaoshaproject.controller;

import miaoshaproject.controller.viewobject.ItemVO;
import miaoshaproject.error.BussinessException;
import miaoshaproject.response.CommonReturnType;
import miaoshaproject.service.ItemService;
import miaoshaproject.service.model.ItemModel;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huang
 * @data 11:34:50
 * @description
 */
@Controller("/item")
@RequestMapping("item")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class ItemController extends BaseController{
    @Autowired
    private ItemService itemService;

    //创建商品的controller
    @RequestMapping(value = "/create" ,method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BussinessException, BussinessException {
        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setImgUrl(imgUrl);
        itemModel.setStock(stock);
        ItemModel item = itemService.creatItem(itemModel);
        ItemVO itemVO = convertVOFromModel(item);
        return CommonReturnType.create(itemVO);
    }

    //商品详情页浏览
    @RequestMapping(value = "/getItem" ,method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id){
        ItemModel items = itemService.getItemById(id);
        ItemVO itemVO = convertVOFromModel(items);
        return CommonReturnType.create(itemVO);
    }
    // 商品列表页面浏览
    @RequestMapping(value = "/listItem" ,method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType itemList(){
        List<ItemModel> itemModelList = itemService.listItem();
        //使用stream api将list内的itemModle转化为ITEMVO；
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    private ItemVO convertVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        if (itemModel.getPromoModel()!=null){
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice()) ;
        }else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}
