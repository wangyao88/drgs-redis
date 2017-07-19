package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * tb_disease_change
 */
@Data
public class SecondaryDisease implements Serializable{
	
	private static final long serialVersionUID = -7542065898782684803L;
	private String icdCode;
	private String priorFlag;

}
