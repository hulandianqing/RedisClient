package zx.constant;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：图标资源
 * 时间：2016/10/16 20:16
 *
 * @author ：zhaokuiqiang
 */
public class ImageConstant {

    //id对应的图片
    public static Map<String,Image> IMG = new HashMap<>();
    public final static Image FINDPNG = new Image(Constant.RESOURCE + "find.png");
    public final static Image CONSOLEPNG = new Image(Constant.RESOURCE + "console.png");
    public final static Image MONITORPNG = new Image(Constant.RESOURCE + "monitor.png");

    static {
        IMG.put("consoleTab",CONSOLEPNG);
        IMG.put("findTab",FINDPNG);
        IMG.put("monitorTab",MONITORPNG);
    }

}
