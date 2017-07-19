package cn.com.cis.module.drgsgroup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CounterService {
	
	private static final String COUNTER_KEY = "count";
	private static final int INIT_VALUE = 0;
	private static final int INCREAMENT_STEP = 1;
	private static final int DECREAMENT_STEP = 1;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	public void startCount(){
		redisTemplate.opsForValue().set(COUNTER_KEY, INIT_VALUE);
	}
	
	public long increament(){
		return redisTemplate.opsForValue().increment(COUNTER_KEY, INCREAMENT_STEP);
	}
	
	public long decreament(){
		return redisTemplate.opsForValue().increment(COUNTER_KEY, DECREAMENT_STEP);
	}
	
	public void stopCount(){
		redisTemplate.delete(COUNTER_KEY);
	}

	public long increament(int batchNum) {
		return redisTemplate.opsForValue().increment(COUNTER_KEY, batchNum);
	}
	
	public long getCount(){
		return Long.parseLong(redisTemplate.opsForValue().get(COUNTER_KEY).toString());
	}

}
