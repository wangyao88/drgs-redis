package cn.com.cis.module.drgsgroup.aop;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import cn.com.cis.utils.DateUtil;

@Slf4j
@Aspect
@Component
public class CostTimeCounter {

	@Pointcut("execution(* cn.com.cis.cache.redis.service.RedisCacheService.*(..))")
	//"execution(* cn.com.cis.cache.service.CacheService.*(..))"
	private void anyMethod() {
	}

	@Around("anyMethod()")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
		long start = DateUtil.getNowTime();
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		Object object = pjp.proceed();// 执行该方法
		log.info("{}耗时:{}ms",method.getName(),DateUtil.getTimeBetweenStartAndNow(start));
		return object;
	}
}
