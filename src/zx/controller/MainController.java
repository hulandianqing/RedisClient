package zx.controller;

import com.datalook.gain.util.ValidateUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import zx.constant.Constant;
import zx.design.Main;
import zx.model.TableData;
import zx.util.DesignUtil;
import zx.util.JedisUtil;

import java.util.ArrayList;
import java.util.List;

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
        if(Main.redisDB.getId() == null){
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
            if(!ValidateUtils.isEmpty(key)){
				TableData tableData = new TableData();
				tableData.setKey(key);
				tableData.setField(field);
				JedisUtil.getKeyType(Main.redisDB.getId(),tableData.getKey()).query(tableData);
				DesignUtil.showFindData(tableData);
			}else{
                Main.dialog.show("无效的key");
            }
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
