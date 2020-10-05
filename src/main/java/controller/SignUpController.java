package controller;

import java.io.IOException;

import constants.RegistrationStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.RegistrationService;

public class SignUpController
{
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField passwordVerification;

	public void goToLoginPage(ActionEvent event) throws IOException {
		Parent loginPage = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene loginScene = new Scene(loginPage);

		RegistrationStatus status = RegistrationService.registration(username.getText(), password.getText(), passwordVerification.getText());

		// TODO something should appear depending on the status

		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(loginScene);
		window.show();
	}
}
