package controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import entity.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.*;

public class LoginInfoController {
	/**
	 * declaring variables
	 */
	@FXML private Label user;
	@FXML private Label date;
	@FXML private Canvas houseRender;
	@FXML private Label time;

	private GraphicsContext gc ;

	private double xOffset = 0;
	private double yOffset = 0;

	private final int ROOM_SIZE = 75;
	private final int DOOR_SIZE = ROOM_SIZE - 55;

	public void setUser(String s) {
		user.setText(s);
	}
	
	public void setDate(String s) {
		date.setText(s);
	}
	
	public void setTime(String s) {
		time.setText(s);
	}

	//going to forgot password scene
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
	 * This function allows the user to add a house layout text file and parses the JSON data obtained
	 *
	 * @param event
	 * @throws IOException
	 */
	public void addHouseLayout(ActionEvent event) throws IOException {

		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter =
				new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(window);

		String marshalled = Files.readString(file.toPath(), StandardCharsets.US_ASCII);

		JSONObject parsed = new JSONObject(marshalled);

		JSONArray roomsJSON = parsed.getJSONArray("rooms");
		Room[] roomsArray = new Room[roomsJSON.length()];

		for (int i=0; i<roomsJSON.length(); i++){
			JSONObject room = roomsJSON.getJSONObject(i);
			String name = roomsJSON.getJSONObject(i).getString("name");
			int lightsTotal = roomsJSON.getJSONObject(i).getInt("lights");

			JSONArray windowsJSON = room.getJSONArray("windows");
			ArrayList<Window> windows = new ArrayList<Window>();

			JSONArray doorsJSON = room.getJSONArray("doors");
			ArrayList<Door> doors = new ArrayList<Door>();

			for (int j=0; j<windowsJSON.length();j++){
				int position = windowsJSON.getJSONObject(j).getInt("position");
				windows.add(new Window(getPosition(position)));
			}

			for (int j=0; j<doorsJSON.length();j++){
				int position = doorsJSON.getJSONObject(j).getInt("position");
				String connection = doorsJSON.getJSONObject(j).getString("connection");
				switch (position) {
					case 0:
						doors.add(new Door(Position.NONE, connection));
						break;
					case 1:
						doors.add(new Door(Position.TOP, connection));
					case 2:
						doors.add(new Door(Position.RIGHT, connection));
						break;
					case 3:
						doors.add(new Door(Position.BOTTOM, connection));
						break;
					case 4:
						doors.add(new Door(Position.LEFT, connection));
						break;
				}
			}
			roomsArray[i] = new Room(name, windows, doors, lightsTotal);
		}

		HashMap<String, Room> rooms = new HashMap<String, Room>();
		for (Room room : roomsArray) rooms.put(room.getName(), room);

		Set<Room> traversed = new HashSet<Room>();

		gc = houseRender.getGraphicsContext2D();
		gc.setFont(new Font(10));

		int lastX = 90, lastY = 170;
		drawRoom(rooms, roomsArray[0], traversed, Position.NONE, lastX, lastY);
	}

	/**
	 * @param x position index
	 * @return Position enum value
	 */
	public Position getPosition(int x) {
		switch (x){
			case 0 -> {return Position.NONE;}
			case 1 -> {return Position.TOP;}
			case 2 -> {return Position.RIGHT;}
			case 3 -> {return Position.BOTTOM;}
			case 4 -> {return Position.LEFT;}
		}
		return Position.NONE;
	}

	/**
	 * @param x first x coordinate of the door
	 * @param y second x coordinate of the door
	 * @param x2 first y coordinate of the door
	 * @param y2 second y coordinate of the door
	 */
	public void drawDoor(int x, int y, int x2, int y2) {
		gc.setLineWidth(3);
		gc.strokeLine(x, y, x2, y2);
		gc.setLineWidth(1);
	}

	/**
	 * This function draws windows on the house layout
	 *
	 * @param room room where window is drawn
	 * @param x x coordinate of top left corner of the room
	 * @param y y coordinate top left corner of the room
	 */
	public void drawWindows(Room room, int x, int y){
		gc.setLineWidth(3);
		gc.setStroke(Color.LIGHTBLUE);
		for (Window window : room.getWindows()) {
			switch (window.getPosition()){
				case NONE -> {}
				case BOTTOM -> {
					gc.strokeLine(x + (ROOM_SIZE - DOOR_SIZE) / 2, y + ROOM_SIZE, x + (ROOM_SIZE - DOOR_SIZE) / 2 + DOOR_SIZE, y + ROOM_SIZE);
				}
				case RIGHT -> {
					gc.strokeLine(x + ROOM_SIZE, y + (ROOM_SIZE - DOOR_SIZE) / 2, x + ROOM_SIZE, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
				}
				case TOP -> {
					gc.strokeLine(x + (ROOM_SIZE - DOOR_SIZE) / 2, y, x + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2, y);
				}
				case LEFT -> {
					gc.strokeLine(x, y + (ROOM_SIZE - DOOR_SIZE) / 2, x, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
				}
			}
		}
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
	}

	public void drawLight(int x, int y, boolean on) {
		if(on)
			gc.setFill(Color.GOLD);
	}

	/**
	 * @param roomHashMap
	 * @param room room to draw
	 * @param visited rooms that have been visited
	 * @param previous position of the previously visited room
	 * @param x x coordinate of the previously visited room
	 * @param y y coordinate of the previously visited room
	 */
	public void drawRoom(HashMap<String, Room> roomHashMap, Room room, Set<Room> visited, Position previous, int x, int y) {
		visited.add(room);
		switch (previous) {
			case NONE -> {
				gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
				gc.fillText(room.getName(), x + 10, y + 15);
				drawDoor(x + (ROOM_SIZE - DOOR_SIZE) / 2, y + ROOM_SIZE, x + (ROOM_SIZE - DOOR_SIZE) / 2 + DOOR_SIZE, y + ROOM_SIZE);
			}
			case BOTTOM -> {
				drawDoor(x + (ROOM_SIZE - DOOR_SIZE) / 2, y, x + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2, y);
				y += ROOM_SIZE;
				gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
				gc.fillText(room.getName(), x + 10, y + 15);
			}
			case RIGHT -> {
				drawDoor(x + ROOM_SIZE, y + (ROOM_SIZE - DOOR_SIZE) / 2, x + ROOM_SIZE, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
				x += ROOM_SIZE;
				gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
				gc.fillText(room.getName(), x + 10, y + 15);
			}
			case TOP -> {
				drawDoor(x + (ROOM_SIZE - DOOR_SIZE) / 2, y, x + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2, y);
				y -= ROOM_SIZE;
				gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
				gc.fillText(room.getName(), x + 10, y + 15);
			}
			case LEFT -> {
				drawDoor(x, y + (ROOM_SIZE - DOOR_SIZE) / 2, x, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
				x -= ROOM_SIZE;
				gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
				gc.fillText(room.getName(), x + 10, y + 15);
			}
		}
		drawWindows(room, x, y);
		drawLight((int) x + ROOM_SIZE/2 - 5, (int) y + ROOM_SIZE/2 - 5, false);
		for (Door child: room.getDoors()){
			Room nextRoom = roomHashMap.get(child.getConnection());
			if(!visited.contains(nextRoom))
				drawRoom(roomHashMap, nextRoom, visited, child.getPosition(), x, y);
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
