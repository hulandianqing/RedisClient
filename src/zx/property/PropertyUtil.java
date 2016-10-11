package zx.property;

import com.datalook.gain.util.ValidateUtils;
import redis.clients.jedis.HostAndPort;
import zx.constant.Constant;
import zx.model.RedisBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：property工具类
 * 时间：2016/3/27 12:16
 *
 * @author ：zhaokuiqiang
 */
public class PropertyUtil {

    /**
     * 全部配置
     * @return
     */
    public static Map<String,RedisBean> readRedisBeanAll(){
        Map<String,RedisBean> maps = new HashMap<String,RedisBean>();
        for(int i = 1; i <= readRedisMaxId(); i++) {
            RedisBean redisBean = readRedisBean(i);
            if(redisBean != null){
                redisBean.setId(String.valueOf(i));
                maps.put(String.valueOf(i),redisBean);
            }
        }
        return maps;
    }

    /**
     * 读取redis配置
     * @param id
     * @return
     */
    public static RedisBean readRedisBean(int id){
        String ip = read(Constant.IP+id);
        String port = read(Constant.PORT+id);
        String password = read(Constant.PASSWORD+id);
        String name = read(Constant.NAME+id);
        if(!ValidateUtils.isEmpty(ip) && !ValidateUtils.isEmpty(port)
                && !ValidateUtils.isEmpty(name)){
            return new RedisBean(ip,Integer.parseInt(port),name,password);
        }
        return null;
    }

    /**
     * 读取redis配置
     * @param id
     * @return
     */
    public static HostAndPort readRedisHostAndPort(int id){
        String ip = read(Constant.IP+id);
        String port = read(Constant.PORT+id);
        if(!ValidateUtils.isEmpty(ip) && !ValidateUtils.isEmpty(port)){
            return new HostAndPort(ip,Integer.parseInt(port));
        }
        return null;
    }

    /**
     * 读取配置的redis最大id
     * @return
     */
    public static int readRedisMaxId(){
        String maxid = read(Constant.MAXID);
        if(!ValidateUtils.isEmpty(maxid)){
            return Integer.parseInt(maxid);
        }
        return 0;
    }

    public static String read(String key){
        try {
            return PropertyFile.read(Constant.PROPERTYPATH,key);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean write(String key,String value){
        try {
            PropertyFile.write(Constant.PROPERTYPATH,key,value);
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(String key){
        try {
            PropertyFile.delete(Constant.PROPERTYPATH, key);
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
