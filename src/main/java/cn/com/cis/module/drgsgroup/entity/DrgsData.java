package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import lombok.Data;

@Data
public class DrgsData implements Serializable{
	private static final long serialVersionUID = -9197478986394202990L;
	//分组号
	private Long groupNo;
	
	//主单ID
 	private String hisId;
 	
 	//参保人性别
 	private String patientSex;
 	
 	//参保人年龄类型
 	private Integer ageType;
 	
 	//参保人体重类型，原先的loadWeightType
 	private Integer weightType;
 	
 	//上传标志 1-上传（病例） 0-结算  原先的isCase
 	private Integer loadFlag;
 	
 	//上传时间
 	private Date uploadDate;
 	
 	//结算日期
 	private Date billDate;
 	
 	//分组日期
 	private Date groupDate;
 	
 	//按床日标志
 	private Integer longterm = 0;
 	
 	//根据groupNo+hisId 查询诊断表（用flag判断主从诊断）
 	private List<Disease> diseases = new ArrayList<Disease>();
 	
 	//备选主诊断
 	private SecondaryDisease secondaryDisease;
 	
 	//手术列表 根据groupNo+hisId 查询手术表 （用flag判断主和非主手术）
 	private List<Operation> subOperations = new ArrayList<Operation>();
 	
 	//主手术列表
 	private List<Operation> mainOperations = new ArrayList<Operation>();
 	
 	//中间结果
 	private IntermediateResult intermediateResult = new IntermediateResult();
 	
 	//是否为单条分组数据
 	private boolean isSingleDataGroup = false;
 	
 	//呼吸机项目id
 	private HashSet<String> mccStdCodes = new HashSet<String>();
 	
 	//其他因素项目id
 	private HashSet<String> otherFactorStdCodes = new HashSet<String>();
 	
 	private boolean isNeedReturnResult = false;
 	
}