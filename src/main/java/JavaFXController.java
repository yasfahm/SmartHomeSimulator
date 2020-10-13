import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.stage.StageStyle;


public class JavaFXController extends Application implements EventHandler<ActionEvent> {
	
	Button button;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		
//		primaryStage.setTitle("TEST");
//		
//		button = new Button();
//		button.setText("test");
//		button.setOnAction(this);
//		
//		StackPane layout = new StackPane();
//		layout.getChildren().add(button);
//		
//		BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Sample.fxml"));
//		Scene scene = new Scene(layout,400,400);
//		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//		primaryStage.setScene(scene);
//		primaryStage.show();
		
		
		Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene scene = new Scene(root,700,400);
		scene.getStylesheets().add(getClass().getResource("/view/application.css").toExternalForm());

		//removing title bar from the window(stage)
		primaryStage.initStyle(StageStyle.UNDECORATED);

		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		
	}
	
//	@Override
//	public void handle(ActionEvent event) {
//		if(event.getSource()==button) {
//			System.out.println("test");
//		}
//	}

	/**
	 * Main method which runs the program.
	 * @param
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void handle(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
