package controller;

import entity.Room;
import entity.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
public class EditSimulationController implements Initializable {
    /**
     * declaring variables
     */
    @FXML
    private ComboBox<String> rooms;
    @FXML
    private Label userToMove;
    @FXML
    private AnchorPane locationDisplay;
    private Map<String, Room> house;
    private String username;
    private double xOffset = 0;
    private double yOffset = 0;

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

        // stage info
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
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username = LoginInfoController.getUsername();
        userToMove.setText("Move " + LoginInfoController.getUsername() + " To:");
        house = LoginInfoController.getHouse();
        if (Objects.nonNull(house)) {
            rooms.getItems().addAll(house.keySet());
            rooms.getItems().add("Outside");
            rooms.getSelectionModel().select("Outside");
        } else {
            rooms.getItems().add("Unknown");
        }
        locationDisplay.getChildren().add(processRows());
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
                    userRole -> "Unknown"
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
        String chosenLocation = rooms.getSelectionModel().getSelectedItem();
        if (StringUtils.isNotEmpty(chosenLocation)) {
            userLocations.put(username, chosenLocation);
            ((Label) locationDisplay.lookup("#" + username + "Location")).setText(chosenLocation);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Location is empty");
            alert.showAndWait();
        }
    }

    public static void deleteLocations() {
        userLocations = null;
    }
}
