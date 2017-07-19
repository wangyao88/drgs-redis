package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_HBZ_MCC	严重合并症表 标识为2---------->过滤附属诊断，选出合并症标识为2的，选出后去合并症排除表过滤
public class TbHbzMcc implements Serializable{
	
	private static final long serialVersionUID = -517253744474885768L;
	
	private String icd10Code;//从诊断编码(过滤后的)
	
}
