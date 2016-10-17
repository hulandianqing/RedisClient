package zx.model;

import javafx.beans.property.SimpleStringProperty;
import zx.constant.Constant;
import zx.redis.RedisType;

import java.util.List;

/**
 * 功能描述：redis数据
 * 时间：2016/3/28 19:43
 *
 * @author ：zhaokuiqiang
 */
public class TableData {
    public TableData() {
    }

    public TableData(String key) {
        this(key,null);
    }

    public TableData(String key, String field) {
        this(null,key,field,null);
    }

    public TableData(String key, String field, String value) {
        this(null,key,field,value);
    }

    public TableData(RedisType type, String key, String field, String value) {
        this.type = type;
        this.setKey(key);
        this.setField(field);
        this.setValue(value);
    }

    RedisType type = null;
    SimpleStringProperty key = new SimpleStringProperty();
    SimpleStringProperty field = new SimpleStringProperty();
    SimpleStringProperty value = new SimpleStringProperty();
    //原始数据
    String source;
    //存放field列表
    Object fields = null;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public RedisType getType() {
        return type;
    }

    public void setType(RedisType type) {
        this.type = type;
    }

    public String getKey() {
        return key.get();
    }

    public SimpleStringProperty keyProperty() {
        return key;
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getField() {
        return field.get();
    }

    public void setField(String field) {
        this.field.set(field);
    }

    public Object getFields() {
        return fields;
    }

    public void setFields(Object fields){
        this.fields = fields;
    }

    public String valueString(){
        if(Constant.REDIS_STRING.equals(type)){
            return String.format("{value:%s}",value.get());
        }
        return String.format("{value:%s}",value.get());
    }

    @Override
    public String toString() {
        if(Constant.REDIS_STRING.equals(type)){
            return "{" +
                    " key:'" + key.get() + '\'' +
                ",value:'" + value.get() + '\'' +
                '}';
        }else if(Constant.REDIS_HASH.equals(type)){
            if(fields != null){
                StringBuilder sb = new StringBuilder();
                List<TableData> tempList = (List<TableData>)fields;
                for(int i = 0; i < tempList.size(); i++) {
                    sb.append(tempList.get(i).toString());
                }
                return sb.toString();
            }else{
                return "{" +
                "key:'" + key.get() + '\'' +
                        ",field:'" + field.get() +"'"+
                ",value:'" + value.get() + '\'' +
                '}';
            }
        }
        return "{" +
                "key:'" + key.get() + '\'' +
                ",value:'" + value.get() + '\'' +
                '}';

    }
}
