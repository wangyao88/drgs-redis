package cn.com.cis.web.bean;

import lombok.Data;

@Data
public class ParameterActionBean {

    private String parameterRef;
    private String parameterName;
    private String parameterType;
    private String parameterValue;
    private String scope;
}
