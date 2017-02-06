package zx.design;

import com.datalook.gain.util.ValidateUtils;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import zx.constant.Constant;
import zx.constant.ImageConstant;
import zx.model.BottomTab;
import zx.model.RedisBean;
import zx.model.RedisDB;
import zx.model.TableData;
import zx.redis.RedisContext;
import zx.redis.RedisType;
import zx.util.DesignUtil;
import zx.util.JedisUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 功能描述：
 * 时间：2016/3/24 19:35
 *
 * @author ：zhaokuiqiang
 */
public class Main extends Application {

    final static public RedisContext CONTEXT = new RedisContext();
    final static Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    public static StackPane mainView;
    public static Pane root;
    //上方右部内容
    public static Pane contentPane;
    public static Dialog dialog;
    //左侧的redis树
    public static TreeView<Object> treeView;
    //左侧的fiels树
    public static ListView<TableData> listView;
    public static TabPane tabPane;
    //redis添加编辑页面
    public static Parent redisServer;
    //数据页面
    public static VBox dataServer;
    //上次一选中的cell（获取cell不区分父子级关系，所以保留一份备份）
    public Cell backCell = null;
    /**
     * 当前选中的redis
     */
    public static RedisDB redisDB = new RedisDB();

    public static ContextMenu ContextMenu_RedisServer = DesignUtil.getContextMenuRedisServer();
    public static ContextMenu ContextMenu_RedisDB = DesignUtil.getContextMenuRedisDB();

    public static TabPane bottomTabPane;
    public static TableView findTable;
    static final String [] FINDTABLECOLUMN = new String[]{"type","key","field","value"};
    public static TextArea consoleTextArea;

    public static void main(String[] args) {
        //加载页面
        launch("nihao");
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainView = new StackPane();
		root = FXMLLoader.load(getClass().getResource("main.fxml"));
		findComponent();
		initDialog();
		initContentBounds();
		//初始化组件
		initRedisiTree(treeView);
		initListView();
		initDataServer();
		initBottomTabPane();
		initDataTable();
		mainView.getChildren().add(root);
		//初始化窗体
		Scene scene = new Scene(mainView, 0, 0);
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

    public void findComponent() throws IOException {
        treeView = (TreeView<Object>) ((SplitPane)root.getChildren().get(0)).getItems().get(0);
        contentPane = (Pane) ((SplitPane)root.getChildren().get(0)).getItems().get(1);
        tabPane = (TabPane) contentPane.lookup("#showHashTabPane");
        listView = (ListView) contentPane.lookup("#fieldListView");
        //server页面
        redisServer = FXMLLoader.load(Main.class.getResource("server.fxml"));
        dataServer = FXMLLoader.load(Main.class.getResource("data.fxml"));
        findTable = (TableView) root.lookup("#dataTable");
        consoleTextArea = (TextArea) root.lookup("#consoleTextArea");
    }

    /**
     * 初始化组件的大小
     */
    public void initContentBounds(){
        SplitPane control = (SplitPane) root.lookup("#contentControll");
        control.setPrefWidth(primaryScreenBounds.getMaxX());
        control.setPrefHeight(primaryScreenBounds.getMaxY());
//        control.setDividerPositions(0.3,0.7);
    }

    /**
     * 数据table
     */
    public void initDataTable(){
        findTable.setRowFactory(new Callback<TableView,TableRow>() {
            @Override
            public TableRow call(TableView param) {
                TableRow<TableData> tableRow = new TableRow<>();
                tableRow.setOnMouseClicked(event -> {
                    TableData tableData = tableRow.getItem();
                    if(tableData == null){
                        return;
                    }
                    ListView listView = (ListView) contentPane.lookup("#fieldListView");
                    listView.getItems().clear();
                    //设置key text
                    TextField textField = (TextField) contentPane.lookup("#showKeys");
                    textField.setText(tableData.getKey());
                    //设置field text
                    textField = (TextField) contentPane.lookup("#showFields");
                    if(tableData.getType() == RedisType.HASH){
                        textField.setText(tableData.getField());
                    }else{
                        textField.setText("");
                    }
                    //如果是hash显示field、value
					if(ValidateUtils.isEmpty(tableData.getValue())){
						tableData.getType().query(tableData);
					}
					DesignUtil.createTab(tableData.getKey(),tableData.getValue());
                });
                return tableRow;
            }
        });
        ObservableList<TableColumn<TableData,?>> columns = findTable.getColumns();
        for(int i = 0; i < columns.size(); i++) {
            TableColumn column = (TableColumn) columns.get(i);
            column.setCellValueFactory(new PropertyValueFactory<TableData,String>(FINDTABLECOLUMN[i]));
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
        node.setCellFactory(param -> {
                TreeCellImpl treeCell = new TreeCellImpl();
                treeCell.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->  {
                    TreeCellImpl cell = (TreeCellImpl) event.getSource();
                    Object data = cell.getItem();
                    if(data == null){
                        return;
                    }
                    RedisBean tempBean = null;
                    RedisDB tempDB = null;
                    treeView.setContextMenu(null);
                    if(data instanceof  RedisBean){
                        tempBean = (RedisBean) data;
                        treeView.setContextMenu(tempBean.getMenuType().getContextMenu());
                        Main.ContextMenu_RedisServer.setUserData(tempBean);
                    }else if(data instanceof RedisDB){
                        tempDB = (RedisDB) data;
                        treeView.setContextMenu(tempDB.getMenuType().getContextMenu());
                        Main.ContextMenu_RedisDB.setUserData(tempDB);
                        String tempId = redisDB.getId();
                        redisDB.setId(tempDB.getId());
                        //db列表单机事件
                        if(cell != backCell){
                            if(!JedisUtil.selectDB(tempDB.getIndex())){
                                redisDB.setId(tempId);
                                dialog.show("选择"+tempDB.getText()+"失败。");
                            }else{
                                redisDB.setIndex(tempDB.getIndex());
                                redisDB.setText(tempDB.getText());
                                if(backCell != null){
                                    if(backCell.getItem() instanceof RedisDB){
                                        backCell.setText((((RedisDB) backCell.getItem()).getText()));
                                    }
                                }
                                cell.setText("● " + tempDB.getText());
                                backCell = cell;
                            }
                        }
                    }
                    //双击事件
                    if(event.getClickCount() > 1){
                        if(tempBean != null){
                            //root子列表点击事件、初始化db列表
                            if(tempBean != null && !ValidateUtils.isEmpty(tempBean.getId())){
                                boolean isExpanded = cell.getTreeItem().isExpanded();
                                treeCell.getTreeItem().getChildren().clear();
								List<TreeItem<Object>> tempTreeItems = DesignUtil.createDBTreeItem(tempBean);
								if(tempTreeItems == null){
									return;
								}
                                treeCell.getTreeItem().getChildren().addAll(tempTreeItems);
                                treeCell.getTreeItem().setExpanded(isExpanded);
                                redisDB.setId(tempBean.getId());
                                if(redisDB.getIndex() == null){
                                    redisDB.setIndex(0);
                                }
                            }
                        }else if(tempDB != null){
                            //暂时屏蔽右上角tab功能，待优化
//                                DesignUtil.refreshTable();
                        }
                    }
                });
                return treeCell;
            });
    }

    public void initListView(){
        listView.setCellFactory(param -> {
                ListCellImpl listCell = new ListCellImpl();
                listCell.setOnMouseClicked(event -> {
                    TableData tableData = listCell.getItem();
                    if(tableData == null){
                        return;
                    }
                    String field = tableData.getField();
                    TextField textField = (TextField) contentPane.lookup("#showFields");
                    textField.setText(field);
                    String value = JedisUtil.getHashValue(redisDB.getId(),tableData.getKey(),field);
                    if(!ValidateUtils.isEmpty(value)){
                        DesignUtil.createTab(tableData.getKey() + "-" + field + " [" + tableData.getType() + "]",value);
                    }else{
                        dialog.show("无效的field，请刷新列表。");
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
        });
        /*listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });*/
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
                }else{
                    textField.setDisable(true);
                }
            }
        });
        choiceBox.getSelectionModel().select(0);
    }

    /**
     * 初始化下部的tab
     * 事件真心坑爹
     */
    public void initBottomTabPane(){
        bottomTabPane = (TabPane) root.lookup("#bottomTabPane");
        bottomTabPane.getSelectionModel().select(0);
        bottomTabPane.setOnMousePressed(event -> {
            DesignUtil.changeBottomTab(true);
        });
        for(int i = 1; i < bottomTabPane.getTabs().size(); i++) {
            Image img = ImageConstant.IMG.get(bottomTabPane.getTabs().get(i).getId());
            if(img != null){
                ImageView imageView = new ImageView(img);
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                bottomTabPane.getTabs().get(i).setGraphic(imageView);
            }
            Object contentPane = bottomTabPane.getTabs().get(i).getContent();
			if(contentPane != null){
				if(contentPane instanceof AnchorPane){
					//tab内容块
					AnchorPane anchorPane = (AnchorPane) contentPane;
					anchorPane.setOnMousePressed(event -> {
						BottomTab bottomTab = ((BottomTab)bottomTabPane.getUserData());
						if(bottomTab != null){
							bottomTab.setTarget(true);
						}
					});
					Node wapperTool = anchorPane.lookup("#wapperTool");
					if(wapperTool != null){
						if(wapperTool instanceof Pane){
							((Pane) wapperTool).getChildren().add(0,DesignUtil.createToolBar(bottomTabPane.getTabs().get(i).getText(),true));
						}
					}
				}
			}
        }
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

    class ListCellImpl extends ListCell<TableData>{
        @Override
        protected void updateItem(TableData item, boolean empty) {
            super.updateItem(item, empty);
            if(!empty){
                setText(item.getField());
            }else{
                setText(null);
            }
        }
    }

}
