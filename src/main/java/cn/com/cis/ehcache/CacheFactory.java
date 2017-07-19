package cn.com.cis.ehcache;

import org.apache.ibatis.cache.Cache;

public interface CacheFactory {
    Cache getCache(String id);
}