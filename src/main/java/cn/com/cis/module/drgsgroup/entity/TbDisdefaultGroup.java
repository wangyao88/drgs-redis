package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_DISDEFAULT_GROUP	诊断默认优先选取表----->选取drgs结果（最后drgs分组的时候，从所有挑出的drgs中选取一个），其中一个环节通过此表过滤
public class TbDisdefaultGroup implements Serializable{
	
	private static final long serialVersionUID = -1906071600177401071L;
	
	private String icd10;//诊断映射后编码
	
	private String oprgroupCode;//adrg编码
	
}
