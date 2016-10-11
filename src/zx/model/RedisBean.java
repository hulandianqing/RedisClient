package zx.model;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

/**
 * 功能描述：
 * 时间：2016/3/27 14:51
 *
 * @author ：zhaokuiqiang
 */
public class RedisBean{
    private String id;
    public String ip;
    public int port;
    public String name;
    public String password;

    public RedisBean(){}

    public RedisBean(String ip, int port, String name, String password) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

}
