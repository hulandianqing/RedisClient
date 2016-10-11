package zx.controller;

import com.datalook.gain.util.ValidateUtils;
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
        String value = null;
        try {
            value = JedisUtil.getValue(Main.redisId,key,field);
        } catch(Exception e) {
            e.printStackTrace();
            Main.dialog.show("redis连接异常");
        }
        if(ValidateUtils.isEmpty(field)){
            DesignUtil.refreshShowData(key,null,value);
        }else{
            DesignUtil.refreshShowData(key, FXCollections.observableArrayList(field),value);
        }

    }

}
