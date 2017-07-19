package cn.com.cis.module.drgsgroup.etl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import scriptella.execution.EtlExecutor;
import scriptella.execution.EtlExecutorException;
import scriptella.execution.ExecutionStatistics;
import scriptella.util.IOUtils;

@Slf4j
public abstract class EtlDrgsGroupData {
	
	public void etlExecute() throws MalformedURLException, EtlExecutorException{
		List<String> files = getEtlFileName();
		for(String fileName : files){
			log.info("开始执行脚本{}",fileName);
			String filePath = DealDrgsResultData.class.getClassLoader().getResource(fileName).getPath();
	    	File file = new File(filePath);
	        URL url = IOUtils.toUrl(file);
	        EtlExecutor etlExecutor = EtlExecutor.newExecutor(url,getEtlParameter());
	        etlExecutor.setJmxEnabled(true);
	        ExecutionStatistics executionStatistics = etlExecutor.execute();
	        log.info("写入完成。共写入{}条数据。耗时{}ms",executionStatistics.getExecutedStatementsCount(),executionStatistics.getTotalTime());
		}
	}

	protected  abstract List<String> getEtlFileName(); 
	
	protected abstract Map<String,String> getEtlParameter();

}
