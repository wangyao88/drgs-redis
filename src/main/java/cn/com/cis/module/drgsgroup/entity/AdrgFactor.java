package cn.com.cis.module.drgsgroup.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AdrgFactor {
	
	private String adrg;
	private List<String> factorList = new ArrayList<String>();

}
