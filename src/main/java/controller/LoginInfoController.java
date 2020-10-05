package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginInfoController {
	
	@FXML private Label user;
	@FXML private Label pass;
	@FXML private Label date;
	
	public void setUser(String s) {
		user.setText(s);
	}
	
	public void setPass(String s) {
		pass.setText(s);
	}
	
	public void setDate(String s) {
		date.setText(s);
	}
	
	//going to forgot password scene
	public void goToLoginPage(ActionEvent event) throws IOException {
		Parent forgotPassword = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene forgotPasswordScene = new Scene(forgotPassword);
		
		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(forgotPasswordScene);
		window.show();
	}
}
