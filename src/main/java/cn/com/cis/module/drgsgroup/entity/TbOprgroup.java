package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_OPRGROUP	手术adrg映射表 ------>通过手术编码（icd9Code）获取诊断adrg
public class TbOprgroup implements Serializable{

	private static final long serialVersionUID = 5618134123038790742L;
	
	private String icd9Code;//手术编码
	
	private String oprgroupCode;//adrg编码
	
	

}
