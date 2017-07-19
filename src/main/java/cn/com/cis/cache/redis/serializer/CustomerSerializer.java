package cn.com.cis.cache.redis.serializer;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

public class CustomerSerializer implements RedisSerializer<Object>{

	static final byte[] EMPTY_ARRAY = new byte[0];
	private final Charset charset;

	public CustomerSerializer() {
		this(Charset.forName("UTF8"));
	}

	public CustomerSerializer(Charset charset) {
		Assert.notNull(charset);
		this.charset = charset;
	}

	@Override
	public byte[] serialize(Object object) throws SerializationException {// 序列化方法
		try {
			if(object instanceof String){
				return object.toString().getBytes(charset);
			}else if(object instanceof List || object instanceof Set || object instanceof HashSet){
				JSONArray array = JSONArray.fromObject(object);
				String jsonString = array.toString();
				return (jsonString == null ? EMPTY_ARRAY : jsonString.getBytes(charset));
			}
			JSONObject jsonObject = JSONObject.fromObject(object);
			String jsonString = jsonObject.toString();
			return (jsonString == null ? EMPTY_ARRAY : jsonString.getBytes(charset));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object deserialize(byte[] bytes) throws SerializationException { // 反序列化
		String objectStr = null;
		Object object = null;
		if (bytes == null) {
			return object;
		}
		try {
			objectStr = new String(bytes, charset); // byte数组转换为String
			if(!objectStr.contains("{")){
				return objectStr;
			}
			JSONObject jsonObject = JSONObject.fromObject(objectStr); // String转化为JSONObject
			object = jsonObject; // 返回的是JSONObject类型 取数据时候需要再次转换一下
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
}
