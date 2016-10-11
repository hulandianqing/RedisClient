package zx.redis;

import zx.constant.Constant;
import zx.model.RedisBean;
import zx.property.PropertyUtil;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 功能描述：
 * 时间：2016/3/27 11:23
 *
 * @author ：zhaokuiqiang
 */
public class RedisContext{

    Map<String,RedisBean> redisMap = new HashMap<String,RedisBean>();

    /**
     * properties存的redis最大id
     */
    public static int maxid = 0;
    /**
     * RedisBean的属性
     */
    private Field[] fields = null;

    {
        fields = RedisBean.class.getFields();
        maxid = PropertyUtil.readRedisMaxId();
        redisMap = PropertyUtil.readRedisBeanAll();
    }

    public RedisBean[] redisBeanCollection(){
        return redisMap.values().toArray(new RedisBean[redisMap.size()]);
    }

    public RedisBean getRedisBean(String id){
        return redisMap.get(id);
    }

    /**
     * 增加一个redis失败返回-1
     * @param bean
     * @return
     */
    public int addRedis(RedisBean bean){
        maxid++;
        for(int i = 0; i < fields.length; i++) {
            try {
                Object value = fields[i].get(bean);
                if(value == null){
                    continue;
                }
                if(value instanceof String){
                    PropertyUtil.write(fields[i].getName() + maxid, (String) value);
                }else if(value instanceof Integer){
                    PropertyUtil.write(fields[i].getName() + maxid, String.valueOf((Integer) value));
                }
            } catch(IllegalAccessException e) {
                return -1;
            }
        }
        bean.setId(String.valueOf(maxid));
        PropertyUtil.write(Constant.MAXID,bean.getId());
        redisMap.put(bean.getId(),bean);
        return maxid;
    }

    public boolean edisRedis(RedisBean bean){
        for(int i = 0; i < fields.length; i++) {
            try {
                Object value = fields[i].get(bean);
                if(value == null){
                    continue;
                }
                if(value instanceof String){
                    PropertyUtil.write(fields[i].getName() + bean.getId(), (String) value);
                }else if(value instanceof Integer){
                    PropertyUtil.write(fields[i].getName() + bean.getId(), String.valueOf((Integer) value));
                }
            } catch(IllegalAccessException e) {
                return false;
            }
        }
        redisMap.put(bean.getId(),bean);
        return true;
    }

    /**
     * 移除redis
     * @param id
     */
    public void removeRedis(String id){
        if(redisMap.remove(id) != null){
            for(int i = 0; i < fields.length; i++) {
                PropertyUtil.delete(fields[i].getName() + id);
            }
        }
    }

}
