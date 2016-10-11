package zx.controller;

import com.datalook.gain.util.ValidateUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import zx.constant.Constant;
import zx.design.Main;
import zx.model.RedisBean;
import zx.util.DesignUtil;
import zx.util.JedisUtil;

/**
 * 功能描述：主类控制器
 * 时间：2016/3/25 15:06
 *
 * @author ：zhaokuiqiang
 */
public class ServerController {

    @FXML Button canelBtn;

    @FXML Button saveBtn;

    @FXML
    TextField nameField;
    @FXML
    TextField ipField;
    @FXML
    TextField portField;
    @FXML
    PasswordField passWordField;
//    @FXML
//    Label passWordTooltip;
    @FXML
    Label nameTooltip;
    @FXML
    Label ipTooltip;
    @FXML
    Label portTooltip;

    @FXML
    TextField keyField;
    @FXML
    TextField fieldField;
    @FXML
    TextField valueField;
    @FXML
    ChoiceBox typeChoice;
    public ServerController(){
    }

    @FXML
    public void saveBtnReleased(){
        String name = nameField.getText();
        String ip = ipField.getText();
        String port = portField.getText();
        String password = passWordField.getText();

        boolean flag = true;
        if(ValidateUtils.isEmpty(name)){
            nameTooltip.setTextFill(Color.RED);
            flag = false;
        }else{
            nameTooltip.setTextFill(Color.BLACK);
        }
        if(ValidateUtils.isEmpty(ip)){
            ipTooltip.setTextFill(Color.RED);
            flag = false;
        }else{
            ipTooltip.setTextFill(Color.BLACK);
        }
        if(ValidateUtils.isEmpty(port)){
            portTooltip.setTextFill(Color.RED);
            flag = false;
        }else{
            portTooltip.setTextFill(Color.BLACK);
        }
        /*if(ValidateUtils.isEmpty(password)){
//            passWordTooltip.setTextFill(Color.RED);
            flag = false;
        }*/
        try {
            if(flag){
                Object data = saveBtn.getScene().getUserData();
                RedisBean redisBean = null;
                if(data == null){
                    redisBean = new RedisBean(ip,Integer.parseInt(port),name,password);
                    Main.CONTEXT.addRedis(redisBean);
                }else{
                    redisBean = (RedisBean) data;
                    redisBean.setIp(ipField.getText());
                    redisBean.setPort(Integer.parseInt(portField.getText()));
                    redisBean.setPassword(passWordField.getText());
                    redisBean.setName(nameField.getText());
                    Main.CONTEXT.edisRedis(redisBean);
                }
                canelBtn.getScene().getWindow().hide();
                DesignUtil.refreshTree();
            }
        }catch(NumberFormatException e){
            portTooltip.setTextFill(Color.RED);
            Main.dialog.show("端口号只能输入数字");
        }
    }

    @FXML
    public void canelBtnReleased(){
        canelBtn.getScene().getWindow().hide();
    }

    @FXML
    public void saveDataReleased(){
        String key,field,value;
        String type = (String) typeChoice.getSelectionModel().getSelectedItem();
        key = keyField.getText();
        value = valueField.getText();
        boolean flag = false;
        if(Constant.REDIS_HASH.equals(type)){
            field = fieldField.getText();
            flag = JedisUtil.saveData(key,field,value);
        }else if(Constant.REDIS_STRING.equals(type)){
            flag = JedisUtil.saveData(key,value);
        }
        if(!flag){
            Main.dialog.show("保存失败");
        }else{
            keyField.getScene().getWindow().hide();
        }
    }
}
