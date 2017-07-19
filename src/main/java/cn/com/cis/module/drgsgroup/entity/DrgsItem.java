package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * TD_DRGS_ITEM
 */
@Data
public class DrgsItem implements Serializable{

	private static final long serialVersionUID = -9131693052869739324L;

	// 分组号
	private long groupNo;

	// 主单ID
	private String hisId;
	
	private String stdCode;
	
	private String description;
	
	private DrgsData drgsData;
	
}
