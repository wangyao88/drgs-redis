package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_HBZ_CC	普通合并症表 标识为1---------->过滤附属诊断，选出合并症标识为1的，选出后去合并症排除表过滤
public class TbHbzCc implements Serializable{
	
	private static final long serialVersionUID = 7615351450322348509L;

	private String icd10Code;//主诊断代码(映射后的)
	
}
