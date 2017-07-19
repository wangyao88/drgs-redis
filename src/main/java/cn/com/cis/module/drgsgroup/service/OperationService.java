package cn.com.cis.module.drgsgroup.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.Operation;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Service
public class OperationService {
	
	public List<Operation> filterMainOperation(List<Operation> operations){
		Predicate<Operation> mainOperationFilter = new Predicate<Operation>() {
            @Override
            public boolean apply(Operation operation) {
                return operation.isFlag();
            }
        };

        FluentIterable<Operation> mainOperations = FluentIterable.from(operations).filter(mainOperationFilter);
        if(!mainOperations.isEmpty()){
        	return mainOperations.toList();
        }
        return new ArrayList<Operation>();
	}
	
	public List<Operation> filterSubOperation(List<Operation> diseases){
		Predicate<Operation> subOperationFilter = new Predicate<Operation>() {
            @Override
            public boolean apply(Operation disease) {
                return !disease.isFlag();
            }
        };

        FluentIterable<Operation> subOperations = FluentIterable.from(diseases).filter(subOperationFilter);
        if(!subOperations.isEmpty()){
        	return subOperations.toList();
        }
        return new ArrayList<Operation>();
	}

	public List<Operation> removeMainOperationFromSubOperations(DrgsData drgsData, List<Operation> oprList) {
		List<Operation> subOperations = new ArrayList<Operation>();
		if(!drgsData.getMainOperations().isEmpty()){
			final Operation mainOpr = drgsData.getMainOperations().get(0);
			Predicate<Operation> subOprFilter = new Predicate<Operation>() {
	            @Override
	            public boolean apply(Operation operation) {
	                return !mainOpr.getIcd9().equals(operation.getIcd9());
	            }
	        };

	        FluentIterable<Operation> unMainOprList = FluentIterable.from(oprList).filter(subOprFilter);
	        if(!unMainOprList.isEmpty()){
	        	subOperations = unMainOprList.toList();
	        }
		}
		return subOperations;
	}

}
