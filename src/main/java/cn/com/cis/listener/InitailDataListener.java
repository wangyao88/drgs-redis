package cn.com.cis.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.com.cis.cache.redis.service.RedisCacheService;

@Slf4j
public class InitailDataListener implements ServletContextListener{
	
	private WebApplicationContext springContext; 
	
	@Autowired
	private RedisCacheService redisCacheService;
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		initCacheData(event);
//		DrgsGroupTaskMain.startDrgsGroupTask();
	}

	private void initCacheData(ServletContextEvent event) {
		springContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		if (springContext != null) {
			redisCacheService = (RedisCacheService) springContext.getBean("redisCacheService");
			try {
				redisCacheService.initAllData();
				log.info("缓存初始化成功");
			} catch (Exception e) {
				log.error("缓存初始化失败!错误信息:{}",e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
