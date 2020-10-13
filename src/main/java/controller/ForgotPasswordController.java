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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.RegistrationService;

public class ForgotPasswordController {
	/**
	 * declaring variables
	 */
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField passwordVerification;
	@FXML
	private Label displayMessage;

	private double xOffset = 0;
	private double yOffset = 0;

	/**
	 * This function loads the login page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
	public void goToLoginPage(ActionEvent event) throws IOException {
		Parent forgotPassword = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene forgotPasswordScene = new Scene(forgotPassword);

		/**
		 * Stage info
		 */
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(forgotPasswordScene);
		window.show();
	}

	/**
	 * This function update the user's password and shows the proper message.
	 * @param event
	 */
	public void updatePassword(ActionEvent event) {
		RegistrationStatus status = RegistrationService.updatePassword(username.getText(), password.getText(), passwordVerification.getText());
		switch (status) {
			case PASSWORD_UPDATED: {
				displayMessage.setTextFill(Color.BLACK);
				displayMessage.setText("Password has been updated");
				//setPrompt(Alert.AlertType.INFORMATION, "Password has been created");
				break;
			}
			case PASSWORD_NOT_EQUAL: {
				displayMessage.setTextFill(Color.RED);
				displayMessage.setText("Passwords are not equal");
				break;
			}
			case USERNAME_NOT_FOUND: {
				displayMessage.setTextFill(Color.RED);
				displayMessage.setText("Username not found");
				break;
			}
			default: {
				displayMessage.setTextFill(Color.RED);
				displayMessage.setText("Seems like an error occurred");
			}
		}
	}

	/**
	 * This function will close the application
	 * @param event
	 * @throws IOException
	 */
	public void close(MouseEvent event) throws IOException {
		System.exit(0);
	}

	/**
	 * Gets the location of a mouse.
	 * @param event
	 */
	public void handle(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	/**
	 * Changes the location of the window(stage) based on the mouse location..
	 * @param event
	 */
	public void move(MouseEvent event) {
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setX(event.getScreenX() - xOffset);
		window.setY(event.getScreenY() - yOffset);
	}
}
