package zx.redis;

/**
 * 功能描述：redis类型
 * 时间：2016/10/11 22:30
 *
 * @author ：zhaokuiqiang
 */
public enum RedisType {
    STRING("string"),HASH("hash"),LIST("list"),SET("set"),ZSET("zset");

    String type = null;
    private RedisType(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
