package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.DrgsItem;

public interface DrgsItemMapper {
	
	List<DrgsItem> getAllDrgsItems(DrgsData drgsData);

}
