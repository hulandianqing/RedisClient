package zx.model.base;

import zx.application.ContextMenuType;

/**
 * 功能描述：组件的基类
 * 时间：2016/10/15 21:22
 *
 * @author ：zhaokuiqiang
 */
public class Base {
    ContextMenuType menuType;

    public ContextMenuType getMenuType() {
        return menuType;
    }

    public void setMenuType(ContextMenuType menuType) {
        this.menuType = menuType;
    }
}
