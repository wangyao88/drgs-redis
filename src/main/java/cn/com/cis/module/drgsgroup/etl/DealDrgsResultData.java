package cn.com.cis.module.drgsgroup.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealDrgsResultData extends EtlDrgsGroupData {

	private String groupNo;
	private String date;

	public DealDrgsResultData(String groupNo, String date) {
		this.groupNo = groupNo;
		this.date = date;
	}

	@Override
	protected List<String> getEtlFileName() {
		List<String> files = new ArrayList<String>();
		files.add("DrgsResult.xml");
		return files;
	}

	@Override
	protected Map<String, String> getEtlParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("GROUP_NO", groupNo);
		parameter.put("DATE", date);
		return parameter;
	}

}