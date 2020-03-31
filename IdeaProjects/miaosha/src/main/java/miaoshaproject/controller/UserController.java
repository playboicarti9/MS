package miaoshaproject.controller;

import miaoshaproject.controller.viewobject.UserVO;
import miaoshaproject.error.BussinessException;
import miaoshaproject.error.EmBusinessError;
import miaoshaproject.response.CommonReturnType;
import miaoshaproject.service.impl.UserServiceImpl;
import miaoshaproject.service.model.UserModel;


import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author huang
 * @data 17:38:46
 * @description
 */

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户登陆接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                @RequestParam(name = "password")String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
     //入参校验
    if (org.apache.commons.lang3.StringUtils.isEmpty(telphone)||
            StringUtils.isEmpty(password)){
        throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
    }
    //用户登陆服务，校验用户登陆是否合法
    UserModel userModel = userService.validateLogin(telphone,this.EncodeByMd5(password));

    //将登陆凭证加入到用户登陆成功的session内
     this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
     this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

     return CommonReturnType.create(null);
    }



    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                    @RequestParam(name="otpCode")String otpCode,
                                    @RequestParam(name="name")String name,
                                    @RequestParam(name="gender")Integer gender,
                                    @RequestParam(name="age")Integer age,
                                    @RequestParam(name="password")String password ) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的OTPCODE相符合
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);
        return CommonReturnType.create(null);
    }
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;

    }


    //用户获取OTP短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){
        //按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将OTP验证码同对应手机用户关联
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        //将验证码通过短信通道发给用户，省略
        System.out.println("telphone="+telphone+"&otpCode="+otpCode);

        return CommonReturnType.create(null);
    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id")Integer id) throws BussinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserByID(id);

        //若获取用户对应信息不存在
        if(userModel==null){
            throw new BussinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }







}
