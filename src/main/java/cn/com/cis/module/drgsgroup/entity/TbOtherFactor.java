package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_OTHER_FACTOR	其他因素表------>通过adrg+[主手术，非主手术（icd9），主诊断，从诊断的ID（icd10）] 取三个其他因素
public class TbOtherFactor implements Serializable{
	
	private static final long serialVersionUID = 4529908164982151176L;

	private String oprgroupCode;//诊断或手术的adrg
	
	private String otherFactorId;//其他因素代码
	
	private String factorItemId;//主手术，非主手术（icd9），主诊断，从诊断的ID（icd10）

}
