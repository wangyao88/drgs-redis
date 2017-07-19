package cn.com.cis.cache;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import cn.com.cis.module.drgsgroup.entity.COMMTbDrgsgroup;
import cn.com.cis.module.drgsgroup.entity.IntermediateResult;

@Data
public class DrgsGroupResultCache {
	
	private List<COMMTbDrgsgroup> cOMMTbDrgsgroups = new ArrayList<COMMTbDrgsgroup>();
	private List<IntermediateResult> intermediateResults = new ArrayList<IntermediateResult>();

	private DrgsGroupResultCache() {
	}

	public static class Singleton {
		public static DrgsGroupResultCache drgsGroupResultCache = new DrgsGroupResultCache();
	}

	public static DrgsGroupResultCache getInstance() {
		return Singleton.drgsGroupResultCache;
	}
	
	public void setCache(List<COMMTbDrgsgroup> results, IntermediateResult intermediateResult){
		DrgsGroupResultCache.getInstance().setCOMMTbDrgsgroups(results);
		List<IntermediateResult> intermediateResults = new ArrayList<IntermediateResult>();
		intermediateResults.add(intermediateResult);
		DrgsGroupResultCache.getInstance().setIntermediateResults(intermediateResults);
	}
	
}
