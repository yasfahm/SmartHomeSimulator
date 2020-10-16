package controller;

import entity.Door;
import entity.Position;
import entity.Room;
import entity.Window;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import service.HouseLayoutService;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class LoginInfoController implements Initializable {

    /**
     * declaring variables
     */
    @FXML
    private Label user;
    @FXML
    private Label date;
    @FXML
    private Canvas houseRender;
    @FXML
    private Label time;
    @FXML
    private Hyperlink loc;

    private static String userParent;

    private GraphicsContext gc;
    private double xOffset = 0;
    private double yOffset = 0;
    private final int ROOM_SIZE = 75;
    private final int DOOR_SIZE = ROOM_SIZE - 55;
    private long timeInMillis;

    /**
     * Function to set the user's location
     *
     * @param s
     */
    public void setLoc(String s) {
        loc.setText(s);
    }

    /**
     * Function to set the user
     *
     * @param s
     */
    public void setUser(String s) {
        user.setText(s);
    }

    /**
     * Function to set the date
     *
     * @param s
     */
    public void setDate(String s) {
        date.setText(s);
    }

    /**
     * Function to setting new time label
     *
     * @param s
     */
    public void setTime(String s) {
        SimpleDateFormat formatFull = new SimpleDateFormat("yyyy - MMMM - dd HH:mm:ss");
        try {
            Date d = formatFull.parse(s);
            this.timeInMillis = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void setUserParent(String userParent) {
        LoginInfoController.userParent = userParent;
    }

    public static String getUserParent() {
        return userParent;
    }

    /**
     * Animation controller for the clock
     */
    private void moveClock() {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        this.timeInMillis += 1000;
        cal.setTimeInMillis(timeInMillis);
        date.setText(formatDate.format(cal.getTime()));
        time.setText(formatTime.format(cal.getTime()));
    }

    /**
     * Function from interface Initializable, initialize default actions
     *
     * @param location, resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
        SimpleDateFormat farmatTime = new SimpleDateFormat("HH:mm:ss");
        long sysmillis = System.currentTimeMillis();
        this.timeInMillis = sysmillis;
        Date d = new Date(sysmillis);
        this.date.setText(formatDate.format(d));
        this.time.setText(farmatTime.format(d));

        // Clock animation
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO,
                        e -> moveClock()
                ),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * This function loads the login page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToLogin(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(login);

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /**
     * This function loads the user roles page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToUserSettings(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/userRoles.fxml"));
        Parent userRoles = loader.load();
        Scene userRolesScene = new Scene(userRoles);

        UserRolesController controller = loader.getController();
        controller.setUsername(user.getText());

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(userRolesScene);
        window.show();
    }

    /**
     * This function allows the user to add a house layout text file and parses the JSON data obtained
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void addHouseLayout(ActionEvent event) throws IOException {

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(window);

        Room[] roomsArray = HouseLayoutService.parseHouseLayout(file);

        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomsArray) {
            rooms.put(room.getName(), room);
        }

        Set<Room> traversed = new HashSet<>();

        gc = houseRender.getGraphicsContext2D();
        gc.setFont(new Font(10));

        int lastX = 90, lastY = 170;
        drawRoom(rooms, roomsArray[0], traversed, Position.NONE, lastX, lastY);
    }

    /**
     * @param x  first x coordinate of the door
     * @param y  second x coordinate of the door
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
     * @param x    x coordinate of top left corner of the room
     * @param y    y coordinate top left corner of the room
     */
    public void drawWindows(Room room, int x, int y) {
        gc.setLineWidth(3);
        gc.setStroke(Color.LIGHTBLUE);
        for (Window window : room.getWindows()) {
            switch (window.getPosition()) {
                case NONE -> {
                }
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

    /**
     * Method responsible for drawing the lights in a room
     *
     * @param x  x coordinate of the light
     * @param y  y coordinate of the light
     * @param on boolean value for lights on/off
     */
    public void drawLight(int x, int y, boolean on) {
        if (on) {
            gc.setFill(Color.GOLD);
        }
    }

    /**
     * Method responsible for drawing the rooms in their correct location
     *
     * @param roomHashMap A map of the rooms and their names as the key
     * @param room        room to draw
     * @param visited     rooms that have been visited
     * @param previous    position of the previously visited room
     * @param x           x coordinate of the previously visited room
     * @param y           y coordinate of the previously visited room
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
        drawLight(x + ROOM_SIZE / 2 - 5, y + ROOM_SIZE / 2 - 5, false);
        for (Door child : room.getDoors()) {
            Room nextRoom = roomHashMap.get(child.getConnection());
            if (!visited.contains(nextRoom))
                drawRoom(roomHashMap, nextRoom, visited, child.getPosition(), x, y);
        }
    }

    /**
     * This function will close the application
     *
     * @param event The event that called this function
     */
    public void close(MouseEvent event) {
        System.exit(0);
    }

    /**
     * Gets the location of a mouse.
     *
     * @param event The event that called this function
     */
    public void getLocation(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Changes the location of the window(stage) based on the mouse location.
     *
     * @param event The event that called this function
     */
    public void move(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setX(event.getScreenX() - xOffset);
        window.setY(event.getScreenY() - yOffset);
    }

    /**
     * This function loads the user roles page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the scene file cannot be read
     */
    public void bt_changeDateTimeOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/changeDateTime.fxml"));
        Parent root = loader.load();

        ChangeDateTimeController controller = loader.getController();
        controller.setParentController(this);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * This function loads the edit(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToEdit(ActionEvent event) throws IOException {
        Parent edit = FXMLLoader.load(getClass().getResource("/view/editSimulation.fxml"));
        Scene editScene = new Scene(edit);

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(editScene);
        window.show();
    }


}