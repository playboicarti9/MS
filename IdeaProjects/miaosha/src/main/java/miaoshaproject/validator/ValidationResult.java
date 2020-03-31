package miaoshaproject.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huang
 * @data 17:30:17
 * @description
 */
public class ValidationResult {
    //校验结果是否有错
    private boolean hasErrors = false;
    //存放错误信息的map
    private Map<String,String> errorMsgMaap = new HashMap<>();

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMaap() {
        return errorMsgMaap;
    }

    public void setErrorMsgMaap(Map<String, String> errorMsgMaap) {
        this.errorMsgMaap = errorMsgMaap;
    }

    //实现通用的通用格式化字符串信息获取错误结果的msg方法
    public String getErrMsg(){
        return StringUtils.join(errorMsgMaap.values().toArray(),",");
    }
}
