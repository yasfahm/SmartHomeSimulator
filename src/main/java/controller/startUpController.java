package controller;

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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

public class startUpController {
	/**
	 * declaring variables
	 */
	@FXML private TextField userD;
	@FXML private PasswordField passD;
	@FXML private Label displayMessage;

	private double xOffset = 0;
	private double yOffset = 0;

	/**
	 * This function loads the login info page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
	public void goToLoginInfo(ActionEvent event) throws IOException {
		Parent loginInfo = FXMLLoader.load(getClass().getResource("/view/loginInfo.fxml"));
		Scene loginInfoScene = new Scene(loginInfo);

		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(loginInfoScene);
		window.show();
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
	public void getLocation(MouseEvent event) {
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
