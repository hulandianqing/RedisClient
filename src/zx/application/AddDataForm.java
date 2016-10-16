package zx.application;

import zx.model.TableData;

/**
 * 功能描述：添加数据的绑定form
 * 时间：2016/10/16 14:37
 *
 * @author ：zhaokuiqiang
 */
public class AddDataForm extends DataForm {

    @Override
    public Object execute(Object param) {
        TableData data = (TableData) param;
        return data.getType().add(data);
    }

}