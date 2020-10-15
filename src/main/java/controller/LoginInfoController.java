package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginInfoController {
	/**
	 * declaring variables
	 */
	@FXML private Label user;
	@FXML private Label date;
	@FXML private Label time;

	private double xOffset = 0;
	private double yOffset = 0;
	
	public void setUser(String s) {
		user.setText(s);
	}
	
	public void setDate(String s) {
		date.setText(s);
	}
	
	public void setTime(String s) {
		time.setText(s);
	}
	
	


	/**
	 * This function loads the start up page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
	public void goToStartUp(ActionEvent event) throws IOException {
		Parent startUp = FXMLLoader.load(getClass().getResource("/view/startUp.fxml"));
		Scene startUpScene = new Scene(startUp);

		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(startUpScene);
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
	
	/**
	 * This function loads the user roles page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
    public void bt_changeDateTimeOnClick(ActionEvent event)  throws IOException {
		System.out.println("bt_changeDateTimeOnClick()");
	
		 FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/changeDateTime.fxml"));
         Parent root = loader.load();

         ChangeDateTimeController controller = loader.getController();
         controller.setParentController(this);
 
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
         stage.show();
		
    }
	
	
}
