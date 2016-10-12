package zx.redis;

import zx.design.Main;
import zx.model.TableData;
import zx.util.JedisUtil;

/**
 * 功能描述：redis类型
 * 时间：2016/10/11 22:30
 *
 * @author ：zhaokuiqiang
 */
public enum RedisType {
    NONE("none"){
        @Override
        public TableData execute0(TableData data){
            data.setValue("无效的key");
            return data;
        }
    },STRING("string"){
        @Override
        public TableData execute0(TableData data) {
            return JedisUtil.getValueSet(Main.redisId,data);
        }
    },HASH("hash"){
        @Override
        public TableData execute0(TableData data) {
            return JedisUtil.getValueHash(Main.redisId,data);
        }
    },LIST("list"){
        @Override
        public TableData execute0(TableData data) {
            data.setValue("暂不支持"+this.name()+"类型");
            return data;
        }
    },SET("set"){
        @Override
        public TableData execute0(TableData data) {
            data.setValue("暂不支持"+this.name()+"类型");
            return data;
        }
    },ZSET("zset"){
        @Override
        public TableData execute0(TableData data) {
            data.setValue("暂不支持"+this.name()+"类型");
            return data;
        }
    };

    String type = null;
    private RedisType(String type){
        this.type = type;
    }

    public TableData execute(TableData data){
        data.setType(this);
        return execute0(data);
    }

    public abstract TableData execute0(TableData data);

    @Override
    public String toString() {
        return type;
    }
}
