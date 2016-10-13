package zx.model;

/**
 * 功能描述：
 * 时间：2016/3/28 19:43
 *
 * @author ：zhaokuiqiang
 */
public class RedisDB {
    String id;
    Integer index;
    String text;

    public RedisDB(){

    }

    public RedisDB(String id) {
        this.id = id;
    }

    public RedisDB(String id, Integer index) {
        this.id = id;
        this.index = index;
    }

    public RedisDB(String id, Integer index, String text) {
        this.id = id;
        this.text = text;
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
