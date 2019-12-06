package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class HomeController {
    @FXML private AnchorPane homeDetailsPane;
    @FXML private AnchorPane homeButtonsPane;
    @FXML private JFXButton homeLoginBtn;
    @FXML private JFXButton homeExitButton;
    @FXML private AnchorPane homeTopPanel;

    @FXML
    void initialize() {
        homeLoginBtn.setOnAction(event -> {
            homeTopPanel.setStyle("-fx-background-color:#78561C");
            try {
            	AnchorPane detailsPane = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            	homeDetailsPane.getChildren().setAll(detailsPane);
            }catch(IOException e) {
            	e.printStackTrace();
            }
            
            homeButtonsPane.setVisible(false);
        });
        
        homeExitButton.setOnAction(event -> homeExitButton.getScene().getWindow().hide());

    }
}
