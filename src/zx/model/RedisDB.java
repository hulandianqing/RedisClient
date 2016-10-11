package zx.model;

/**
 * 功能描述：
 * 时间：2016/3/28 19:43
 *
 * @author ：zhaokuiqiang
 */
public class RedisDB {
    String id;
    int index;
    String text;

    public RedisDB(String id, int index, String text) {
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
