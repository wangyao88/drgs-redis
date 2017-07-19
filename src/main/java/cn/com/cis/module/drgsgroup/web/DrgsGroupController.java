package cn.com.cis.module.drgsgroup.web;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.com.cis.cache.DrgsGroupResultCache;
import cn.com.cis.module.drgsgroup.entity.DrgsGroupType;
import cn.com.cis.module.drgsgroup.service.DrgsGroupService;
import cn.com.cis.module.drgsgroup.service.DrgsItemService;

@RestController
@RequestMapping(value="/drgsGroup")
public class DrgsGroupController {

	@Autowired
	private DrgsGroupService drgsGroupService;
	@Autowired
	private DrgsItemService drgsItemService;
	
	@RequestMapping(value="/drgsGroupForBalance")
	public void drgsGroupForBalance() throws ParseException, InterruptedException{
		drgsGroupService.drgsGroupForBalance(DrgsGroupType.ALL,null);
	}
	
	@RequestMapping(value="/drgsGroupForDayAgain/{date}")
	public void drgsGroupForDayAgain(@PathVariable String date) throws ParseException, InterruptedException{
		drgsGroupService.drgsGroupForBalance(DrgsGroupType.DAY,date);
	}
	
	@RequestMapping(value="/drgsGroupForMonthAgain/{date}")
	public void drgsGroupForMonthAgain(@PathVariable String date) throws ParseException, InterruptedException{
		drgsGroupService.drgsGroupForBalance(DrgsGroupType.MONTH,date);
	}
	
	@RequestMapping(value="/drgsGroupByHisId")
	public ModelAndView drgsGroupByHisId(){
		ModelAndView modelAndView = new ModelAndView("drgs_group_result");
		return modelAndView;
	}
	
	@RequestMapping(value="/drgsGroupByHisIdForReslutList/{hisId}")
	@ResponseBody
	public Object drgsGroupByHisIdForReslutList(@PathVariable String hisId,HttpServletResponse response){
		drgsGroupService.drgsGroupByHisId(hisId);
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("total", DrgsGroupResultCache.getInstance().getCOMMTbDrgsgroups().size());
		map.put("rows", DrgsGroupResultCache.getInstance().getCOMMTbDrgsgroups());
		return map;
	}
	
	@RequestMapping(value="/drgsGroupByHisIdForIntermediateResult/{hisId}")
	@ResponseBody
	public Object drgsGroupByHisIdForIntermediateResult(@PathVariable String hisId,HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("total", DrgsGroupResultCache.getInstance().getIntermediateResults().size());
		map.put("rows", DrgsGroupResultCache.getInstance().getIntermediateResults());
		return map;
	}
	
	@RequestMapping(value="/drgsGroupByParameters/{hisId}/{icd9}/{unMainOpr}/{icd10}/{subDisease}")
	public void drgsGroupByParameters(@PathVariable String hisId,@PathVariable String icd9,@PathVariable String unMainOpr,@PathVariable String icd10,@PathVariable String subDisease){
		drgsGroupService.drgsGroupByParameters(hisId, icd9, unMainOpr, icd10, subDisease);
	}
	
	@RequestMapping(value="/reInitAllCache")
	public void reInitAllCache(){
//		cacheService.reInitAllCache();
	}
	
	@RequestMapping(value="/drgsGroupForMonthAgainWithoutPreparedDatas/{date}/{groupNo}")
	public void drgsGroupForMonthAgainWithoutPreparedDatas(@PathVariable String date,@PathVariable String groupNo) throws ParseException, InterruptedException{
		drgsGroupService.drgsGroupForBalanceWithoutPreparedDatas(date,groupNo);
	}

}
