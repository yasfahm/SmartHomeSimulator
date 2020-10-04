package application;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class loginController {
	
	@FXML private TextField userD;
	@FXML private PasswordField passD;
	
	
	//going to forgot password scene
	public void goToForgotPassword(ActionEvent event) throws IOException {
		Parent forgotPassword = FXMLLoader.load(getClass().getResource("forgotPassword.fxml"));
		Scene forgotPasswordScene = new Scene(forgotPassword);
		
		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(forgotPasswordScene);
		window.show();
	}
	
	//going to sign up scene
		public void goToSignUp(ActionEvent event) throws IOException {
			Parent signUp = FXMLLoader.load(getClass().getResource("signUp.fxml"));
			Scene signUpScene = new Scene(signUp);
			
			// stage info
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
			window.setScene(signUpScene);
			window.show();
		}
	
	//show the login info
	public void loginInfo(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("loginInfo.fxml"));
		Parent login = loader.load();
		
		Scene loginScene = new Scene(login);
		
		loginInfoController controller = loader.getController();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		String date = dtf.format(now);
		
		controller.setUser(userD.getText());
		controller.setPass(passD.getText());
		controller.setDate(date);
		
		//userD.setText(controller.getUser().getText());
		//passD.setText(controller.getPass().getText());
		//controller.getUser()
		
		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(loginScene);
		window.show();
	}
		
	public void close(MouseEvent event) throws IOException {
		System.exit(0);
	}
}
