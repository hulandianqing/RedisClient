package zx.model;

import com.datalook.gain.model.RedisProto;
import javafx.beans.property.SimpleStringProperty;
import zx.constant.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：redis数据
 * 时间：2016/3/28 19:43
 *
 * @author ：zhaokuiqiang
 */
public class TableData<T> {
    SimpleStringProperty type = new SimpleStringProperty();
    SimpleStringProperty key = new SimpleStringProperty();
    SimpleStringProperty value = new SimpleStringProperty();
    //hash存储field列
    List<String> fieldList = new ArrayList<>();
    //hash存储当前field和value
    String hashField;
    String hashValue;
    //原始数据
    T source;

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public String getHashField() {
        return hashField;
    }

    public void setHashField(String hashField) {
        this.hashField = hashField;
    }

    public T getSource() {
        return source;
    }

    public void setSource(T source) {
        this.source = source;
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
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

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public List<String> getField() {
        return fieldList;
    }

    public void addField(String field) {
        fieldList.add(field);
    }

    public void setValue(String value) {
        this.value.set(value);
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
        }
        return "{" +
                "key:'" + key.get() + '\'' +
                ",value:'" + value.get() + '\'' +
                '}';

    }
}
