package miaoshaproject.error;

/**
 * @author huang
 * @data 20:21:38
 * @description
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);

}
