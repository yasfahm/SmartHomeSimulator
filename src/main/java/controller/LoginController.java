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

/**
 * Controller responsible for the control and flow of the Login scene
 */
public class LoginController {
	/**
	 * declaring variables
	 */
	@FXML private TextField userD;
	@FXML private PasswordField passD;
	@FXML private Label displayMessage;

	private double xOffset = 0;
	private double yOffset = 0;

	/**
	 * This function loads the forgot password page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
	public void goToForgotPassword(ActionEvent event) throws IOException {
		Parent forgotPassword = FXMLLoader.load(getClass().getResource("/view/forgotPassword.fxml"));
		Scene forgotPasswordScene = new Scene(forgotPassword);

		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(forgotPasswordScene);
		window.show();
	}

	/**
	 * This function loads the sign up page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
	public void goToSignUp(ActionEvent event) throws IOException {
		Parent signUp = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
		Scene signUpScene = new Scene(signUp);
			
		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(signUpScene);window.show();
	}

	/**
	 * This function loads the loginInfo page(scene) into the window(stage)
	 * @param event
	 * @throws IOException
	 */
	public void loginInfo(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/loginInfo.fxml"));
		Parent login = loader.load();
		
		System.out.println("Location:=" + loader.getLocation());
		
		Scene loginScene = new Scene(login);
		
		LoginInfoController controller = loader.getController();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);
		
		String time = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());

		Map<String, Object> userInfo = LoginService.login(userD.getText(), passD.getText());

		if (Objects.nonNull(userInfo)) {
			controller.setUser(userInfo.get("firstname").toString() + " " + userInfo.get("lastname").toString());
			controller.setDate(date);
			controller.setTime(time);
			// stage info
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
			window.setScene(loginScene);
			window.show();
		} else {
			// Create Popup with no user correlating to given username and password
			displayMessage.setText("Incorrect username and password");
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
