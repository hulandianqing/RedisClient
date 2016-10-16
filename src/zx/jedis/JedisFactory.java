package zx.jedis;

import com.datalook.gain.util.ValidateUtils;
import redis.clients.jedis.Jedis;
import zx.design.Main;
import zx.model.RedisBean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 功能描述：
 * 时间：2016/3/28 18:03
 *
 * @author ：zhaokuiqiang
 */
public class JedisFactory {

    static Map<String,Jedis> jedisMap = new HashMap<String,Jedis>();

    public static boolean validate(Jedis jedis){
        try {
            if(jedis != null && jedis.ping().equals("PONG")){
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取jedis
     * @param id
     * @return
     */
    public static Jedis getJedis(String id){
        Jedis jedis = jedisMap.get(id);
        if(!validate(jedis)){
            RedisBean redisBean = Main.CONTEXT.getRedisBean(id);
            if(redisBean == null || ValidateUtils.isEmpty(redisBean.getIp()) || ValidateUtils.isEmpty(redisBean.getPort())){
                return null;
            }else{
                jedis = new Jedis(redisBean.getIp(),redisBean.getPort());
                jedisMap.put(redisBean.getId(),jedis);
            }
        }
        return jedis;
    }

    /**
     * 释放所有的redis连接
     */
    public static void destroyAllRedis(){
        Iterator<String> keys = jedisMap.keySet().iterator();
        while(keys.hasNext()){
            destroyRedis(keys.next());
        }
    }

    /**
     * 释放指定的redis连接
     * @param id
     */
    public static void destroyRedis(String id){
        Jedis jedis = jedisMap.get(id);
        if(jedis != null) {
            jedis.close();
        }
        jedisMap.put(id,null);
    }
}
