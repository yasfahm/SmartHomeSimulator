package controller;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class LoginInfoController {
	/**
	 * declaring variables
	 */
	@FXML private Label user;
	@FXML private Label date;
	private Desktop desktop = Desktop.getDesktop();

	private double xOffset = 0;
	private double yOffset = 0;
	public void setUser(String s) {
		user.setText(s);
	}
	
	public void setDate(String s) {
		date.setText(s);
	}

	//going to forgot password scene
	/**
	 * This function loads the login page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
	public void goToLoginPage(ActionEvent event) throws IOException {
		Parent forgotPassword = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene forgotPasswordScene = new Scene(forgotPassword);
		
		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(forgotPasswordScene);
		window.show();
	}

	/**
	 * This function loads the user roles page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
	public void goToUserSettings(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/userRoles.fxml"));
		Parent userRoles = loader.load();
		Scene userRolesScene = new Scene(userRoles);

		UserRolesController controller = loader.getController();
		controller.setUsername(user.getText());

		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(userRolesScene);
		window.show();
	}
	public void addHouseLayout(ActionEvent event) throws IOException {

		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter =
				new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(window);

		if (file != null) System.out.println(":)");


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
