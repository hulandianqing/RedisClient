package zx.controller;

import com.datalook.gain.util.ValidateUtils;
import com.sun.xml.internal.bind.v2.model.core.ID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import zx.constant.Constant;
import zx.design.Main;
import zx.model.TableData;
import zx.util.DesignUtil;
import zx.util.JedisUtil;

import java.io.IOException;

/**
 * 功能描述：主类控制器
 * 时间：2016/3/25 15:06
 *
 * @author ：zhaokuiqiang
 */
public class MainController {

    @FXML
    TreeView<String> serverTree;

    @FXML
    TableView dataTable;

    public MainController(){

//        executor = new CommandExecutor(JedisContext.POOLSERVER.getJedis());
    }

    @FXML
    public void redisAddAction(){
        DesignUtil.addRedisServer();
    }

    @FXML
    public void searchKeyAndField(){
        if(Main.redisId == null){
            Main.dialog.show("请选择redis连接");
            return;
        }
        String key;
        String field;
        TextField textField = (TextField) Main.root.lookup("#showKeys");
        key = textField.getText();
        textField = (TextField) Main.root.lookup("#showFields");
        field = textField.getText();
        try {
            TableData tableData = new TableData();
            tableData.setKey(key);
            tableData.setField(field);
            tableData = JedisUtil.getKeyType(Main.redisId,key).execute(tableData);
            DesignUtil.refreshShowData(tableData);
        } catch(Exception e) {
            e.printStackTrace();
            if(e.getMessage() != null){
                Main.dialog.show(e.getMessage());
            }else{
                Main.dialog.show("redis连接异常");
            }
        }

    }

}
