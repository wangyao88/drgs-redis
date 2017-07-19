package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_MDC_OPR	取手术MDC--->通过手术adrg和诊断的icdMdcType与手术的对应起来，能够对应的放入主手术列表
public class TbMdcOpr implements Serializable{

	private static final long serialVersionUID = 5853798376399071921L;
	
	private String oprgroupCode;//手术和诊断的adrg编码
	
	private String icdMdcType;//诊断类型
	
}
