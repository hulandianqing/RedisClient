package zx.model;

import javafx.scene.control.Tab;
import zx.model.base.Base;

/**
 * 功能描述：下面工具栏
 * 用来控制bottom显示状态
 * 时间：2016/10/16 0:23
 *
 * @author ：zhaokuiqiang
 */
public class BottomTab extends Base{
    public BottomTab() {
    }

    public BottomTab(Tab tab) {
        this.tab = tab;
    }

    Tab tab;
    boolean isTarget = false;

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean target) {
        isTarget = target;
    }
}
