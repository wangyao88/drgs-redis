package cn.com.cis.module.drgsgroup.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import lombok.Cleanup;
import lombok.Data;

@Data
//TB_OPR_CHARGE	过滤手术表------>过滤手术列表，把icd10Code为空和匹配到的手术取出，再把没有进行关联项目的手术过滤掉，
public class TbOprCharge implements Serializable{

	private static final long serialVersionUID = 4174141249529797807L;
	
	private String seqId;//关联手术ID（seqId相同的话，必须含有另一个itemId）
	
	private String itemId;//std_code(映射后的itemId)
	
	private String icd9Code;//手术编码
	
	private String icd10Code;//诊断编码（映射后的）
	
	private HashSet<String> itemIdSet = new HashSet<String>();
	
}
