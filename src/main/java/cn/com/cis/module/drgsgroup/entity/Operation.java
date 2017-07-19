package cn.com.cis.module.drgsgroup.entity;

import java.util.Date;

import lombok.Data;

/**
 * 手术表 td_drgs_operation
 */
@Data
public class Operation {
	
	private Long groupNo;
	private String hisId;
	private String icd9;
	private boolean flag;
	private Float costs;
	private Date itemDate;
	private String adrg;
	private String icd10;
	private DrgsData drgsData;
	private String seqId;
}
