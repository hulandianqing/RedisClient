package zx.util;

import zx.design.Main;

import java.text.SimpleDateFormat;

/**
 * 功能描述：控制台输出
 * 时间：2016/10/16 21:30
 *
 * @author ：zhaokuiqiang
 */
public class ConsoleUtil {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void write(String text){
        StringBuilder console = new StringBuilder();
        console.append("时间:").append(sdf.format(System.currentTimeMillis())).append("\n");
        console.append(text);
        console.append("\n\n");
        Main.consoleTextArea.setText(Main.consoleTextArea.getText() + console.toString());
    }
}
