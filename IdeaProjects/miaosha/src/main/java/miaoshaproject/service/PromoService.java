package miaoshaproject.service;

import miaoshaproject.service.model.PromoModel;

/**
 * @author huang
 * @data 11:08:12
 * @description
 */
public interface PromoService {
    //根据itemid获取即将进行的或正在进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);
}
