package cn.com.cis.cache.redis.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.com.cis.module.drgsgroup.entity.RedisBean;

@Service
public class RedisOperationService {
	
	private static final String NULL = "";
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public Object getMapValue(String mapKey,String key){
		key = StringUtils.isEmpty(key) ? NULL : key.toUpperCase();
		return redisTemplate.opsForHash().get(mapKey, key);
	}
	
	public String getMapStringValue(String mapKey,String key){
		key = StringUtils.isEmpty(key) ? NULL : key.toUpperCase();
		Object result = redisTemplate.opsForHash().get(mapKey, key);
		return result == null ? "" : String.valueOf(result);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getMapListStringValue(String mapKey, String key) {
		key = StringUtils.isEmpty(key) ? NULL : key.toUpperCase();
		Object result = redisTemplate.opsForHash().get(mapKey, key);
		return result == null ? new ArrayList<String>(): (List<String>)result;
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getMapSetStringValue(String mapKey, String key) {
		key = StringUtils.isEmpty(key) ? NULL : key.toUpperCase();
		Object result = redisTemplate.opsForHash().get(mapKey, key);
		return result == null ? new HashSet<String>(): (Set<String>)result;
	}
	
	@SuppressWarnings("unchecked")
	public Set<RedisBean> getMapSetRedisBeanValue(String mapKey, String key) {
		key = StringUtils.isEmpty(key) ? NULL : key.toUpperCase();
		Object result = redisTemplate.opsForHash().get(mapKey, key);
		return result == null ? new HashSet<RedisBean>(): (HashSet<RedisBean>)result;
	}
	
	public boolean mapContainsStringKey(String mapKey,String key){
		key = StringUtils.isEmpty(key) ? NULL : key.toUpperCase();
		return redisTemplate.opsForHash().hasKey(mapKey, key);
	}
	
	public boolean setContains(String setKey,Object object){
		return redisTemplate.opsForSet().isMember(setKey, object);
	}
	
	public boolean setContainsStringValue(String setKey,String value){
		value = StringUtils.isEmpty(value) ? value : value.toUpperCase();
		return redisTemplate.opsForSet().isMember(setKey, value);
	}

	public void convertAndSend(String channel, Object message) {
		redisTemplate.convertAndSend(channel, message);
	}
}
