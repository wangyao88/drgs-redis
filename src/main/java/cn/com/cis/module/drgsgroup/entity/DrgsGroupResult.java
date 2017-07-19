package cn.com.cis.module.drgsgroup.entity;

import java.util.Date;

import lombok.Data;

@Data
public class DrgsGroupResult {
	
	private Long groupNo;
	private String hisId;
	private Date uploadDate;
	private String drgsCode;
	private String icd10;
	private String primaryDiagnosisCode;
	private String icd9;
	private String unMainOprStr;
	private Date groupDate;
	private Integer loadFlag;
	private Integer matchCount;
}
