package miaoshaproject.error;

/**
 * @author huang
 * @data 20:37:25
 * @description
 */
public class BussinessException extends Exception implements CommonError {
    private CommonError commonError;
    //直接接收EmBussinessError的传参用户构造业务异常
    public BussinessException(CommonError commonError){
        super();
        this.commonError = commonError;
    }

    //接收自定义errMsg的方式构造业务异常
    public BussinessException(CommonError commonError,String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }



    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
