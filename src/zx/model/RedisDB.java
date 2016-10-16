package zx.model;

import zx.application.ContextMenuType;
import zx.model.base.Base;
/**
 * 功能描述：db的pojo
 * 时间：2016/3/28 19:43
 *
 * @author ：zhaokuiqiang
 */
public class RedisDB extends Base{
    String id;
    Integer index;
    String text;

    public RedisDB(){
        this(null,null,null);
    }

    public RedisDB(String id) {
        this(id,null,null);
    }

    public RedisDB(String id, Integer index) {
        this(id,index,null);
    }

    public RedisDB(String id, Integer index, String text) {
        setMenuType(ContextMenuType.DB);
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
