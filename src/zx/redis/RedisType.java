package zx.redis;

import com.datalook.gain.util.ValidateUtils;
import javafx.collections.FXCollections;
import zx.design.Main;
import zx.model.TableData;
import zx.util.JedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            return JedisUtil.getValueSet(Main.redisDB.getId(),data);
        }
    },HASH("hash"){
        @Override
        public TableData execute0(TableData data) {
            if(ValidateUtils.isEmpty(data.getField())){
                List<TableData> keyList = new ArrayList<>();
                keyList.add(data);
                List<List<TableData>> tempList = JedisUtil.getAllHash(Main.redisDB.getId(),keyList);
                data.setFields(tempList.get(0));
                return data;
            }else{
                return JedisUtil.getValueHash(Main.redisDB.getId(),data);
            }
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
    RedisType(String type){
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
