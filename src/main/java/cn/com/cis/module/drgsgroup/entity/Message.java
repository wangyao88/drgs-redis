package cn.com.cis.module.drgsgroup.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Message {
	
	private String channel;
	private String content;
	private Date sendDate;
	
}
