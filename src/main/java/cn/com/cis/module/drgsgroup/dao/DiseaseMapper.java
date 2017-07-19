package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import cn.com.cis.module.drgsgroup.entity.Disease;
import cn.com.cis.module.drgsgroup.entity.DrgsData;

public interface DiseaseMapper {

	List<Disease> selectAllDiseasesByGroupNoAndHisId(DrgsData drgsData);
}
