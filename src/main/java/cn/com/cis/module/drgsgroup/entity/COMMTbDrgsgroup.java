package cn.com.cis.module.drgsgroup.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.Cleanup;
import lombok.Data;
import cn.com.cis.enums.DrgType;

@Data
//COMM.TB_DRGSGROUP	drgs分组规则表------->通过adrg(oprgroupCode，通过主手术和主诊断的adrg)得到drgs()分组编码的list，选出一个匹配项最多的drgs作为结果
public class COMMTbDrgsgroup implements Serializable{
	
	private static final long serialVersionUID = 1073289711742641147L;
	
	private String drgCnCode;//DRG编码
	
	private String drgCnName;//DRG名称
	
	private DrgType drgType;//DRG类别
	
	//private String pccl;//暂未启用
	
	private String oprgroupCode;//adrg
	
	private String ccFlag;//合并症
	
	private String otherFactorId1;//其他因素1
	
	private String otherFactorId2;//其他因素2
	
	private String otherFactorId3;//其他因素3
	
	private String drgsSex;//性别
	
	private String drgsAge;//年龄
	
	private String drgsOutcome;//出院方式
	
	private String drgsWeight;//体重
	
	private String drgsTrtime;//治疗时长
	
	private String flag;//是否有效
	
	//private String drgsGestnlAge;//暂未启用
	
	private String dfxzycsFlag;
	
	private Integer defaultAdrg;//0:不是默认的adrg,1:是默认的adrg
	
	private Integer matchCount = 0;
	
}
