package zx.model;

import javafx.beans.property.SimpleStringProperty;
import zx.constant.Constant;

/**
 * 功能描述：redis数据的转化
 * 时间：2016/3/28 19:43
 *
 * @author ：zhaokuiqiang
 */
public abstract class RedisDataDecode {
    String key;
    String field;
    String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
