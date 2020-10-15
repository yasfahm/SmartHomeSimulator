package controller;

import constants.UserRoles;
import entity.UserRole;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.RegistrationService;
import service.RoleService;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller responsible for the view for User Control
 */
public class UserRolesController {

    /**
     * Values is the location for the username label, the role box, and the remove button
     */
    @FXML
    public AnchorPane values;
    /**
     * Cached value for the name displayed in the main menu
     */
    private String username;

    /**
     * On initialization, add the grid of information into values
     */
    @FXML
    public void initialize() {
        values.getChildren().add(processRows());
    }

    /**
     * Button function to go back to the Main Menu
     *
     * @param event The event that triggered this method
     * @throws IOException Thrown if the method is unable to locate the view resource
     */
    public void goToLoginInfo(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/loginInfo.fxml"));
        Parent login = loader.load();
        Scene loginScene = new Scene(login);

        LoginInfoController controller = loader.getController();
        controller.setUser(username);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /**
     * Setter method to set the cached value of the username
     *
     * @param username The cached username for Main Menu
     */
    public void setUsername(String username) {
        this.username = username;
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
        List<UserRole> userRoles = RoleService.getRoles();
        if (Objects.nonNull(userRoles)) {
            userRoles.forEach(result -> {
                gridPane.addRow(gridPane.getRowCount(), createUserLabel(result.getUsername(), index.get()), createRoleComboBox(result.getRole().toString(), index.get()), createDeleteButton(index.get()));
                index.getAndIncrement();
            });
        }
        return gridPane;
    }

    /**
     * Creates a Label with the username
     *
     * @param user  The username placed in the label
     * @param index The index value used for the label's ID
     * @return The label with the username and ID with the index
     */
    private Node createUserLabel(final String user, final int index) {
        Label userLabel = new Label();
        userLabel.setMinWidth(100);
        userLabel.setId("gridLabel" + index);
        userLabel.setText(user);
        return userLabel;
    }

    /**
     * Creates the drop down box to select and change a user's role
     *
     * @param role  The role for the user, possible values defined in {@link UserRoles}.
     * @param index The index used to create the ID used to fetch its linked username label's value.
     * @return The drop down box with the user's current role as the default value and the options available.
     */
    private Node createRoleComboBox(final String role, final int index) {
        ComboBox<String> box = new ComboBox<>(RoleService.getAllRoles());
        box.setId("gridBox" + index);
        box.getSelectionModel().select(UserRoles.valueOf(role).ordinal());
        box.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    String userLabelName = ((Label) values.lookup("#gridLabel" + index)).getText();
                    RoleService.changeRole(userLabelName, newValue);
                }
            }
        });
        return box;
    }

    private Node createDeleteButton(final int index) {
        Button deleteButton = new Button();
        deleteButton.setText("DELETE");
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Label userToDelete = ((Label) values.lookup("#gridLabel" + index));
                ComboBox comboBoxToDelete = ((ComboBox) values.lookup("#gridBox" + index));
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete user " + userToDelete.getText() + "?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    RegistrationService.deleteUser(userToDelete.getText());
                    userToDelete.setTextFill(Color.LIGHTGRAY);
                    comboBoxToDelete.setDisable(true);
                    deleteButton.setText("This user has been deleted");
                    deleteButton.setDisable(true);
                }
            }
        });
        return deleteButton;
    }
}
