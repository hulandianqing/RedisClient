package zx.application;

import zx.model.TableData;
import zx.util.DesignUtil;

/**
 * 功能描述：查询的绑定form
 * 时间：2016/10/16 14:37
 *
 * @author ：zhaokuiqiang
 */
public class FindDataForm extends DataForm {

    public FindDataForm() {
    }

    @Override
    public Object execute(Object param) {
        TableData data = (TableData) param;
        try {
            data.getType().query(data);
        } catch(RuntimeException e) {
        }
        if(data.getValue() == null){
            data.setValue("无效的key");
        }
        DesignUtil.showFindData(data);
        return true;
    }

}