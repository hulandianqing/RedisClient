package zx.application;

import javafx.util.Callback;
import zx.model.TableData;
import zx.redis.RedisType;

/**
 * 功能描述：数据的form配置
 * 时间：2016/10/16 14:29
 *
 * @author ：zhaokuiqiang
 */
public abstract class DataForm<P,R> implements Callback<P,R>{

    @Override
    public R call(P param) {
        return null;
    }

    public abstract Object execute(Object param);
}
