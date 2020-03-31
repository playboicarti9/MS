package miaoshaproject.service.impl;

import miaoshaproject.dao.PromoDOMapper;
import miaoshaproject.dataobject.PromoDO;
import miaoshaproject.service.PromoService;
import miaoshaproject.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author huang
 * @data 11:10:40
 * @description
 */
@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
    //获取对应秒杀活动的信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        //dataobject转化为model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if(promoModel==null){
            return null;
        }
        //判断当前秒杀活动即将开始或正在进行

        if (promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if (promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }
        return promoModel;


    }
    private PromoModel convertFromDataObject(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
