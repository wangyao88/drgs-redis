package cn.com.cis.module.drgsgroup.drgsgrouper;

import cn.com.cis.module.drgsgroup.entity.DrgsData;

public class DrgsGrouperFactory {
	
    private DrgsGrouperFactory(){
		
	} 
	
	public static DrgsGrouperFactory getInstance(){
		return Singleton.drgsGrouperFactory;
	}
	
	private static class Singleton{
		private static final DrgsGrouperFactory drgsGrouperFactory = new DrgsGrouperFactory();
	}
	
	public DrgsGrouper getDrgsGrouper(DrgsData drgsData){
		if(drgsData.getLoadFlag() == 0){
			return new BalanceDrgsGrouper(drgsData);
		}
		return new CaseDrgsGrouper(drgsData);
	}

}
