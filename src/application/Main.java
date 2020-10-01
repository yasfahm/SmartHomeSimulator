package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			/*
			Pane root = new Pane(); 
			
			Button b = new Button();
			b.setLayoutX(100);
			b.setLayoutY(100);
			b.setText("Hello!");
			root.getChildren().add(b);
			*/
			
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("javafx.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
