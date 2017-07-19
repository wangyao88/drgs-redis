package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_HBZ_EXCEPTION	合并症排除表------->除呼吸机选出的合并症，都来此过滤排除，通过附属诊断代码+合并症类型
public class TbHbzException implements Serializable{

	private static final long serialVersionUID = 6857472061821459610L;
	
	private String diseaseIcd10Id;//主诊断代码
	
	private String exceptType;//合并症类型

	private String exceptIcd10Id;//附属诊断代码(映射后的)
	
}
