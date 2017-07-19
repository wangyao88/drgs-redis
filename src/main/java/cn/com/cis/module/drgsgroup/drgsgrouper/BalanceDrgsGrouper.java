package cn.com.cis.module.drgsgroup.drgsgrouper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cn.com.cis.cache.DataCache;
import cn.com.cis.common.utils.StringUtil;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.Operation;
import cn.com.cis.utils.ServiceAndMapperUtil;

import com.google.common.collect.ComparisonChain;

public class BalanceDrgsGrouper extends DrgsGrouper{

	public BalanceDrgsGrouper(DrgsData drgsData) {
		super(drgsData);
	}

	@Override
	public void getMainOperation(Set<String> diseaseMdcAdrgSet) {
		//找出诊断的adrg对应的mdcType
		Set<String> diseaseMdcSet = getMdcSet(diseaseMdcAdrgSet);
		if(!diseaseMdcSet.isEmpty()){//若诊断有mdc
			StringBuilder mdc = new StringBuilder();
			for(String diseaseMdc : diseaseMdcSet){
				mdc.append(diseaseMdc).append(",");
			}
			mdc.deleteCharAt(mdc.length()-1);
			drgsData.getIntermediateResult().setDiseaseMdc(mdc.toString());
			//用手术的adrg对应的mdcType与诊断的mdcType匹配，过滤主手术列表
			filterMainOprListByMdc(diseaseMdcSet);
			diseaseMdcAdrgSet.clear();
			diseaseMdcSet.clear();
			//通过1.mainOprFirst 为true 2.cost 降序 3.item_date升序 4.icd_10不为空 5.adrg升序 排序出一条主手术
			selectMainOperation();
		}
	}
	
	private void selectMainOperation() {
		List<Operation> mainOperations = drgsData.getMainOperations();
		Collections.sort(mainOperations, new Comparator<Operation>() {
            @Override
            public int compare(Operation o1, Operation o2) {
            	//false为1，true为0，升序排列
            	int o1MainOpr = 1;
            	if(o1.isFlag()){
            		o1MainOpr = 0;
            	}
            	int o2MainOpr = 1;
            	if(o2.isFlag()){
            		o2MainOpr = 0;
            	}
            	//为空=1 非空=0，升序排列
            	String o1Icd10 = "0";
            	String o2Icd10 = "0";
            	if(StringUtil.isNull(o1.getIcd10()) && StringUtil.isNull(o2.getIcd10())){
            		o1Icd10 = "1";
            		o2Icd10 = "1";
            	}
            	if(!StringUtil.isNull(o1.getIcd10()) && StringUtil.isNull(o2.getIcd10())){
            		o1Icd10 = "0";
            		o2Icd10 = "1";
            	}
            	if(StringUtil.isNull(o1.getIcd10()) && !StringUtil.isNull(o2.getIcd10())){
            		o1Icd10 = "1";
            		o2Icd10 = "0";
            	}
            	if(!StringUtil.isNull(o1.getIcd10()) && !StringUtil.isNull(o2.getIcd10())){
            		o1Icd10 = o1.getIcd10();
            		o2Icd10 = o2.getIcd10();
            	}
            	String o1adrg = "0";
            	String o2adrg = "0";
            	if(StringUtil.isNull(o1.getAdrg()) && StringUtil.isNull(o2.getAdrg())){
            		o1adrg = "1";
            		o2adrg = "1";
            	}
            	if(!StringUtil.isNull(o1.getAdrg()) && StringUtil.isNull(o2.getAdrg())){
            		o1adrg = "0";
            		o2adrg = "1";
            	}
            	if(StringUtil.isNull(o1.getAdrg()) && !StringUtil.isNull(o2.getAdrg())){
            		o1adrg = "1";
            		o2adrg = "0";
            	}
            	if(!StringUtil.isNull(o1.getAdrg()) && !StringUtil.isNull(o2.getAdrg())){
            		String[] o1adrgs = o1.getAdrg().split("#");
            		String[] o2adrgs = o2.getAdrg().split("#");
        			if(o1adrgs[0].compareTo(o2adrgs[0]) > 0){
        				o1adrg = "1";
                		o2adrg = "0";
        			}else if(o1adrgs[0].compareTo(o2adrgs[0]) < 0){
        				o1adrg = "0";
                		o2adrg = "1";
        			}else{
        				o1adrg = "0";
                		o2adrg = "0";
        			}
            	}
            	//o1在前为升序，o2在前为降序
                return ComparisonChain.start()
                        .compare(o1MainOpr, o2MainOpr)
                        .compare(o2.getCosts(),o1.getCosts())
                        .compare(o1.getItemDate(), o2.getItemDate())
                        .compare(o1Icd10, o2Icd10)
                        .compare(o1adrg,o2adrg)
                        .compare(o1.getIcd9(), o2.getIcd9())
                        .result();
            }
        });
		
		for(int i = 0 ; i< mainOperations.size() ; i ++){
			if(i==0){
				continue;
			}else{
				mainOperations.remove(i);
				i--;
			}
		}
		drgsData.setMainOperations(mainOperations);
	}

	private void filterMainOprListByMdc(Set<String> diseaseMdcSet) {
		//取出预备的主手术列表
		List<Operation> mainOperations = drgsData.getMainOperations();
		//遍历主手术列表
		for(int i = 0; i < mainOperations.size() ; i++){
			Operation operation = mainOperations.get(i);
			boolean mainOprIsRemove = true;
			//如果手术对应的adrg为空,则该手术不能作为主手术,从主手术列表去除
			if(StringUtil.isEmpty(operation.getAdrg())){
				mainOperations.remove(i);
				i--;
				continue;
			}
			//取出当前手术的所有adrg
			String[] oprAdrgArr = operation.getAdrg().split("#");
			//把当前手术的adrg存入set中
			Set<String> oprAdrgSet = new HashSet<String>(new ArrayList<String>(Arrays.asList(oprAdrgArr)));
			//用当前手术的adrg获得当前手术的Mdc
			Set<String> oprMdcSet = getMdcSet(oprAdrgSet);
			//诊断Mdc不包含的手术Mdc
			Set<String> unContainsMdcSet = new HashSet<String>();
			//遍历每个手术的Mdc,当诊断的Mdc包含手术的Mdc，则此手术可以作为主手术，如果不包含,则把oprMdc存入unContainsMdcSet
			for(String oprMdc : oprMdcSet){
				if(diseaseMdcSet.contains(oprMdc)){
					mainOprIsRemove = false;
				}else{
					unContainsMdcSet.add(oprMdc);
				}
			}
			//若都不包含，则此手术不能作为主手术
			if(mainOprIsRemove){
				mainOperations.remove(i);
				i--;
				continue;
			}
			
			//遍历当前手术对应的adrg,如果要排除的mdc中完全包含当前adrg对应的mdc,则把当前adrg放入adrg排除列表exceptAdrgList
			//要排除的adrg列表
			List<String> exceptAdrgList = new ArrayList<String>();
			//过滤每个adrg
			for(String adrg : oprAdrgSet){
				String mdcStrings = ServiceAndMapperUtil.getService().getRedisOperationService().getMapStringValue(DataCache.MDC_MAP, adrg.toUpperCase());
				HashSet<String> tempAdrgMdcSet = new HashSet<String>();
				//若找到的MDC不为空
				if(!StringUtils.isEmpty(mdcStrings)){
					String[] mdcArr = mdcStrings.split("#");
					tempAdrgMdcSet.addAll(new ArrayList<String>(Arrays.asList(mdcArr)));
					//如果要排除的mdc中完全包含当前adrg对应的mdc
					if(unContainsMdcSet.containsAll(tempAdrgMdcSet)){
						exceptAdrgList.add(adrg);
					}
				}
			}
			
			//从没有被排除的主手术的adrg列表中去除要排除的adrg
			List<String> adrgList = new ArrayList<String>(oprAdrgSet);
			adrgList.removeAll(exceptAdrgList);
			
			//将排除后的adrg按照升序排列(为了选取主手术时,按照第一格adrg升序排序)
			Collections.sort(adrgList, new Comparator<String>() {
	            @Override
	            public int compare(String o1, String o2) {
	            	//o1在前为升序，o2在前为降序
	                return ComparisonChain.start()
	                        .compare(o1, o2)
	                        .result();
	            }
	        });
			String adrgs = "";
			for(String adrg : adrgList){
				adrgs += "#" + adrg;
			}
			if(!adrgs.equals("")){
				adrgs = adrgs.substring(1);
			}
			operation.setAdrg(adrgs);
		}
		drgsData.setMainOperations(mainOperations);
	}

	@Override
	public void setMainOperationFlag() {
		for(Operation operation : drgsData.getSubOperations()){
			String key = (operation.getIcd9()+"#"+drgsData.getIntermediateResult().getIcd10()).toUpperCase();
			if(ServiceAndMapperUtil.getService().getRedisOperationService().setContains(DataCache.MAIN_OPR_FIRST_SET, key)){
				operation.setFlag(true);
			}else{
				operation.setFlag(false);
			}
		}
	}

}
