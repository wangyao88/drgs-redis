package cn.com.cis.module.drgsgroup.entity;

import java.util.Date;

import lombok.Data;

/**
 * 分组记录表td_drgs_controll
 */
@Data
//td_drgs_controll
public class DrgsGroupRecord {
	
	private Long groupNo;
	
	//分组开始时间
	private Date groupDate;
	
	//分组数据条数
	private Integer groupCount;
	
	//分组耗时
	private long groupTime;
	
	//分组状态0:待分组，1.正在分组 2.分组完成
	private String groupState;
	
	//状态描述
	private String message;
	
}
