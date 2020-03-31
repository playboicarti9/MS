package miaoshaproject.service.impl;

import com.alibaba.druid.util.StringUtils;
import miaoshaproject.dao.UserDOMapper;
import miaoshaproject.dao.UserPasswordDOMapper;
import miaoshaproject.dataobject.UserDO;
import miaoshaproject.dataobject.UserPasswordDO;
import miaoshaproject.error.BussinessException;
import miaoshaproject.error.EmBusinessError;
import miaoshaproject.service.UserService;
import miaoshaproject.service.model.UserModel;
import miaoshaproject.validator.ValidationResult;
import miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * @author huang
 * @data 17:45:49
 * @description
 */
@Service
public class UserServiceImpl  implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;
    @Override
    public UserModel getUserByID(Integer id) {
        //调用userdomapper获取对应的用户的dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO==null){
            return null;
        }
        //通过用户ID获取对应用户加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO,userPasswordDO);

    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BussinessException {
        if(userModel==null){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//                ||userModel.getGender()==null
//                ||userModel.getAge()==null
//                ||StringUtils.isEmpty(userModel.getTelphone())){
//            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//
//        }
        ValidationResult result = validator.validate(userModel);
        if(result.isHasErrors()){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }


        //实现MODEL到DATAOBJECT方法
        UserDO userDO = convertFromModel(userModel);
        try{
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException ex){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已重复注册");
        }
        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BussinessException {
        //通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if (userDO==null){
            throw new BussinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO,userPasswordDO);

        //比对用户信息类加密和密码是否和传输的密码相匹配
        if (!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BussinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);

        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO==null){
            return null;
        }

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPasswordDO!=null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }


        return userModel;
    }
}
