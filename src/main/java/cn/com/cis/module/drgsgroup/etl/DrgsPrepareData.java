package cn.com.cis.module.drgsgroup.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.cis.module.drgsgroup.entity.DrgsGroupType;

public class DrgsPrepareData extends EtlDrgsGroupData {

	private DrgsGroupType type;
	private String date;

    public DrgsPrepareData(DrgsGroupType type, String date) {
		this.type = type;
		this.date = date;
	}

	@Override
	protected List<String> getEtlFileName() {
		List<String> files = new ArrayList<String>();
		if(DrgsGroupType.DAY.equals(type)) {
			files.add("DrgsDayControll.xml");
		}else if(DrgsGroupType.MONTH.equals(type)) {
			files.add("DrgsMonControll.xml");
		}else if(DrgsGroupType.ALL.equals(type)) {
			files.add("DrgsDayControll.xml");
			files.add("DrgsMonControll.xml");
		}
		return files;
	}

	@Override
	protected Map<String, String> getEtlParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("DATE", date);
		return parameter;
	}
}