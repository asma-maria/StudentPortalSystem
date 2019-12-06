package controllers;

import animation.Shaker;
import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import database.DBHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

	@FXML private AnchorPane loginPane;
    @FXML private JFXTextField loginUsername;
    @FXML private JFXPasswordField loginPassword;
    @FXML private JFXButton loginButton;
    @FXML private JFXComboBox<String> loginBox;

    @FXML
    void initialize() {

    	loginBox.getItems().add("Admin");
    	loginBox.getItems().add("Student");
    	
        loginButton.setOnAction(e->{
            String username = loginUsername.getText().trim();
            String password = loginPassword.getText().trim();
            
            if(checkEmpty(username, password)) return;
            
            boolean isAUser = checkUser(username, password);
            
            if(isAUser) {
            	loginButton.getScene().getWindow().hide();
				System.out.println("found");
				Stage stage = new Stage();
				Parent root = null;
				try {
					root = FXMLLoader.load(getClass().getResource("/views/dashboard.fxml"));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				stage.setTitle("Dashboard");
				stage.setScene(new Scene(root));
				stage.show();
//            	goToDashboard();
//            	AnchorPane detailsPane;
//				try {
//					detailsPane = FXMLLoader.load(getClass().getResource("/views/admin_dashboard.fxml"));
//					loginPane.getChildren().setAll(detailsPane);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
            	
            }
        });
    }
    
    private boolean checkEmpty(String username, String password) {
    	JFXSnackbar jfxSnackbar = new JFXSnackbar(loginPane);
        if(username.equals("")){
            new Shaker(loginUsername).shake();
            jfxSnackbar.enqueue(new SnackbarEvent("Username Empty"));
            return true;
        }
        if(password.equals("")){
            new Shaker(loginPassword).shake();
            jfxSnackbar.enqueue(new SnackbarEvent("Password Empty"));
            return true;
        }
        
        if(loginBox.getSelectionModel().isEmpty()) {
            new Shaker(loginBox).shake();
            jfxSnackbar.enqueue(new SnackbarEvent("Select User"));
        	return true;
        }
        return false;
    }

	private boolean checkUser(String username, String password) {
		boolean isAUser = false;
		
		String userType = loginBox.getSelectionModel().getSelectedItem().toLowerCase();
		String query = "SELECT * FROM users WHERE type = ? and username = ? and password = ?";
		
		try {
			Connection connection = DBHandler.connect();
			if(connection==null){
				JFXSnackbar jfxSnackbar = new JFXSnackbar(loginPane);
				jfxSnackbar.enqueue(new SnackbarEvent("Cant connect to database!"));
				return false;
			}
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, userType);
			ps.setString(2, username);
			ps.setString(3, password);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				System.out.println("Found");
				isAUser = true;
				
				// go to dashboard
			}else {
				JFXSnackbar jfxSnackbar = new JFXSnackbar(loginPane);
				new Shaker(loginPassword).shake();
	            jfxSnackbar.enqueue(new SnackbarEvent("User not found"));
			}
			rs.close();
			ps.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}

		return isAUser;
	}
}
