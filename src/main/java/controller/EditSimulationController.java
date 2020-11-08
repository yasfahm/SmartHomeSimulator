package controller;

import entity.Room;
import entity.UserRole;
import entity.Window;
import interfaces.SubController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import observerPattern.Subject;
import observerPattern.UserLocationObserver;
import org.apache.commons.lang3.StringUtils;
import service.RoleService;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Controller responsible for the editing of user location
 */
public class EditSimulationController implements Initializable, SubController {
    /**
     * declaring variables
     */
    @FXML
    private ComboBox<String> roomsMove;
    @FXML
    private ComboBox<String> roomsBlock;
    @FXML
    private ComboBox<String> windows;
    @FXML
    private Label userToMove;
    @FXML
    private Label windowToBlock;
    @FXML
    private AnchorPane locationDisplay;
    @FXML
    private TextArea windowNote;
    @FXML
    private Label windowBlockStatus;
    @FXML
    private Button button;
    private Map<String, Room> house;
    private String username;
    private double xOffset = 0;
    private double yOffset = 0;
    private static Room room;

    private static Map<String, String> userLocations;

    public static Map<String, String> getUserLocations() {
        return userLocations;
    }

    /**
     * This function loads the login info page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the view file cannot be read
     */
    public void goToLoginInfo(ActionEvent event) throws IOException {
        Parent loginInfo = FXMLLoader.load(getClass().getResource("/view/loginInfo.fxml"));
        Scene loginInfoScene = new Scene(loginInfo);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginInfoScene);
        window.show();
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
     * @param event The event triggering this function
     */
    public void getLocation(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Changes the location of the window(stage) based on the mouse location..
     *
     * @param event The event that called this function
     */
    public void move(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setX(event.getScreenX() - xOffset);
        window.setY(event.getScreenY() - yOffset);
    }

    /**
     * This function will add the rooms to the combobox
     *
     * @param location  The URL of the resource file
     * @param resources The set of resources used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username = LoginInfoController.getUsername();
        userToMove.setText("Move " + username + " to:");
        house = LoginInfoController.getHouse();
        if (Objects.nonNull(house)) {
            roomsMove.getItems().addAll(house.keySet());
            roomsMove.getItems().add("Outside");
            roomsMove.getSelectionModel().select("Outside");
        } else {
            roomsMove.getItems().add("Unknown");
        }
        locationDisplay.getChildren().add(processRows());

        windowToBlock.setText("Select Window: ");
        if (Objects.nonNull(house)) {
            roomsBlock.getItems().addAll(house.keySet());
            roomsBlock.getSelectionModel().selectFirst();

            roomsBlock.setOnAction(event -> {
                String roomName = roomsBlock.getValue();
                room = house.get(roomName);
                windows.getItems().clear();
                for (Window window : room.getWindows()) {
                    windows.getItems().add(window.getPosition().toString());
                }
            });
            windows.getSelectionModel().selectFirst();
        }
    }

    /**
     * This function will return a list containing blocking status of windows
     *
     * @param room the room passed to function
     * @return list of blocked status of windows
     */
    public String[] windowsList(Room room) {
        String[] list = new String[room.getWindows().size()];
        for (int i = 0; i < room.getWindows().size(); i++) {
            if (room.getWindows().get(i).getBlocking()) {
                list[i] = room.getWindows().get(i).getPosition().toString() + ": " + "true";
            } else {
                list[i] = room.getWindows().get(i).getPosition().toString() + ": " + "false";
            }
        }
        return list;
    }

    /**
     * Function responsible for unblocking/blocking windows
     *
     * @param event The event that called this function
     */
    public void windowsBlocked(ActionEvent event) {
    	int selectedWindow = 0;
        for (int i = 0; i < room.getWindows().size(); i++) {
            if (room.getWindows().get(i).getPosition().toString().equals(windows.getValue())) {
                selectedWindow = i;
                String message = "";
                if(!room.getWindows().get(selectedWindow).getBlocking()) {
                	room.getWindows().get(selectedWindow).setBlocking(true);
                	message = "The window at the " + windows.getValue() + " in the " + room.getName() + " has been blocked.\n";
                	updateWindowBlockStatus(true);
                } else {
                	room.getWindows().get(selectedWindow).setBlocking(false);
                	message = "The window at the " + windows.getValue() + " in the " + room.getName() + " has been unblocked.\n";
                	updateWindowBlockStatus(false);
                }
                String log = windowNote.getText();
                windowNote.setText(log + message);
                LoginInfoController.consoleLogFile("The window at the " + windows.getValue() + " in the " + room.getName() + " has been blocked.");
                break;
            }
        }
    }

    /**
     * Action event on change dynamic content with combobox for window list
     * @param event The event that called this function
     */
    public void cb_onWindowChange(ActionEvent event) {
    	int selectedWindow = 0;
        for (int i = 0; i < room.getWindows().size(); i++) {
            if (room.getWindows().get(i).getPosition().toString().equals(windows.getValue())) {
            	selectedWindow = i;
            	updateWindowBlockStatus(room.getWindows().get(selectedWindow).getBlocking());
        	}
        }
    }
    
    /**
     * Action event on change combobox for room list
     * @param event The event that called this function
     */
    public void cb_selectWindowOnChange(MouseEvent event) {
    	this.windowBlockStatus.setText(" ");
    	this.button.setText("Action");
    }
    
    /**
     * Update windows status label
     * @param bool The status of windows (blocked/unblocked)
     */
    public void updateWindowBlockStatus(boolean bool) {
    	this.windowBlockStatus.setText(bool ? "Blocked" : " ");
    	this.button.setText(bool? "Unblock" : "Block");
    }

    /**
     * Method that will create the grid placed in the {@link AnchorPane}.
     * Also calls the {@link RoleService} to obtain the Users and Roles.
     *
     * @return The grid pane used by the display
     */
    private GridPane processRows() {
        AtomicInteger index = new AtomicInteger();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        if (Objects.isNull(userLocations)) {
            userLocations = RoleService.getRoles(LoginInfoController.getUserParent()).stream().collect(Collectors.toMap(
                    UserRole::getUsername,
                    userRole -> "Outside"
            ));
        }
        userLocations.forEach((key, value) -> {
            gridPane.addRow(gridPane.getRowCount(), createUserLabel(key, key), createUserLabel("Location:", ""), createUserLabel(value, key + "Location"));
            index.getAndIncrement();
        });
        return gridPane;
    }

    /**
     * Creates a Label with the username
     *
     * @param value The value placed in the label
     * @param id    The value used for the label's ID
     * @return The label with the value and ID
     */
    private Node createUserLabel(final String value, final String id) {
        Label userLabel = new Label();
        userLabel.setMinWidth(100);
        userLabel.setId(id);
        userLabel.setText(value);
        return userLabel;
    }

    /**
     * Function responsible for updating the user's location
     *
     * @param event The event that called this function
     */
    public void changeLocation(ActionEvent event) {
        String chosenLocation = roomsMove.getSelectionModel().getSelectedItem();
        if (StringUtils.isNotEmpty(chosenLocation)) {
            userLocations.put(username, chosenLocation);
            if (locationDisplay.lookup("#" + username + "Location") != null) {
                ((Label) locationDisplay.lookup("#" + username + "Location")).setText(chosenLocation);
            }
            Subject subject = new Subject();
            new UserLocationObserver(subject);
            subject.setUserLocations(username, chosenLocation);
            subject.notifyObserver();
            LoginInfoController.consoleLogFile("Moved " + username + " to " + chosenLocation);
            windowNote.appendText("Moved " + username + " to " + chosenLocation+"\n");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Location is empty");
            alert.showAndWait();
        }
    }

    /**
     * Get the roomsMove ComboBox
     *
     * @return roomsMove
     */
    public ComboBox getRoomsMove(){
        return roomsMove;
    }

    /**
     * Deletes the location cached variable
     */
    public static void deleteLocations() {
        userLocations = null;
    }

    /**
     * Sets the username for the user to move
     *
     * @param username The username to set it to
     */
    protected void setUsername(String username) {
        this.username = username;
    }
}
