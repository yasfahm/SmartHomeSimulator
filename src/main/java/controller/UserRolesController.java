package controller;

import constants.UserRoles;
import entity.UserRole;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import service.RoleService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRolesController {

    @FXML
    public AnchorPane values;
    private String username;

    @FXML
    public void initialize() {
        values.getChildren().add(processRows());
    }
    public void goToLoginInfo(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/loginInfo.fxml"));
        Parent login = loader.load();
        Scene loginScene = new Scene(login);

        LoginInfoController controller = loader.getController();
        controller.setUser(username);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private GridPane processRows() {
        AtomicInteger index = new AtomicInteger();
        GridPane gridPane = new GridPane();
        List<UserRole> userRoles = RoleService.getRoles();
        if (Objects.nonNull(userRoles)) {
            userRoles.forEach(result -> {
                gridPane.addRow(gridPane.getRowCount(), createUserLabel(result.getUsername(), index.get()), createRoleComboBox(result.getRole().toString(), index.get()));
                index.getAndIncrement();
            });
        }
        return gridPane;
    }

    private Node createUserLabel(final String user, final int index)
    {
        Label userLabel = new Label();
        userLabel.setMinWidth(100);
        userLabel.setId("gridLabel" + index);
        userLabel.setText(user);
        return userLabel;
    }

    private Node createRoleComboBox(final String role, final int index)
    {
        ComboBox<String> box = new ComboBox<>(RoleService.getAllRoles());
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

}
