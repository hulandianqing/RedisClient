package zx.constant;

import javafx.scene.image.Image;
import zx.design.Main;

import java.io.File;

/**
 * 功能描述：
 * 时间：2016/3/27 12:17
 *
 * @author ：zhaokuiqiang
 */
public class Constant {

    /**
     * 配置文件路径
     */
    public final static String PROPERTYPATH = System.getProperty("user.dir") + File.separator + "Redis.properties";

    public final static String NAME = "name";
    public final static String PASSWORD = "password";
    public final static String PORT = "port";
    public final static String IP = "ip";

    public final static String MAXID = "maxid";

    /**
     * 系统icon
     */
    public final static Image ICONIMG = new Image("file:"+System.getProperty("user.dir") +
            File.separator + "resource" + File.separator + "icon.png");

    /**
     * tree显示的db
     */
    public final static String DB = "db";

    //-------------redis返回常量-----------------
    public final static String REDIS_OK = "OK";
    //-------------redis类型常量-----------------
    public final static String REDIS_STRING = "string";
    public final static String REDIS_HASH = "hash";
    public final static String REDIS_LIST = "list";

}
