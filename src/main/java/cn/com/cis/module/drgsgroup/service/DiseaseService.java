package cn.com.cis.module.drgsgroup.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cn.com.cis.module.drgsgroup.entity.Disease;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Service
public class DiseaseService {
	
	public Disease filterMainDisease(List<Disease> diseases){
		Predicate<Disease> mainDiseaseFilter = new Predicate<Disease>() {
            @Override
            public boolean apply(Disease disease) {
                return disease.isFlag();
            }
        };

        FluentIterable<Disease> mainDiseases = FluentIterable.from(diseases).filter(mainDiseaseFilter);
        if(!mainDiseases.isEmpty()){
        	return mainDiseases.get(0);
        }
        return new Disease();
	}
	
	public List<Disease> filterSubDisease(List<Disease> diseases){
		Predicate<Disease> subDiseaseFilter = new Predicate<Disease>() {
            @Override
            public boolean apply(Disease disease) {
                return !disease.isFlag();
            }
        };

        FluentIterable<Disease> subDiseases = FluentIterable.from(diseases).filter(subDiseaseFilter);
        if(!subDiseases.isEmpty()){
        	return subDiseases.toList();
        }
        return new ArrayList<Disease>();
	}

}
