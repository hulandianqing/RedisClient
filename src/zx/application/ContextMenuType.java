package zx.application;

import javafx.scene.control.ContextMenu;
import zx.util.DesignUtil;

/**
 * 功能描述：支持右键菜单类型
 * 时间：2016/10/15 19:30
 *
 * @author ：zhaokuiqiang
 */
public enum ContextMenuType {
    SERVER{
        @Override
        public ContextMenu getContextMenu() {
            return DesignUtil.getContextMenuRedisServer();
        }
    },DB {
        @Override
        public ContextMenu getContextMenu() {
            return DesignUtil.getContextMenuRedisDB();
        }
    };

    public abstract ContextMenu getContextMenu();
}
