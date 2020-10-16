package controller;

import constants.UserRoles;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.DatabaseService;
import service.RoleService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewUserRoleController {


    private String username;
    private String parentUser;
    @FXML
    private TextField newUserField;
    @FXML
    private ComboBox<String> roles;

    @FXML
    public void initialize() {
        parentUser = LoginInfoController.getUserParent();
        roles.getItems().addAll(RoleService.getAllRoles());
        roles.getSelectionModel().select(UserRoles.STRANGER.ordinal());
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
     * Function to go back to the UserRole
     *
     * @param event The event that triggered this method
     * @throws IOException Thrown if the method is unable to locate the view resource
     */
    public void goToUserRole(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/userRoles.fxml"));
        Parent login = loader.load();
        Scene loginScene = new Scene(login);

        UserRolesController controller = loader.getController();
        controller.setUsername(username);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /**
     * Function to go create a userRole
     *
     * @param event The event that triggered this method
     * @throws IOException Thrown if the method is unable to locate the view resource
     */
    public void createUserRole(ActionEvent event) throws SQLException, IOException {
        String newUsername = newUserField.getText();
        AtomicBoolean existence = new AtomicBoolean(false);
        DatabaseService.getAllUserRoles(parentUser).forEach(r -> {
            if (r.getUsername().equals(newUsername)) {
                existence.set(true);
            }
        });
        if (Objects.nonNull(newUsername) && !existence.get()) {
            DatabaseService.createNewUserRole(parentUser, newUsername, roles.getValue());
            newUserField.setText("");
            roles.getSelectionModel().select(UserRoles.STRANGER.ordinal());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "User has been created");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "User needs a name or already exists");
            alert.showAndWait();
        }
    }
}
