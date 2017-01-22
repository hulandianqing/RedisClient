package zx.util;

import com.datalook.gain.util.ValidateUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import zx.application.FindDataForm;
import zx.constant.Constant;
import zx.design.Main;
import zx.model.BottomTab;
import zx.model.RedisBean;
import zx.model.RedisDB;
import zx.model.TableData;
import zx.redis.RedisType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 功能描述：
 * 时间：2016/3/28 18:51
 *
 * @author ：zhaokuiqiang
 */
public class DesignUtil {

    /**
     * 初始宽度
     */
    static double showHashFieldWidth = 0;
    /**
     * 刷新tree
     */
    public static void refreshTree(){
        TreeItem<Object> rootItem = Main.treeView.getRoot();
        if(rootItem == null){
            rootItem = new TreeItem<Object>(new RedisBean("",0,"Redis列表",""));
            rootItem.getChildren().addAll();
            Main.treeView.setRoot(rootItem);
        }
        rootItem.getChildren().clear();
        RedisBean[] redisBeans = Main.CONTEXT.redisBeanCollection();
        TreeItem<Object> serverItem = null;
        for(int i = 0; i < redisBeans.length; i++) {
            RedisBean bean = redisBeans[i];
            serverItem = new TreeItem<Object>(bean);
            rootItem.getChildren().add(serverItem);
        }
        rootItem.setExpanded(true);
        //清除reis db的选中
        Main.redisDB = new RedisDB();
        //释放redis连接
        JedisUtil.destroyAllRedis();
        clearTabPane();
//        clearTableView();
        clearListView();
        TextField textField = (TextField) Main.root.lookup("#showKeys");
        textField.setText("");
        textField = (TextField) Main.root.lookup("#showFields");
        textField.setText("");

    }

    /**
     * 刷新redis数据table
     */
    /*public static void refreshTable(){
        if(Main.redisDB.getIndex() != null){
            clearTableView();
            ObservableList<TableData> list = DesignUtil.showSelectDBData(Main.redisDB.getId(),Main.redisDB.getIndex());
            Main.tableView.setItems(list);
        }else{
            Main.dialog.show("未选中redis");
        }
    }*/

    /**
     * 刷新下面的数据
     */
    public static void refreshShowData(List<TableData> dataList){
        if(dataList == null){
            return;
        }
        clearListView();
        clearTabPane();
        for(int i = 0; i < dataList.size(); i++) {
            addTableData(dataList.get(i));
        }
    }

    /**
     * 刷新下面的数据
     */
    public static void refreshShowData(TableData tableData){
        clearListView();
        clearTabPane();
        addTableData(tableData);
    }

    /**
     * 添加value数据
     * @param tableData
     */
    public static void addTableData(TableData tableData){
        String text = tableData.getKey();
        if(tableData.getType() == RedisType.HASH){
            if(tableData.getFields() != null){
                Main.listView.getItems().addAll((Collection<? extends TableData>) tableData.getFields());
            }else{
                Main.listView.getItems().addAll(tableData);
            }
            Main.listView.getSelectionModel().select(0);
            text+="-"+tableData.getField();
        }
        text += " [" + tableData.getType() + "]";
        if(tableData.getFields() == null && tableData.getValue() != null){
            createTab(text,tableData.getValue());
        }
    }

    /**
     * 清除table数据
     */
    public static void clearFindTable(){
        //清除findtable
        Main.findTable.getItems().clear();
    }

    /**
     * 清除左侧的fields
     */
    public static void clearListView(){
        //清除左侧的fiels树
        Main.listView.getItems().clear();
    }

    /**
     * 清除value框
     */
    public static void clearTabPane(){
        ObservableList<Tab> tabs = Main.tabPane.getTabs();
        Tab tab = tabs.get(0);
        tabs.clear();
        tabs.add(tab);
    }

    /**
     * 创建db树
     * @param redisBean
     * @return
     */
    public static List<TreeItem<Object>> createDBTreeItem(RedisBean redisBean){
        try{
            if(redisBean == null){
                return null;
            }
            String databases = JedisUtil.getDatabases(redisBean.getId());
            int db = Integer.parseInt(databases);
            List<TreeItem<Object>> treeItems = new ArrayList<>();
            for(int i = 0; i < db; i++) {
                treeItems.add(new TreeItem<>(new RedisDB(redisBean.getId(),i,Constant.DB + i)));
            }
            return treeItems;
        }catch(RuntimeException e){
            e.printStackTrace();
            Main.dialog.show(String.format("连接%s失败",redisBean.getName()));
            return null;
        }
    }

    /**
     * 创建一个tab
     * @return
     */
    public static Tab createTab(String text,String value){
        ObservableList<Tab> tabs = Main.tabPane.getTabs();
        Tab tab = null;
        boolean isExists = false;
        for(int i = 0; i < tabs.size(); i++) {
            tab = tabs.get(i);
            if(tab.getText() == null){
                continue;
            }
            if(tab.getText().equals(text)){
                isExists = true;
                break;
            }
        }
        if(tab == null || !isExists){
            tab = new Tab();
            tab.setText(text);
            Main.tabPane.getTabs().add(tab);
        }
        TextArea textArea = new TextArea();
        textArea.setText(value);
        textArea.setWrapText(true);
        tab.setContent(textArea);
        SingleSelectionModel<Tab> selectionMode = Main.tabPane.getSelectionModel();
        selectionMode.select(tab);
        Main.tabPane.setSelectionModel(selectionMode);
        return tab;
    }

    /**
     * 添加redis
     */
    public static void addRedisServer(){
        Stage stage = new Stage();
        stage.setUserData(new RedisBean());
        Scene scene;
        if(Main.redisServer.getScene() == null){
            scene = new Scene(Main.redisServer);
        }else{
             scene = Main.redisServer.getScene();
        }
        stage.setScene(scene);
        stage.setTitle("添加redis");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(Constant.ICONIMG);
        stage.show();
    }

    /**
     * 编辑redis
     */
    public static void editRedisServer(RedisBean redisBean){
        if(ValidateUtils.isEmpty(redisBean)){
            Main.dialog.show("请选择要修改的redis");
            return;
        }
        if(ValidateUtils.isEmpty(redisBean.getId()) && ValidateUtils.isEmpty(redisBean.getIp()) &&
                ValidateUtils.isEmpty(redisBean.getPort()) && ValidateUtils.isEmpty(redisBean.getName())){
            Main.dialog.show("初始化页面失败、配置文件损坏");
            return;
        }
        TextField textField = (TextField) Main.redisServer.lookup("#ipField");
        textField.setText(redisBean.getIp());
        textField = (TextField) Main.redisServer.lookup("#portField");
        textField.setText(String.valueOf(redisBean.getPort()));
        textField = (TextField) Main.redisServer.lookup("#nameField");
        textField.setText(redisBean.getName());
        textField = (TextField) Main.redisServer.lookup("#passWordField");
        textField.setText(redisBean.getPassword());
        Scene scene;
        if(Main.redisServer.getScene() == null){
            scene = new Scene(Main.redisServer);
        }else{
            scene = Main.redisServer.getScene();
        }
        showWindow(scene,redisBean,"修改redis",Modality.APPLICATION_MODAL);
    }

    /**
     * 删除redis
     * @param redisBean
     */
    public static void deleteRedisServer(RedisBean redisBean){
        if(redisBean == null || ValidateUtils.isEmpty(redisBean.getId())){
            Main.dialog.show("请选择要删除的redis");
        }
        Main.CONTEXT.removeRedis(redisBean.getId());
        refreshTree();
    }

    /**
     * 添加table data数据
     */
    public static void addData(){
        Scene scene = Main.dataServer.getScene();
        if(scene == null){
            scene = new Scene(Main.dataServer);
        }
        TextField textField = (TextField) Main.dataServer.lookup("#keyField");
        textField.setText("");
        textField = (TextField) Main.dataServer.lookup("#fieldField");
        textField.setText("");
        textField = (TextField) Main.dataServer.lookup("#valueField");
        textField.setText("");
        showWindow(scene,null,"添加数据",Modality.APPLICATION_MODAL);
    }

    /**
     * 修改table data数据
     */
    public static void editData(TableData tableData){
        if(ValidateUtils.isEmpty(tableData)){
            return;
        }
        Scene scene = Main.dataServer.getScene();
        if(scene == null){
            scene = new Scene(Main.dataServer);
        }
        TextField textField = (TextField) Main.dataServer.lookup("#keyField");
        textField.setText(tableData.getKey());
        textField = (TextField) Main.dataServer.lookup("#fieldField");
        textField.setText(tableData.getField());
        textField = (TextField) Main.dataServer.lookup("#valueField");
        textField.setText(tableData.getValue());
        showWindow(scene,null,"修改数据",Modality.APPLICATION_MODAL);
    }

    public static void showWindow(Scene scene,Object userData,String title,Modality modality){
        Stage stage = new Stage();
        stage.setScene(scene);
        scene.setUserData(userData);
        stage.setTitle(title);
        stage.initModality(modality);
        stage.getIcons().add(Constant.ICONIMG);
        stage.show();
    }

    /**
     * 显示和隐藏hash组件
     * @param visible
     */
    public static void showHashUI(boolean visible){
        VBox showHashField = (VBox) Main.root.lookup("#showHashField");
        showHashField.setVisible(visible);
        Main.root.lookup("#showFields").setDisable(!visible);
        if(!visible){
            if(showHashFieldWidth == 0){
                showHashFieldWidth = showHashField.getWidth();
            }
            showHashField.setPrefWidth(0);
            showHashField.setMaxWidth(0);
        }else{
            if(showHashFieldWidth != 0){
//                showHashField.setMinWidth(showHashFieldWidth);
                showHashField.setMaxWidth(showHashFieldWidth);
                showHashField.setPrefWidth(showHashFieldWidth);
            }
        }

    }

    /**
     * 创建menuitem
     * @param text
     * @param event
     * @return
     */
    public static MenuItem newMenuItem(String text,javafx.event.EventHandler<javafx.event.ActionEvent> event){
        MenuItem addTreeMenu = new MenuItem(text);
        addTreeMenu.setOnAction(event);
        return addTreeMenu;
    }

    /**
     * 生成tree的redisserver右键菜单
     * @return
     */
    public static ContextMenu getContextMenuRedisServer(){
        if(ValidateUtils.isEmpty(Main.ContextMenu_RedisServer)){
            Main.ContextMenu_RedisServer = new ContextMenu();
            MenuItem addTreeMenu = DesignUtil.newMenuItem("添加",event -> DesignUtil.addRedisServer());
            MenuItem editTreeMenu = DesignUtil.newMenuItem("编辑",event ->  {
                Object data = ((MenuItem)event.getSource()).getParentPopup().getUserData();
                if(data == null){
                    Main.dialog.show("请选择要编辑的redis");
                    return;
                }
                DesignUtil.editRedisServer((RedisBean) data);});
            MenuItem deleteTreeMenu = DesignUtil.newMenuItem("删除",event -> {
                Object data = ((MenuItem)event.getSource()).getParentPopup().getUserData();
                if(data == null){
                    Main.dialog.show("请选择要删除的redis");
                    return;
                }
                DesignUtil.deleteRedisServer((RedisBean) data);});
            MenuItem refreshTreeMenu = DesignUtil.newMenuItem("刷新",event -> DesignUtil.refreshTree());
            Main.ContextMenu_RedisServer.getItems().addAll(addTreeMenu,editTreeMenu,deleteTreeMenu,refreshTreeMenu);
        }
        return Main.ContextMenu_RedisServer;
    }

    /**
     * 生成tree的redisdb右键菜单
     * @return
     */
    public static ContextMenu getContextMenuRedisDB(){
        if(ValidateUtils.isEmpty(Main.ContextMenu_RedisDB)){
            Main.ContextMenu_RedisDB = new ContextMenu();
            MenuItem findTreeMenu = DesignUtil.newMenuItem("查找",event -> DesignUtil.showFindStage());
            Main.ContextMenu_RedisDB.getItems().addAll(findTreeMenu);
        }
        return Main.ContextMenu_RedisDB;
    }

    /**
     * 显示查询的数据
     */
    public static void showFindData(TableData tableData){
    	//如果value不为空则证明已经查询出结果
    	if(!ValidateUtils.isEmpty(tableData.getValue())){
    		String key = tableData.getKey();
    		if(!ValidateUtils.isEmpty(tableData.getField())){
    			key += "-" + tableData.getField();
			}
    		DesignUtil.createTab(key,tableData.getValue());
			return;
		}
        clearFindTable();
        ObservableList<TableData> datas = FXCollections.observableArrayList();
        if(tableData.getType() == RedisType.HASH){
            if(tableData.getFields() == null){
                datas.addAll(tableData);
            }else{
                ArrayList<TableData> list = (ArrayList<TableData>) tableData.getFields();
                datas.addAll(list);
            }
        }else{
            datas.add(tableData);
        }
        Main.findTable.setItems(datas);
        bottomPaneSelect(1);
    }

    public static void bottomPaneSelect(int index){
        Main.bottomTabPane.getSelectionModel().select(1);
        changeBottomTab(false);
    }

    /**
     * bottom tab显示隐藏
     * @param isChangeVisiable 是否需要变更显示状态
     */
    public static void changeBottomTab(boolean isChangeVisiable){
        if(Main.bottomTabPane.getSelectionModel().getSelectedIndex() == 0){
            return;
        }
        Tab tab = Main.bottomTabPane.getSelectionModel().getSelectedItem();
        BottomTab bottomTab = (BottomTab) Main.bottomTabPane.getUserData();
        if(bottomTab == null) bottomTab = new BottomTab();
        Tab oldTab = bottomTab.getTab();
        if(!bottomTab.isTarget()){
            if(tab == oldTab && isChangeVisiable){
                DesignUtil.changeBottomPaneVisiable(false);
                bottomTab.setTab(Main.bottomTabPane.getSelectionModel().getSelectedItem());
            }else{
                DesignUtil.changeBottomPaneVisiable(true);
                bottomTab.setTab(tab);
            }
        }else{
            bottomTab.setTarget(false);
        }
        Main.bottomTabPane.setUserData(bottomTab);
    }

    /**
     * 更改下面工具栏的显示状态
     * @param isShow
     */
    public static void changeBottomPaneVisiable(boolean isShow){
        if(isShow){
            Main.bottomTabPane.setPrefHeight(1050);
        }else{
            Main.bottomTabPane.getSelectionModel().select(0);
            Main.bottomTabPane.setPrefHeight(150);
        }
    }

    /**
     * 显示查询的pane
     * @return
     */
    public static void showFindStage(){
        ChoiceBox choiceBox = (ChoiceBox)Main.dataServer.lookup("#typeChoice");
        choiceBox.getSelectionModel().select(0);
        TextField valueField = (TextField) Main.dataServer.lookup("#valueField");
        valueField.setDisable(true);
        valueField.setText("");
        TextField fieldField = (TextField) Main.dataServer.lookup("#fieldField");
        fieldField.setDisable(true);
        fieldField.setText("");
        TextField keyField = (TextField) Main.dataServer.lookup("#keyField");
        keyField.setText("");
        keyField.requestFocus();
        Stage stage = DesignUtil.newStage(Main.dataServer,"查询",Modality.APPLICATION_MODAL);
        stage.getScene().setUserData(new FindDataForm());
        stage.show();
    }

    /**
     * 显示添加数据的pane
     * @return
     */
    public static void showAddDataStage(){
        TextField valueField = (TextField) Main.dataServer.lookup("#valueField");
        valueField.setDisable(false);
        TextField fieldField = (TextField) Main.dataServer.lookup("#fieldField");
        fieldField.setDisable(false);
        Stage stage = DesignUtil.newStage(Main.dataServer,"添加数据",Modality.APPLICATION_MODAL);
        stage.getScene().setUserData(new FindDataForm());
        stage.show();
    }

    /**
     * 创建窗体
     * @param parent
     * @param title
     * @param modality
     */
    public static Stage newStage(Parent parent,String title,Modality modality){
        Stage stage = new Stage();
        stage.setUserData(new RedisBean());
        Scene scene = parent.getScene();
        if(scene == null){
            scene = new Scene(parent);
        }
        stage.setScene(scene);
        stage.setTitle(title);
        stage.initModality(modality);
        stage.getIcons().add(Constant.ICONIMG);
        return stage;
    }
}
