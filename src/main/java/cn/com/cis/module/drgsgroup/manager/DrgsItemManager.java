package cn.com.cis.module.drgsgroup.manager;

import java.util.List;

import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.DrgsItem;
import cn.com.cis.utils.ServiceAndMapperUtil;

public class DrgsItemManager {

	private DrgsData drgsData;

	private DrgsItemManager(DrgsData drgsData) {
		this.drgsData = drgsData;
	}

	public static DrgsItemManager newManager(DrgsData drgsData) {
		return new DrgsItemManager(drgsData);
	}

	public void getDrgsItemData(){
		List<DrgsItem> drgsItems = ServiceAndMapperUtil.getService().getDrgsItemService().getAllDrgsItems(drgsData);
		if(drgsItems!=null && drgsItems.size()>0){
			setDrgsItemForMcc(drgsItems);
			drgsItemsForFactor(drgsItems);
		}
	}
	
	private void setDrgsItemForMcc(List<DrgsItem> drgsItems){
		List<DrgsItem> drgsItemsForMCC = ServiceAndMapperUtil.getService().getDrgsItemService().filterDrgsItemsForMCC(drgsItems);
		for(DrgsItem drgsItem : drgsItemsForMCC){
			drgsData.getMccStdCodes().add(drgsItem.getStdCode());
		}
	}
	
	private void drgsItemsForFactor(List<DrgsItem> drgsItems){
		List<DrgsItem> drgsItemsForFactorItemId = ServiceAndMapperUtil.getService().getDrgsItemService().filterDrgsItemsForFactorItemId(drgsItems);
		for(DrgsItem drgsItem : drgsItemsForFactorItemId){
			drgsData.getOtherFactorStdCodes().add(drgsItem.getStdCode());
		}
	}
}
