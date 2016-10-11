package zx.design;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import zx.constant.Constant;

import java.io.IOException;

/**
 * 功能描述：
 * 时间：2016/3/31 10:45
 *
 * @author ：zhaokuiqiang
 */
public class Dialog extends GridPane{

    Stage stage = null;

    @FXML
    Button dialogOK;

    @FXML
    Label dialogDetail;

    private static GridPane dialog;

    public Dialog(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dialog.fxml"));
            loader.setController(this);
            dialog = loader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
        dialogOK.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.hide();
            }
        });
    }

    public void show(String message){
        if(stage == null){
            stage = new Stage();
            stage.setScene(new Scene(dialog));
            stage.getIcons().add(Constant.ICONIMG);
            stage.initModality(Modality.APPLICATION_MODAL);
        }else{
        }
        dialogDetail.setText(message);
        stage.show();
    }

    public void close(){
        stage.hide();
    }

    @FXML
    public void mouseClicked(){
        stage.hide();
    }
}
