package cn.com.cis.module.drgsgroup.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.com.cis.module.drgsgroup.entity.SecondaryDisease;

import com.google.common.collect.ComparisonChain;

@Service
public class SecondaryDiseaseService {
	
	public SecondaryDisease sortScondaryDiseassesByPriod(List<SecondaryDisease> secondaryDiseases){
		int  size = secondaryDiseases.size();
		if(size == 1){
			return secondaryDiseases.get(0);
		}
		for(int i = 0; i < size; i++){
			SecondaryDisease secondaryDisease = secondaryDiseases.get(i);
			if(StringUtils.isEmpty(secondaryDisease.getPriorFlag())){
				secondaryDiseases.remove(i);
				i--;
			}
		}
		Collections.sort(secondaryDiseases, new Comparator<SecondaryDisease>() {
			@Override
            public int compare(SecondaryDisease o1, SecondaryDisease o2) {
            	//o1在前为升序，o2在前为降序
                return ComparisonChain.start()
                        .compare(o1.getPriorFlag(), o2.getPriorFlag())
                        .result();
            }
        });
		return secondaryDiseases.get(0);
	}

}
