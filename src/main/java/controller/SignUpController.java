package controller;

import java.io.IOException;

import constants.RegistrationStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.RegistrationService;

public class SignUpController {
	@FXML
	private TextField username;
	@FXML
	private TextField firstname;
	@FXML
	private TextField lastname;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField passwordVerification;

	private Alert alert;

	public void goToLoginPage(ActionEvent event) throws IOException {
		Parent loginPage = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene loginScene = new Scene(loginPage);

		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(loginScene);
		window.show();
	}

	public void processRegistration(ActionEvent event) {
		RegistrationStatus status = RegistrationService.registration(username.getText(), firstname.getText(), lastname.getText(), password.getText(), passwordVerification.getText());

		switch (status) {
			case USER_CREATED: {
				setPrompt(Alert.AlertType.INFORMATION, "User has been created");
				break;
			}
			case PASSWORD_NOT_EQUAL: {
				setPrompt(Alert.AlertType.WARNING, "Passwords are not equal");
				break;
			}
			case NOT_UNIQUE_USERNAME: {
				setPrompt(Alert.AlertType.WARNING, "Username has already been taken");
				break;
			}
			case NAME_IS_EMPTY: {
				setPrompt(Alert.AlertType.WARNING, "First name and Last name must not be empty");
				break;
			}
			default: {
				setPrompt(Alert.AlertType.ERROR, "Seems like an error occurred");
			}
		}

		alert.showAndWait();
	}

	private void setPrompt(final Alert.AlertType alertType, final String message) {
		alert = new Alert(alertType);
		alert.setHeaderText(null);
		alert.setContentText(message);
	}
}
