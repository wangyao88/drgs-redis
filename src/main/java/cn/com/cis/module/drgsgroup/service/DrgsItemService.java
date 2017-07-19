package cn.com.cis.module.drgsgroup.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.cis.enums.DescriptionFlag;
import cn.com.cis.module.drgsgroup.dao.DrgsItemMapper;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.DrgsItem;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Service
public class DrgsItemService {

	@Autowired
	private DrgsItemMapper drgsItemMapper;
	
	public List<DrgsItem> getAllDrgsItems(DrgsData drgsData){
		return drgsItemMapper.getAllDrgsItems(drgsData);
	}
	
	public List<DrgsItem> filterDrgsItemsForMCC(List<DrgsItem> drgsItems){
		Predicate<DrgsItem> drgsItemsForMCCFilter = new Predicate<DrgsItem>() {
            @Override
            public boolean apply(DrgsItem drgsItem) {
                return drgsItem.getDescription().equals(DescriptionFlag.MCC.toString());
            }
        };

        FluentIterable<DrgsItem> drgsItemsForMCC = FluentIterable.from(drgsItems).filter(drgsItemsForMCCFilter);
        if(!drgsItemsForMCC.isEmpty()){
        	return drgsItemsForMCC.toList();
        }
        return new ArrayList<DrgsItem>();
	}
	
	public List<DrgsItem> filterDrgsItemsForFactorItemId(List<DrgsItem> drgsItems){
		Predicate<DrgsItem> drgsItemsForFactorItemIdFilter = new Predicate<DrgsItem>() {
            @Override
            public boolean apply(DrgsItem drgsItem) {
                return drgsItem.getDescription().equals(DescriptionFlag.FACTOR_ITEM_ID.toString());
            }
        };

        FluentIterable<DrgsItem> drgsItemsForFactorItemId = FluentIterable.from(drgsItems).filter(drgsItemsForFactorItemIdFilter);
        if(!drgsItemsForFactorItemId.isEmpty()){
        	return drgsItemsForFactorItemId.toList();
        }
        return new ArrayList<DrgsItem>();
	}
}
