package cn.com.cis.module.drgsgroup.drgsgrouper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ComparisonChain;

import cn.com.cis.cache.DataCache;
import cn.com.cis.common.utils.StringUtil;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.Operation;
import cn.com.cis.utils.ServiceAndMapperUtil;

public class CaseDrgsGrouper extends DrgsGrouper{

	public CaseDrgsGrouper(DrgsData drgsData) {
		super(drgsData);
	}

	@Override
	public void getMainOperation(Set<String> diseaseMdcAdrgSet) {
		drgsData.setMainOperations(ServiceAndMapperUtil.getService().getOperationService().filterMainOperation(drgsData.getMainOperations()));
		
		if(drgsData.getMainOperations().isEmpty()){
			return;
		}
		//根据码表过滤主手术
		Operation mainOperation = drgsData.getMainOperations().get(0);
		if(StringUtil.isEmpty(mainOperation.getAdrg())){
			return;
		}
		//取出主手术的所有adrg
		String[] oprAdrgArr = mainOperation.getAdrg().split("#");
		//把当前手术的adrg存入set中
		Set<String> oprAdrgSet = new HashSet<String>(new ArrayList<String>(Arrays.asList(oprAdrgArr)));
		//用当前手术的adrg获得当前手术的Mdc
		Set<String> oprMdcSet = getMdcSet(oprAdrgSet);
		//诊断Mdc不包含的手术Mdc
		Set<String> unContainsMdcSet = new HashSet<String>();
		//遍历每个手术的Mdc,当诊断的Mdc包含手术的Mdc，则此手术可以作为主手术，如果不包含,则把oprMdc存入unContainsMdcSet
		Set<String> diseaseMdcSet = getMdcSet(diseaseMdcAdrgSet);
		for(String oprMdc : oprMdcSet){
			if(!diseaseMdcSet.contains(oprMdc)){
				unContainsMdcSet.add(oprMdc);
			}
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
		mainOperation.setAdrg(adrgs);
	}

	@Override
	public void setMainOperationFlag() {
		
	}
	
}
