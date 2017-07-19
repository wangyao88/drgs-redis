package cn.com.cis.module.drgsgroup.entity;

import lombok.Data;

/**
 * 诊断表td_drgs_disease
 */
@Data
public class Disease {
	
	private Long groupNo;
	private String hisId;
	private String diseaseId;
	private boolean flag;
	private DrgsData drgsData;
}
