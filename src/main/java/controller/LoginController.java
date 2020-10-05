package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

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
import service.LoginService;

public class LoginController {
	
	@FXML private TextField userD;
	@FXML private PasswordField passD;
	
	
	//going to forgot password scene
	public void goToForgotPassword(ActionEvent event) throws IOException {
		Parent forgotPassword = FXMLLoader.load(getClass().getResource("/view/forgotPassword.fxml"));
		Scene forgotPasswordScene = new Scene(forgotPassword);
		
		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(forgotPasswordScene);
		window.show();
	}
	
	//going to sign up scene
	public void goToSignUp(ActionEvent event) throws IOException {
		Parent signUp = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
		Scene signUpScene = new Scene(signUp);
			
		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(signUpScene);window.show();
	}
	
	//show the login info
	public void loginInfo(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/loginInfo.fxml"));
		Parent login = loader.load();
		
		Scene loginScene = new Scene(login);
		
		LoginInfoController controller = loader.getController();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		String date = dtf.format(now);

		Map<String, Object> userInfo = LoginService.login(userD.getText(), passD.getText());

		if (Objects.nonNull(userInfo))
		{
			controller.setUser(userInfo.get("firstname").toString());
			controller.setPass(userInfo.get("lastname").toString());
			controller.setDate(date);

			//userD.setText(controller.getUser().getText());
			//passD.setText(controller.getPass().getText());
			//controller.getUser()

			// stage info
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
			window.setScene(loginScene);
			window.show();
		}
		else
		{
			// Create Popup with no user correlating to given username and password
			System.out.println("Incorrect username and password");
		}

	}
		
	public void close(MouseEvent event) throws IOException {
		System.exit(0);
	}
}
