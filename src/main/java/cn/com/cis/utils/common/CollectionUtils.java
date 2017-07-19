package cn.com.cis.utils.common;

import java.util.List;

/**
 * Created by wanglei on 16/6/27.
 * 集合工具类
 */
public class CollectionUtils {
	
	/**
	 * 用于从数据库结果集中取单独对象（从list中获取第一个对象）
	 * @param lists
	 * @return
	 */
    public static <T> T getOneElementOrNull(List<T> lists) {

        if (null != lists && lists.size() >0) {
            return lists.get(0);
        } else {
            return null;
        }
    }
}
