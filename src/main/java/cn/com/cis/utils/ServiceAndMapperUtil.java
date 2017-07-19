package cn.com.cis.utils;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.cis.cache.redis.service.RedisOperationService;
import cn.com.cis.module.drgsgroup.dao.DiseaseMapper;
import cn.com.cis.module.drgsgroup.dao.IIntermediateResultDao;
import cn.com.cis.module.drgsgroup.dao.IResultDataDao;
import cn.com.cis.module.drgsgroup.dao.OperationMapper;
import cn.com.cis.module.drgsgroup.service.CounterService;
import cn.com.cis.module.drgsgroup.service.DiseaseService;
import cn.com.cis.module.drgsgroup.service.DrgsGroupService;
import cn.com.cis.module.drgsgroup.service.DrgsItemService;
import cn.com.cis.module.drgsgroup.service.OperationService;
import cn.com.cis.module.drgsgroup.service.SecondaryDiseaseService;

@Data
@Component
public class ServiceAndMapperUtil {

	@Autowired
	private DiseaseService diseaseService;
	@Autowired
	protected OperationService operationService;
	@Autowired
	private OperationMapper operationMapper;
	@Autowired
	private DiseaseMapper diseaseMapper;
	@Autowired
	private DrgsGroupService drgsGroupService;
	@Autowired
	private SecondaryDiseaseService secondaryDiseaseService;
	@Autowired
	private DrgsItemService drgsItemService;
	@Autowired
	private IResultDataDao resultDataDao;
	@Autowired
	private IIntermediateResultDao intermediateResultDao;
	@Autowired
	private RedisOperationService redisOperationService;
	@Autowired
	private CounterService counterServic;
	
	
	public static ServiceAndMapperUtil getService() {
		return (ServiceAndMapperUtil) SpringContextUtil.getBean("serviceAndMapperUtil");
	}
	
	public static ServiceAndMapperUtil getMapper() {
		return (ServiceAndMapperUtil) SpringContextUtil.getBean("serviceAndMapperUtil");
	}
}
