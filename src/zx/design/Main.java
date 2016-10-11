package zx.design;

import com.datalook.gain.model.RedisProto;
import com.datalook.gain.util.ValidateUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import zx.codec.Codec;
import zx.codec.ProtobufDecode;
import zx.constant.Constant;
import zx.model.RedisBean;
import zx.model.RedisDB;
import zx.model.TableData;
import zx.redis.RedisContext;
import zx.util.DesignUtil;
import zx.util.JedisUtil;

import java.io.IOException;
import java.util.List;

/**
 * 功能描述：
 * 时间：2016/3/24 19:35
 *
 * @author ：zhaokuiqiang
 */
public class Main extends Application {

    final static public RedisContext CONTEXT = new RedisContext();
    /**
     * 统一的解码接口
     */
    public static Codec CODEC = new ProtobufDecode();
    final static Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    public static VBox root;
    public static Dialog dialog;
    //数据的table
    public static TableView tableView;
    //左侧的redis树
    public static TreeView<Object> treeView;
    //左侧的fiels树
    public static ListView<String> listView;
    public static TabPane tabPane;
    //redis添加编辑页面
    public static Parent redisServer;
    //数据页面
    public static Parent dataServer;
    /**
     * 当前选中的redisid
     */
    public static String redisId;
    /**
     * 当前选中的redis数据库块
     */
    public static RedisDB currentRedisDB;

    /**
     * 初始宽度
     */
    double showHashFieldWidth = 0;

    static final String [] TABLECOLUMN = new String[]{"type","key","value"};

    public static void main(String[] args) {
        //加载页面
        launch("nihao");
    }

    @Override
    public void start(Stage stage) throws Exception {
        root = FXMLLoader.load(getClass().getResource("main.fxml"));
        initDialog();
        initContent();
        treeView = (TreeView) root.lookup("#serverTree");
        tableView = (TableView) root.lookup("#dataTable");
        /*********暂时隐藏右上tab功能********/
        tableView.setVisible(false);
        HBox pane = (HBox) root.lookup("#tabhidden");
        pane.setMaxHeight(0);
        pane.setPrefHeight(0);
        pane.setMinHeight(0);
        pane.setVisible(false);
        /********end*********/
        tabPane = (TabPane) root.lookup("#showHashTabPane");
        listView = (ListView<String>) root.lookup("#fieldListView");
        //server页面
        redisServer = FXMLLoader.load(Main.class.getResource("server.fxml"));
        dataServer = FXMLLoader.load(Main.class.getResource("data.fxml"));
        //初始化组件
        initRedisiTree(treeView);
        initDataTable();
        initListView();
        initTreeRightClickMenus();
        initTableRightClickMenus();
        initDataServer();
        //初始化窗体
        Scene scene = new Scene(root, 0, 0);
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        stage.setTitle("Redis客户端");
        stage.setMaximized(true);
        stage.getIcons().add(Constant.ICONIMG);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * 中间部位
     */
    public void initContent(){
        HBox hBox = (HBox) root.lookup("#content");
        hBox.setPrefWidth(primaryScreenBounds.getMaxX());
        hBox.setPrefHeight(primaryScreenBounds.getMaxY());
    }

    /**
     * 数据table
     */
    public void initDataTable(){
//        TableView tableView = (TableView) root.lookup("#dataTable");
        tableView.setRowFactory(new Callback<TableView,TableRow>() {
            @Override
            public TableRow call(TableView param) {
                TableRow<TableData> tableRow = new TableRow<>();
                tableRow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        TableData tableData = tableRow.getItem();
                        if(tableData == null){
                            return;
                        }
                        ListView<String> listView = (ListView<String>) root.lookup("#fieldListView");
                        listView.getItems().clear();
                        //设置key text
                        TextField textField = (TextField) root.lookup("#showKeys");
                        textField.setText(tableData.getKey());
                        //设置field text
                        textField = (TextField) root.lookup("#showFields");
                        if(tableData.getType().equals(Constant.REDIS_HASH)){
                            textField.setText(tableData.getValue());
                        }else{
                            textField.setText("");
                        }
                        //如果是hash显示field、value
                        if(Constant.REDIS_HASH.equals(tableData.getType())){
                            //绑定list数据
                            listView.getItems().addAll(JedisUtil.CURRENTKEYFIELDS.get(tableData.getKey()));
                            //显示hash
                            showHashUI(true);
                        }else{
                            //隐藏hash组件
                            showHashUI(false);
                            DesignUtil.createTab(tableData.getKey(),tableData.getValue());
                        }

                    }
                });
                return tableRow;
            }
        });
        ObservableList<TableColumn> columns = tableView.getColumns();
        for(int i = 0; i < columns.size(); i++) {
            TableColumn column = (TableColumn) columns.get(i);
//            column.setText(TABLECOLUMN[i]);
            column.setCellValueFactory(new PropertyValueFactory<TableData,String>(TABLECOLUMN[i]));
        }
    }

    /**
     * 初始化树
     */
    public void initRedisiTree(TreeView node){
        if(node == null){
            throw new NullPointerException();
        }
        DesignUtil.refreshTree();
        //设置cell
        node.setCellFactory(new Callback<TreeView,TreeCell>() {
            @Override
            public TreeCell call(TreeView param) {
                TreeCellImpl treeCell = new TreeCellImpl();
                treeCell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        TreeCellImpl cell = (TreeCellImpl) event.getSource();
                        Object data = cell.getItem();
                        if(data == null){
                            return;
                        }
                        treeView.getContextMenu().setUserData(null);
                        RedisBean tempBean = null;
                        RedisDB tempDB = null;
                        if(data instanceof  RedisBean){
                            tempBean = (RedisBean) data;
                            treeView.getContextMenu().setUserData(tempBean);
                        }else if(data instanceof RedisDB){
                            tempDB = (RedisDB) data;
                        }
                        //双击事件
                        if(event.getClickCount() > 1){
                            if(tempBean != null){
                                //root子列表点击事件、初始化db列表
                                if(tempBean != null && !ValidateUtils.isEmpty(tempBean.getId())){
                                    boolean isExpanded = cell.getTreeItem().isExpanded();
                                    treeCell.getTreeItem().getChildren().clear();
                                    treeCell.getTreeItem().getChildren().addAll(DesignUtil.createDBTreeItem(tempBean));
                                    treeCell.getTreeItem().setExpanded(isExpanded);
                                    redisId = tempBean.getId();
                                }
                            }else if(tempDB != null){
                                //db列表双击事件、初始化table数据
                                currentRedisDB = tempDB;
                                //暂时屏蔽右上角tab功能，待优化
//                                DesignUtil.refreshTable();
                            }
                        }
                    }
                });
                return treeCell;
            }
        });
    }

    public void initListView(){
        listView.setCellFactory(new Callback<ListView<String>,ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCellImpl listCell = new ListCellImpl();
                listCell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        String field = listCell.getItem();
                        TextField textField = (TextField) root.lookup("#showFields");
                        textField.setText(field);
                        TableData tableData = (TableData) tableView.getSelectionModel().getSelectedItem();
                        String value = JedisUtil.getHashValue(redisId,tableData.getKey(),field);
                        if(!ValidateUtils.isEmpty(value)){
                            DesignUtil.createTab(tableData.getKey() + "-" + field,value);
                        }else{
                            dialog.show("无效的field，请刷新列表。");
                        }
                    }
                });
                /*listCell.focusedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        System.out.println(oldValue);
                        System.out.println(newValue);
                    }
                });*/
                return listCell;
            }
        });
        /*listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });*/
    }

    /**
     * 初始化右键菜单
     */
    public void initTreeRightClickMenus(){
        MenuItem addTreeMenu = new MenuItem("添加");
        addTreeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DesignUtil.addRedisServer();
            }
        });
        MenuItem editTreeMenu = new MenuItem("修改");
        editTreeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Object data = ((MenuItem)event.getSource()).getParentPopup().getUserData();
                if(data == null){
                    dialog.show("请选择要修改的redis");
                    return;
                }
                DesignUtil.editRedisServer((RedisBean) data);
            }
        });
        MenuItem deleteTreeMenu = new MenuItem("删除");
        deleteTreeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("gaojibamao");
                Object data = ((MenuItem)event.getSource()).getParentPopup().getUserData();
                if(data == null){
                    dialog.show("请选择要删除的redis");
                    return;
                }
                DesignUtil.deleteRedisServer((RedisBean) data);
            }
        });
        MenuItem refreshTreeMenu = new MenuItem("刷新");
        refreshTreeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DesignUtil.refreshTree();
            }
        });
        ContextMenu treeMenu = new ContextMenu();
        treeMenu.getItems().addAll(addTreeMenu,editTreeMenu,deleteTreeMenu,refreshTreeMenu);
        treeView.setContextMenu(treeMenu);
    }
    /**
     * 初始化table右键菜单
     */
    public void initTableRightClickMenus(){
        MenuItem addTreeMenu = new MenuItem("添加");
        addTreeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DesignUtil.addData();
            }
        });
        MenuItem editTreeMenu = new MenuItem("编辑");
        editTreeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tableView.getSelectionModel().getSelectedItem();
            }
        });
        MenuItem deleteTreeMenu = new MenuItem("删除");
        deleteTreeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MenuItem menuItem = (MenuItem) event.getTarget();
            }
        });
        MenuItem refreshTreeMenu = new MenuItem("刷新");
        deleteTreeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MenuItem menuItem = (MenuItem) event.getTarget();
            }
        });
        ContextMenu treeMenu = new ContextMenu();
        treeMenu.getItems().addAll(addTreeMenu,editTreeMenu,deleteTreeMenu,refreshTreeMenu);
        tableView.setContextMenu(treeMenu);
    }

    //初始化数据页面
    public void initDataServer(){
        ChoiceBox choiceBox = (ChoiceBox) dataServer.lookup("#typeChoice");
        choiceBox.setItems(FXCollections.observableArrayList("string","hash"));
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                TextField textField = (TextField) dataServer.lookup("#fieldField");
                if(Constant.REDIS_HASH.equals(choiceBox.getItems().get((Integer) newValue))){
                    //hash
                    textField.setDisable(false);
                }else if(Constant.REDIS_STRING.equals(choiceBox.getItems().get((Integer) newValue))){
                    //string
                    textField.setDisable(true);
                }
            }
        });
        choiceBox.getSelectionModel().select(0);
    }

    public void initDialog() throws IOException {
        dialog = new Dialog();
    }

    class TreeCellImpl extends TreeCell<Object>{
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if(!empty){
                if(item instanceof RedisBean){
                    setText(((RedisBean)item).getName());
                }else if(item instanceof RedisDB){
                    setText(((RedisDB)item).getText());
                }else{
                    setText(String.valueOf(item));
                }
            }else{
                setText(null);
            }
        }
    }


    class TableCellImpl extends TableCell{
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if(empty){
            }else{
                setText(String.valueOf(item));
            }
        }
    }

    /**
     * 显示和隐藏hash组件
     * @param visible
     */
    public void showHashUI(boolean visible){
        VBox showHashField = (VBox) root.lookup("#showHashField");
        showHashField.setVisible(visible);
        root.lookup("#showFields").setDisable(!visible);
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

    class ListCellImpl extends ListCell<String>{
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if(item != null){
                setText(item);
            }else{
                setText(null);
            }
        }
    }

}
