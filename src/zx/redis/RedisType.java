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
        public TableData query0(TableData data){
            data.setValue("无效的key");
            return data;
        }

        @Override
        public boolean add0(TableData data) {
            return false;
        }
    },STRING("string"){
        @Override
        public TableData query0(TableData data) {
            return JedisUtil.getValueSet(Main.redisDB.getId(),data);
        }

        @Override
        public boolean add0(TableData data) {
            if(ValidateUtils.isEmpty(data) || ValidateUtils.isEmpty(data.getKey())){
                return false;
            }
            return JedisUtil.saveData(data.getKey(),data.getValue());
        }
    },HASH("hash"){
        @Override
        public TableData query0(TableData data) {
            if(ValidateUtils.isEmpty(data.getField())){
                List<TableData> tempList = JedisUtil.getAllHash(Main.redisDB.getId(),data);
                data.setFields(tempList);
                return data;
            }else{
                return JedisUtil.getValueHash(Main.redisDB.getId(),data);
            }
        }

        @Override
        public boolean add0(TableData data) {
            if(ValidateUtils.isEmpty(data) || ValidateUtils.isEmpty(data.getKey())
                    || ValidateUtils.isEmpty(data.getField()) || ValidateUtils.isEmpty(data.getValue())){
                return false;
            }
            return JedisUtil.saveData(data.getKey(),data.getField(),data.getValue());
        }
    },LIST("list"){
        @Override
        public TableData query0(TableData data) {
            data.setValue("暂不支持"+this.name()+"类型");
            return data;
        }

        @Override
        public boolean add0(TableData data) {
            return false;
        }
    },SET("set"){
        @Override
        public TableData query0(TableData data) {
            data.setValue("暂不支持"+this.name()+"类型");
            return data;
        }

        @Override
        public boolean add0(TableData data) {
            return false;
        }
    },ZSET("zset"){
        @Override
        public TableData query0(TableData data) {
            data.setValue("暂不支持"+this.name()+"类型");
            return data;
        }

        @Override
        public boolean add0(TableData data) {
            return false;
        }
    };

    String type = null;
    RedisType(String type){
        this.type = type;
    }

    /**
     * 查询数据
     * @param data
     * @return
     */
    public TableData query(TableData data){
        data.setType(this);
        return query0(data);
    }

    abstract TableData query0(TableData data);

    /**
     * 添加数据
     * @return
     */
    public boolean add(TableData data){
        return add0(data);
    }

    abstract boolean add0(TableData data);

    @Override
    public String toString() {
        return type;
    }
}
