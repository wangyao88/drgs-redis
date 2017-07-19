package cn.com.cis.web.bean;

import lombok.Data;

import java.util.Date;

@Data
public class ExcelActionBean {
    private String fileName;
    private String createTime;
    private long size;
}
