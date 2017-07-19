package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_HBZ_CHARGE_MCC	取合并症（判断明显表是否上了呼吸机）-------->用明细表服务项id通过此表过滤下，如果能找到，则属于严重合并症，标识为2
public class TbHbzChargeMcc implements Serializable{

	private static final long serialVersionUID = -3130066524405264871L;
	
	private String clientCode;//映射前的项目编码
	
	//private String areaCode;//地区编码
	
}
