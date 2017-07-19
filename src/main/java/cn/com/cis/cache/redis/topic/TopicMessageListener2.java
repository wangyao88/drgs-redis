package cn.com.cis.cache.redis.topic;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public class TopicMessageListener2 implements MessageListener {  
  
    private RedisTemplate redisTemplate;  
      
    public void setRedisTemplate(RedisTemplate redisTemplate) {  
        this.redisTemplate = redisTemplate;  
    }  
  
    @Override  
    public void onMessage(Message message, byte[] pattern) {  
        byte[] body = message.getBody();//请使用valueSerializer  
        byte[] channel = message.getChannel();  
        //请参考配置文件，本例中key，value的序列化方式均为string。  
        //其中key必须为stringSerializer。和redisTemplate.convertAndSend对应  
        cn.com.cis.module.drgsgroup.entity.Message msg = (cn.com.cis.module.drgsgroup.entity.Message) redisTemplate.getValueSerializer().deserialize(body);  
        String topic = (String)redisTemplate.getStringSerializer().deserialize(channel);  
        System.out.println("222"+msg.toString());
    }  
}  