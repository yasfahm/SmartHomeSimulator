package controller;

import constants.Position;
import constants.UserRoles;
import entity.CommandType;
import entity.Door;
import entity.PermissionType;
import entity.Room;
import entity.Window;
import interfaces.MainController;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import observerPattern.UserLocationObserver;
import org.apache.commons.lang3.StringUtils;
import service.ConsoleService;
import service.HouseLayoutService;
import service.PermissionService;
import service.RoleService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginInfoController implements Initializable, MainController {

    /**
     * declaring variables
     */
    @FXML
    private Canvas houseRender;
    @FXML
    private Label time, date, user, temperature;
    @FXML
    private Hyperlink loc;
    @FXML
    private TextArea console;
    @FXML
    private ComboBox<String> selectedUser;
    @FXML
    private Label userRole;
    @FXML
    private AnchorPane anchorToggle;
    @FXML
    private HBox invisibleContainer, hBoxTemperature;
    @FXML
    private TextField textFieldTemperature;
    @FXML
    private VBox vboxSHCWindows;
    @FXML
    private VBox vboxSHCLights;
    @FXML
    private VBox vboxSHCDoors;
    @FXML
    private VBox vboxSHCRooms;
    @FXML
    private ToggleButton awayModeON;
    @FXML
    private ToggleButton awayModeOFF;
    @FXML
    private ComboBox<String> rooms;
    @FXML
    private Label roomToLight;
    @FXML
    private TextField timeBeforeAlertInput;
    @FXML
    private Label labelRoomName;
    @FXML
    private Label labelLight;
    @FXML
    private Label labelWindow;
    @FXML
    private Label labelDoor;
    @FXML
    private AnchorPane permissionsList;
    @FXML
    private Label labelAwayMode;
    @FXML
	private Button autoModeBt;
  
    private static String userParent;
    private static Map<String, Room> house;
    private static Room[] roomArray;
    private static String username;
    private static boolean awayMode;
    private static BooleanProperty booleanProperty;
    private static boolean autoMode = false;
    private final Text toggleText = new Text();

    private GraphicsContext gc;
    private double xOffset = 0;
    private double yOffset = 0;
    private final int ROOM_SIZE = 90;
    private final int DOOR_SIZE = ROOM_SIZE - 66;
    private static long timeInMillis;
    private static Timeline clock;
    private static boolean firstLaunch = true;
    private static int temperatureInInt = 15;
    private Map<String, int[]> roomPosition = new HashMap<>();
    private Map<String, Date[]> lightsSchedule = new HashMap<>();
    private String timeStr;
    private static String timeBeforeAlert;
    private Map<String, String> userLocation = EditSimulationController.getUserLocations();
    private Map<String, Integer> userPositions = new HashMap<>();

    /**
     * Sets up the logged in user as the active user
     *
     * @param userParent The active user's username
     */
    public void setSelectedUser(String userParent) {
        setupCurrentUser();
        selectedUser.getSelectionModel().select(userParent);
        username = userParent;
    }

    /**
     * Function to set the user's location
     *
     * @param place the String that is the name of the location that will be passed to this function.
     */
    public void setLoc(String place) {
        loc.setText(place);
    }

    /**
     * Function to set the user
     *
     * @param username the username that will be passed to this function.
     */
    public void setUser(String username) {
        user.setText(username);
    }

    /**
     * Function to set the date
     *
     * @param dateTime the time and date that will be passed to this function
     */
    public void setDate(String dateTime) {
        date.setText(dateTime);
    }

    /**
     * Function to setting new time label
     *
     * @param dateTime the time and date that will be passed to this function
     */
    public void setTime(String dateTime) {
        SimpleDateFormat formatFull = new SimpleDateFormat("yyyy - MMMM - dd HH:mm:ss");
        try {
            Date d = formatFull.parse(dateTime);
            timeInMillis = d.getTime();
            String[] arr = dateTime.split(" ");
            timeStr = arr[5];
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function return the text for date label
     */
    public String getDate() {
        return this.date.getText();
    }

    /**
     * Function return the AnchorPane
     */
    public AnchorPane getAnc() {
        return anchorToggle;
    }

    /**
     * Function return the text for time label
     */
    public String getTime() {
        return this.time.getText();
    }

    public static void setUserParent(String userParent) {
        LoginInfoController.userParent = userParent;
    }

    public static String getUserParent() {
        return userParent;
    }

    public static String getUsername() {
        return username;
    }

    public Label getTemperature() {
        return temperature;
    }

    public TextField getTemperatureField() {
        return textFieldTemperature;
    }

    /**
     * Get timeBeforeAlert
     *
     * @return timeBeforeAlert string
     */
    public static String getTimeBeforeAlert(){
        return timeBeforeAlert;
    }

    public TextField getTimeBeforeAlertField() {
        return timeBeforeAlertInput;
    }

    public static void setUsername(String username) {
        LoginInfoController.username = username;
    }

    public static boolean isAwayMode() {
        return awayMode;
    }

    /**
     * Animation controller for the clock
     */
    protected void moveClock() {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        timeInMillis += 1000;
        cal.setTimeInMillis(timeInMillis);
        this.date.setText(formatDate.format(cal.getTime()));
        this.time.setText(formatTime.format(cal.getTime()));
    }

    /**
     * Function from interface Initializable, initialize default actions
     *
     * @param location, resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // any action at the first initialization
        if (firstLaunch) {
            firstLaunch = false;
            ChangeDateTimeController.setParentController(this);
            LightsScheduleController.setParentController(this);
            UserLocationObserver.setParentController(this);
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
            SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
            long sysmillis = System.currentTimeMillis();
            timeInMillis = sysmillis;
            Date d = new Date(sysmillis);
            this.date.setText(formatDate.format(d));
            this.time.setText(formatTime.format(d));
            ConsoleService.initialize();
            consoleLog("System initialized");
        }
        
        console.setText(ConsoleService.getConsole());

        if (Objects.nonNull(house) && rooms != null) {
            rooms.getItems().addAll(house.keySet());
            rooms.getSelectionModel().selectFirst();
        }
      
        awayModeON.setSelected(awayMode);
        awayModeOFF.setSelected(!awayMode);

        // Temperature
        this.temperature.setText(Integer.toString(temperatureInInt));

        // Clock animation
        if (clock != null) clock.getKeyFrames().clear();
        clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    moveClock();
                }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        setupCurrentUser();
        if(autoMode) {
        	autoModeBt.setText("ON");
        	updateAutoLights();
    		updateAutoDoors();
        }

        if (Objects.nonNull(house)) {
            try {
                drawRoomFromCache();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        selectedUser.getSelectionModel().select(username);
        Map userLocs = EditSimulationController.getUserLocations();
        loc.setText(Objects.isNull(userLocs) ? "Outside" : userLocs.get(username).toString());

        // toggleSwitch
        Pane root = getAnc();

        ToggleSwitch toggle = new ToggleSwitch();
        toggle.setId("toggleSwitch");
        toggle.setTranslateX(60);
        toggle.setTranslateY(30);

        toggleText.setId("toggleText");
        toggleText.setFont(Font.font(13));
        toggleText.setFill(Color.WHITE);
        toggleText.setTranslateX(60);
        toggleText.setTranslateY(30);
        toggleText.textProperty().bind(Bindings.when(toggle.switchedOnProperty()).then("ON").otherwise("OFF"));
        
        root.getChildren().addAll(toggle, toggleText);
        
    }

    /**
     * Method that will create the grid placed in the {@link AnchorPane}.
     * Also calls the {@link RoleService} to obtain the Users and Permissions.
     *
     * @return The grid pane used by the display
     */
    private GridPane processRows() {
        AtomicInteger index = new AtomicInteger();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        Map<String, Map<CommandType, PermissionType>> userPerms = UserPermissionsController.getUserPermissions();
        if (StringUtils.isEmpty(userRole.getText())) {
            return null;
        }
        if (Objects.isNull(userPerms)) {
            Map<CommandType, PermissionType> currentPermissions = PermissionService.getDefaultPermissions(UserRoles.valueOf(userRole.getText()));
            Map<String, Map<CommandType, PermissionType>> mapToAdd = new HashMap<>();
            mapToAdd.put(username, currentPermissions);
            UserPermissionsController.setUserPermissions(mapToAdd);
        } else if (Objects.isNull(userPerms.get(username))) {
            Map<CommandType, PermissionType> currentPermissions = PermissionService.getDefaultPermissions(UserRoles.valueOf(userRole.getText()));
            userPerms.put(username, currentPermissions);
            UserPermissionsController.setUserPermissions(userPerms);
        }
        UserPermissionsController.getUserPermissions().get(username).forEach((key, value) -> {
            gridPane.addRow(gridPane.getRowCount(), createUserLabel(key.toString(), key.toString()), createUserLabel("Permission:", ""), createUserLabel(value.toString(), key + "Permission"));
            index.getAndIncrement();
        });
        return gridPane;
    }

    /**
     * Creates a Label with the specified value
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
     * On Click function for the temperature label
     *
     * @param event The event that triggered the onClick method
     */
    public void temperatureOnClick(MouseEvent event) {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, enable to process action.");
    	} else {
	        invisibleContainer.getChildren().add(temperature);
	        textFieldTemperature.setText(temperature.getText());
	        textFieldTemperature.setPrefWidth(20 + (temperature.getText().length() * 5));
	        hBoxTemperature.getChildren().add(0, textFieldTemperature);
	        textFieldTemperature.requestFocus();
	        textFieldTemperature.setOnAction(e -> {  // on enter key
	            changeTemperatureOnEnter();
	        });
    	}
    }

    /**
     * On Enter functionality for the textFieldTemperature
     */
    protected void changeTemperatureOnEnter() {
        if (textFieldTemperature.getText().matches("-?\\d+") && textFieldTemperature.getText().length() != 0) {
            int new_temp = Integer.parseInt(textFieldTemperature.getText());
            invisibleContainer.getChildren().add(textFieldTemperature);
            hBoxTemperature.getChildren().add(0, temperature);
            temperature.setText(textFieldTemperature.getText());
            textFieldTemperature.clear();
            temperatureInInt = Integer.parseInt(temperature.getText());
            consoleLog("Change outside temperature to " + temperatureInInt);
        } else {
            consoleLog("Please enter a valid input for outside temperature.");
        }
    }
    
    /**
	 * Toogle auto mode
	 * 
	 * @param event The event that trigger action
	 */
	public void autoModeOnClick(ActionEvent event) {
		if (!toggleText.getText().equals("ON")) {
			consoleLog("Simulation is off, enable to process action.");
		} else {
			if (!autoMode) {
				autoModeBt.setText("ON");
				this.autoMode = true;
				consoleLog("Auto mode ON");
			} else {
				autoModeBt.setText("OFF");
				this.autoMode = false;
				consoleLog("Auto mode OFF");
			}
		}
	}
	
	/**
	 * Function return autoMode status
	 * @return boolean of autoMode
	 */
	public static boolean getAutoMode() {
		return autoMode;
	}
	
	/**
	 * Function open away mode
	 * 
	 * @param event The event that trigger action
	 */
    public void onMouseClickAwayToggleON(MouseEvent event) {
        if (!toggleText.getText().equals("ON")) {
    		  consoleLog("Simulation is off, enable to process action.");
    	  } else {
            Map<String, String> userLocations = EditSimulationController.getUserLocations();
            AtomicBoolean isNotInHouse = new AtomicBoolean(true);
            if (Objects.nonNull(userLocations)) {
                userLocations.keySet().forEach(user -> {
                    if (!userLocations.get(user).equals("Outside")) {
                        isNotInHouse.set(false);
                    }
                });
            }
            if (isNotInHouse.get()) {
               if(closeWindowsDoorsLights()) {
                    consoleLog("Away mode turns on.");
                    awayMode = true;
                    awayModeON.setSelected(true);
                    labelAwayMode.setTextFill(Color. WHITE);
                    labelAwayMode.setText("Away mode is on");
              }else {
                    awayModeOFF.setSelected(true);
              }
            } else {
                consoleLog("Unable to turn Away Mode ON, there is someone in the house");
                awayMode = false;
                awayModeON.setSelected(false);
                awayModeOFF.setSelected(true);
            }
          }
    }

    /**
	 * Function turn off away mode
	 * 
	 * @param event The event that trigger action
	 */
    public void onMouseClickAwayToggleOFF(MouseEvent event) {
      if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, unable to process action.");
    	} else {
          consoleLog("Away mode was turned off.");
          awayMode = false;
          awayModeOFF.setSelected(true);
          labelAwayMode.setTextFill(Color. WHITE);
          labelAwayMode.setText("Away mode is off");
      }
    }

    /**
     * This class creates a toggle switch.
     */
    protected static class ToggleSwitch extends Parent {

        private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
        private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
        private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

        public BooleanProperty switchedOnProperty() {
            return booleanProperty;
        }

        public ToggleSwitch() {
            Rectangle background = new Rectangle(60, 30);
            background.setArcWidth(30);
            background.setArcHeight(30);
            background.setFill(Color.RED);
            background.setStroke(Color.LIGHTGRAY);

            Circle trigger = new Circle(15);
            trigger.setCenterX(15);
            trigger.setCenterY(15);
            trigger.setFill(Color.WHITE);
            trigger.setStroke(Color.LIGHTGRAY);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(2);
            trigger.setEffect(shadow);

            translateAnimation.setNode(trigger);
            fillAnimation.setShape(background);

            getChildren().addAll(background, trigger);

            if (Objects.isNull(booleanProperty)) {
                booleanProperty = new SimpleBooleanProperty(false);
            } else {
                boolean check = booleanProperty.get();
                translateAnimation.setToX(check ? 60 - 30 : 0);
                fillAnimation.setFromValue(check ? Color.RED : Color.GREEN);
                fillAnimation.setToValue(check ? Color.GREEN : Color.RED);
                animation.play();
            }

            booleanProperty.addListener((obs, oldState, newState) -> {
                boolean isOn = newState.booleanValue();
                translateAnimation.setToX(isOn ? 60 - 30 : 0);
                fillAnimation.setFromValue(isOn ? Color.RED : Color.GREEN);
                fillAnimation.setToValue(isOn ? Color.GREEN : Color.RED);

                animation.play();
            });

            setOnMouseClicked(event -> {
                booleanProperty.set(!booleanProperty.get());
            });
        }
    }


    /**
     * Sets up the current active user and all possible options
     */
    private void setupCurrentUser() {
        selectedUser.getItems().clear();
        Map<String, String> listOfUsers = RoleService.findRole(userParent);
        userRole.setText(listOfUsers.get(username));
        selectedUser.getItems().addAll(listOfUsers.keySet());
        selectedUser.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    permissionsList.getChildren().clear();
                    username = newValue;
                    selectedUser.getSelectionModel().select(newValue);
                    userRole.setText(listOfUsers.get(newValue));
                    Map<String, String> userLocs = EditSimulationController.getUserLocations();
                    loc.setText(Objects.isNull(userLocs) ? "Outside" : userLocs.get(username).toString());
                    if (StringUtils.isNotEmpty(username)) {
                        Node toAdd = processRows();
                        if (Objects.nonNull(toAdd)) {
                            permissionsList.getChildren().add(toAdd);
                        }
                    }
                }
            }
        });
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

        deleteHouse();
        EditSimulationController.deleteLocations();

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /**
     * The function appends text onto the consoleLog when it is not on the scene
     *
     * @param str String to append onto the console
     */
    public static void consoleLogFile(String str) {
        updateConsoleLog(str);
    }

    /**
     * This function appends text onto the console
     *
     * @param str String to append onto the console
     */
    public void consoleLog(String str) {
        updateConsoleLog(str);
        console.appendText("[" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString() + "] " + str + "\n");
    }

    /**
     * This function is responsible for updating the cached logs
     *
     * @param str String to append onto the console
     */
    private static void updateConsoleLog(String str) {
        String toAppend = "[" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString() + "] " + str + "\n";
        ConsoleService.exportConsole(toAppend);
    }

    /**
     * This function loads the user roles page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToUserSettings(ActionEvent event) throws IOException {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, unable to process action.");
    	} else {
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
    }
    
    /**
     * Function setter for room array
     * @param file File of room list
     * @throws IOException IOException when access File
     */
    public void setRoomArray(File file) throws IOException {
    	if(file==null) roomArray=null;
    	else roomArray = HouseLayoutService.parseHouseLayout(file);
    }
    
    /**
     * Function getter for room array
     * @return
     */
    public Room[] getRoomArray() {
    	return roomArray;
    }

    /**
     * This function allows the user to add a house layout text file, calls the function to parse the json
     * and calls the function to draw the house with rooms, doors, windows and lights
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void addHouseLayout(ActionEvent event) throws IOException {
        if (toggleText.getText().equals("ON")) {
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(window);
            if (Objects.isNull(file)) {
                return;
            }

            labelRoomName.setText("ROOM");
            labelLight.setText("LIGHT");
            labelWindow.setText("WINDOW");
            labelDoor.setText("DOOR");

            //show the away mode status on house layout
            if (awayMode) {
                labelAwayMode.setTextFill(Color. WHITE);
                labelAwayMode.setText("Away mode is on");
            }
            else {
                labelAwayMode.setTextFill(Color. WHITE);
                labelAwayMode.setText("Away mode is off");
            }

            roomArray = HouseLayoutService.parseHouseLayout(file);
            HashMap<String, Room> rooms = new HashMap<>();
            for (Room room : roomArray) {
                rooms.put(room.getName(), room);
            }

            Set<Room> traversed = new HashSet<>();

            gc = houseRender.getGraphicsContext2D();
            gc.setFont(new Font(11));
            gc.setFill(Color.WHITE);
            int lastX = 130, lastY = 190;
            house = rooms;
            drawRoom(rooms, roomArray[0], traversed, Position.NONE, lastX, lastY);

            // creating a room label which has the name of the room.
            GridPane gpSHCRooms = new GridPane();
            gpSHCRooms.setVgap(21.3);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Label room = new Label();
                room.setText(roomArray[i].getName());
                gpSHCRooms.addRow(i, room);
            }
            vboxSHCRooms.getChildren().clear();

            vboxSHCRooms.getChildren().add(gpSHCRooms);

            GridPane gpSHCLights = new GridPane();
            gpSHCLights.setVgap(13);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Image lightOn = new Image(new FileInputStream("src/main/resources/Images/lightOn.png"), 60, 27, true, false);
                Image lightOff = new Image(new FileInputStream("src/main/resources/Images/lightOff.png"), 60, 27, true, false);
                ImageView light = new ImageView(lightOff);
                int finalI = i;

                if (roomArray[finalI].getLightsOn() == 0)
                    light.setImage(lightOff);
                else {
                    light.setImage(lightOn);
                }

                light.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (roomArray[finalI].getLightsOn() == 0) {
                            roomArray[finalI].setLightsOn(1);
                            drawLight(roomArray[finalI]);
                            light.setImage(lightOn);
                        }
                        else {
                            roomArray[finalI].setLightsOn(0);
                            drawLight(roomArray[finalI]);
                            light.setImage(lightOff);
                        }
                    }
                });
                gpSHCLights.addRow(i, light);
            }
            vboxSHCLights.getChildren().clear();

            vboxSHCLights.getChildren().add(gpSHCLights);

            // open/close window functionality
            GridPane gpSHCWindows = new GridPane();
            gpSHCWindows.setVgap(13);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Image windowCloseTop = new Image(new FileInputStream("src/main/resources/Images/windowCloseTop.png"), 60, 27, true, false);
                Image windowCloseBottom = new Image(new FileInputStream("src/main/resources/Images/windowCloseBottom.png"), 60, 27, true, false);
                Image windowCloseLeft = new Image(new FileInputStream("src/main/resources/Images/windowCloseLeft.png"), 60, 27, true, false);
                Image windowCloseRight = new Image(new FileInputStream("src/main/resources/Images/windowCloseRight.png"), 60, 27, true, false);
                Image windowOpenTop = new Image(new FileInputStream("src/main/resources/Images/windowOpenTop.png"), 60, 27, true, false);
                Image windowOpenBottom = new Image(new FileInputStream("src/main/resources/Images/windowOpenBottom.png"), 60, 27, true, false);
                Image windowOpenLeft = new Image(new FileInputStream("src/main/resources/Images/windowOpenLeft.png"), 60, 27, true, false);
                Image windowOpenRight = new Image(new FileInputStream("src/main/resources/Images/windowOpenRight.png"), 60, 27, true, false);
                Image windowEmpty = new Image(new FileInputStream("src/main/resources/Images/windowEmpty.png"), 60, 27, true, false);

                ImageView windowsTop = new ImageView(windowCloseTop);
                ImageView windowsLeft = new ImageView(windowCloseLeft);
                ImageView windowsRight = new ImageView(windowCloseRight);
                ImageView windowsBottom = new ImageView(windowCloseBottom);
                ImageView windowsEmpty = new ImageView(windowEmpty);

                int finalI = i;
                ArrayList<Window> windowList = roomArray[i].getWindows();

                for (int j = 0; j < windowList.size(); j++) {
                    int finalJ = j;

                    if (windowList.get(finalJ).getPosition().toString() == "TOP" && !windowList.get(finalJ).getBlocking()) {
                        if (!windowList.get(finalJ).getOpenWindow()) {
                            windowsTop.setImage(windowCloseTop);
                        }
                        else {
                            windowsTop.setImage(windowOpenTop);
                        }
                        windowsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "TOP" && !windowList.get(finalJ).getBlocking()) {
                                    if (!windowList.get(finalJ).getOpenWindow()) {
                                        windowList.get(finalJ).setOpenWindow(true);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsTop.setImage(windowOpenTop);
                                    } else {
                                        windowList.get(finalJ).setOpenWindow(false);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsTop.setImage(windowCloseTop);
                                    }
                                }
                                else {
                                	consoleLog("This window is blocked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                    else {
                        drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "LEFT" && !windowList.get(finalJ).getBlocking()) {
                        if (!windowList.get(finalJ).getOpenWindow()) {
                            windowsLeft.setImage(windowCloseLeft);
                        }
                        else {
                            windowsLeft.setImage(windowOpenLeft);
                        }
                        windowsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "LEFT" && !windowList.get(finalJ).getBlocking()) {
                                    if (!windowList.get(finalJ).getOpenWindow()) {
                                        windowList.get(finalJ).setOpenWindow(true);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsLeft.setImage(windowOpenLeft);
                                    } else {
                                        windowList.get(finalJ).setOpenWindow(false);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsLeft.setImage(windowCloseLeft);
                                    }
                                }
                                else {
                                	consoleLog("This window is blocked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                    else {
                        drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "RIGHT" && !windowList.get(finalJ).getBlocking()) {
                        if (!windowList.get(finalJ).getOpenWindow()) {
                            windowsRight.setImage(windowCloseRight);
                        }
                        else {
                        	windowsRight.setImage(windowOpenRight);
                        }
                        windowsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "RIGHT" && !windowList.get(finalJ).getBlocking()) {
                                    if (!windowList.get(finalJ).getOpenWindow()) {
                                        windowList.get(finalJ).setOpenWindow(true);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsRight.setImage(windowOpenRight);
                                    } else {
                                        windowList.get(finalJ).setOpenWindow(false);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsRight.setImage(windowCloseRight);
                                    }
                                }
                                else {
                                	consoleLog("This window is blocked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                    else {
                        drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "BOTTOM" && !windowList.get(finalJ).getBlocking()) {
                        if (!windowList.get(finalJ).getOpenWindow()) {
                            windowsBottom.setImage(windowCloseBottom);
                        }
                        else {
                            windowsBottom.setImage(windowOpenBottom);
                        }
                        windowsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "BOTTOM" && !windowList.get(finalJ).getBlocking()) {
                                    if (!windowList.get(finalJ).getOpenWindow()) {
                                        windowList.get(finalJ).setOpenWindow(true);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsBottom.setImage(windowOpenBottom);
                                    } else {
                                        windowList.get(finalJ).setOpenWindow(false);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsBottom.setImage(windowCloseBottom);
                                    }
                                }
                                else {
                                	consoleLog("This window is blocked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                    else {
                        drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
                    }
                }

                // when room has 0 windows
                if(windowList.size() == 0) {
                    gpSHCWindows.addRow(i, windowsEmpty);
                }

                // when room has 1 window
                if(windowList.size() == 1) {
                    ImageView selectedWindow = new ImageView();

                    if(roomArray[i].getWindows().get(0).getPosition().toString().equals("TOP"))
                        selectedWindow = windowsTop;
                    if(roomArray[i].getWindows().get(0).getPosition().toString().equals("BOTTOM"))
                        selectedWindow = windowsBottom;
                    if(roomArray[i].getWindows().get(0).getPosition().toString().equals("LEFT"))
                        selectedWindow = windowsLeft;
                    if(roomArray[i].getWindows().get(0).getPosition().toString().equals("RIGHT"))
                        selectedWindow = windowsRight;
                    gpSHCWindows.addRow(i, selectedWindow);
                }

                // when room has 2 windows
                if(windowList.size() == 2) {
                    ImageView[] selectedWindows = new ImageView[2];

                    for(int j = 0; j < selectedWindows.length; j++) {
                        if (roomArray[i].getWindows().get(j).getPosition().toString().equals("TOP"))
                            selectedWindows[j] = windowsTop;
                        if (roomArray[i].getWindows().get(j).getPosition().toString().equals("BOTTOM"))
                            selectedWindows[j] = windowsBottom;
                        if (roomArray[i].getWindows().get(j).getPosition().toString().equals("LEFT"))
                            selectedWindows[j] = windowsLeft;
                        if (roomArray[i].getWindows().get(j).getPosition().toString().equals("RIGHT"))
                            selectedWindows[j] = windowsRight;
                    }
                    gpSHCWindows.addRow(i, selectedWindows[0], selectedWindows[1]);
                }

                // when room has 3 windows
                if(windowList.size() == 3) {
                    ImageView[] selectedWindows = new ImageView[3];

                    for(int j = 0; j < selectedWindows.length; j++) {
                        if (roomArray[i].getWindows().get(j).getPosition().toString().equals("TOP"))
                            selectedWindows[j] = windowsTop;
                        if (roomArray[i].getWindows().get(j).getPosition().toString().equals("BOTTOM"))
                            selectedWindows[j] = windowsBottom;
                        if (roomArray[i].getWindows().get(j).getPosition().toString().equals("LEFT"))
                            selectedWindows[j] = windowsLeft;
                        if (roomArray[i].getWindows().get(j).getPosition().toString().equals("RIGHT"))
                            selectedWindows[j] = windowsRight;
                    }
                    gpSHCWindows.addRow(i, selectedWindows[0], selectedWindows[1], selectedWindows[2]);
                }

                // when room has 4 windows
                if(windowList.size() == 4) {
                    gpSHCWindows.addRow(i, windowsTop, windowsBottom, windowsLeft, windowsRight);
                }
            }

            vboxSHCWindows.getChildren().clear();

            vboxSHCWindows.getChildren().add(gpSHCWindows);

            // open/close door functionality
            GridPane gpSHCDoors = new GridPane();
            gpSHCDoors.setVgap(13);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Image doorCloseTop = new Image(new FileInputStream("src/main/resources/Images/DoorCloseTop.png"), 60, 27, true, false);
                Image doorCloseBottom = new Image(new FileInputStream("src/main/resources/Images/DoorCloseBottom.png"), 60, 27, true, false);
                Image doorCloseLeft = new Image(new FileInputStream("src/main/resources/Images/DoorCloseLeft.png"), 60, 27, true, false);
                Image doorCloseRight = new Image(new FileInputStream("src/main/resources/Images/DoorCloseRight.png"), 60, 27, true, false);
                Image doorOpenTop = new Image(new FileInputStream("src/main/resources/Images/doorOpenTop.png"), 60, 27, true, false);
                Image doorOpenBottom = new Image(new FileInputStream("src/main/resources/Images/doorOpenBottom.png"), 60, 27, true, false);
                Image doorOpenLeft = new Image(new FileInputStream("src/main/resources/Images/doorOpenLeft.png"), 60, 27, true, false);
                Image doorOpenRight = new Image(new FileInputStream("src/main/resources/Images/doorOpenRight.png"), 60, 27, true, false);

                ImageView doorsTop = new ImageView(doorCloseTop);
                ImageView doorsLeft = new ImageView(doorCloseLeft);
                ImageView doorsRight = new ImageView(doorCloseRight);
                ImageView doorsBottom = new ImageView(doorCloseBottom);

                int finalI = i;
                ArrayList<Door> doorList = roomArray[i].getDoors();

                for (int j = 0; j < doorList.size(); j++) {
                    int finalJ = j;

                    if (doorList.get(finalJ).getPosition().toString() == "TOP") {
                        String connectedRoom = doorList.get(finalJ).getConnection();
                        if (!doorList.get(finalJ).getOpenDoor())
                            doorsTop.setImage(doorCloseTop);
                        else {
                            doorsTop.setImage(doorOpenTop);
                        }

                        doorsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (doorList.get(finalJ).getPosition().toString() == "TOP") {
                                    if (!doorList.get(finalJ).getOpenDoor()) {
                                        doorList.get(finalJ).setOpenDoor(true);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(true);
                                                }
                                            }
                                        }

                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsTop.setImage(doorOpenTop);
                                    } else {
                                        doorList.get(finalJ).setOpenDoor(false);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(false);
                                                }
                                            }
                                        }

                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsTop.setImage(doorCloseTop);
                                    }
                                }
                            }
                        });
                    }

                    if (doorList.get(finalJ).getPosition().toString() == "LEFT") {
                        String connectedRoom = doorList.get(finalJ).getConnection();
                        if (!doorList.get(finalJ).getOpenDoor())
                            doorsLeft.setImage(doorCloseLeft);
                        else {
                            doorsLeft.setImage(doorOpenLeft);
                        }

                        doorsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (doorList.get(finalJ).getPosition().toString() == "LEFT") {
                                    if (!doorList.get(finalJ).getOpenDoor()) {
                                        doorList.get(finalJ).setOpenDoor(true);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(true);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsLeft.setImage(doorOpenLeft);
                                    } else {
                                        doorList.get(finalJ).setOpenDoor(false);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(false);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsLeft.setImage(doorCloseLeft);
                                    }
                                }
                            }
                        });
                    }

                    if (doorList.get(finalJ).getPosition().toString() == "RIGHT") {
                        String connectedRoom = doorList.get(finalJ).getConnection();
                        if (!doorList.get(finalJ).getOpenDoor())
                            doorsRight.setImage(doorCloseRight);
                        else {
                            doorsRight.setImage(doorOpenRight);
                        }

                        doorsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (doorList.get(finalJ).getPosition().toString() == "RIGHT") {
                                    if (!doorList.get(finalJ).getOpenDoor()) {
                                        doorList.get(finalJ).setOpenDoor(true);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(true);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsRight.setImage(doorOpenRight);
                                    } else {
                                        doorList.get(finalJ).setOpenDoor(false);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(false);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsRight.setImage(doorCloseRight);
                                    }
                                }
                            }
                        });
                    }

                    if (doorList.get(finalJ).getPosition().toString() == "BOTTOM") {
                        String connectedRoom = doorList.get(finalJ).getConnection();
                        if (!doorList.get(finalJ).getOpenDoor())
                            doorsBottom.setImage(doorCloseBottom);
                        else {
                            doorsBottom.setImage(doorOpenBottom);
                        }

                        doorsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (doorList.get(finalJ).getPosition().toString() == "BOTTOM") {
                                    if (!doorList.get(finalJ).getOpenDoor()) {
                                        doorList.get(finalJ).setOpenDoor(true);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(true);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsBottom.setImage(doorOpenBottom);
                                    } else {
                                        doorList.get(finalJ).setOpenDoor(false);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(false);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsBottom.setImage(doorCloseBottom);
                                    }
                                }
                            }
                        });
                    }
                }

                // when room has 1 door
                if(doorList.size() == 1) {
                    ImageView selectedDoors = new ImageView();

                    if(roomArray[i].getDoors().get(0).getPosition().toString().equals("TOP"))
                        selectedDoors = doorsTop;
                    if(roomArray[i].getDoors().get(0).getPosition().toString().equals("BOTTOM"))
                        selectedDoors = doorsBottom;
                    if(roomArray[i].getDoors().get(0).getPosition().toString().equals("LEFT"))
                        selectedDoors = doorsLeft;
                    if(roomArray[i].getDoors().get(0).getPosition().toString().equals("RIGHT"))
                        selectedDoors = doorsRight;
                    gpSHCDoors.addRow(i, selectedDoors);
                }

                // when room has 2 doors
                if(doorList.size() == 2) {
                    ImageView[] selectedDoors = new ImageView[2];

                    for(int j = 0; j < selectedDoors.length; j++) {
                        if (roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                            selectedDoors[j] = doorsTop;
                        if (roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                            selectedDoors[j] = doorsBottom;
                        if (roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                            selectedDoors[j] = doorsLeft;
                        if (roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                            selectedDoors[j] = doorsRight;
                    }
                    gpSHCDoors.addRow(i, selectedDoors[0], selectedDoors[1]);
                }

                // when room has 3 doors
                if(doorList.size() == 3) {
                    ImageView[] selectedDoors = new ImageView[3];

                    for(int j = 0; j < selectedDoors.length; j++) {
                        if (roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                            selectedDoors[j] = doorsTop;
                        if (roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                            selectedDoors[j] = doorsBottom;
                        if (roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                            selectedDoors[j] = doorsLeft;
                        if (roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                            selectedDoors[j] = doorsRight;
                    }
                    gpSHCDoors.addRow(i, selectedDoors[0], selectedDoors[1], selectedDoors[2]);
                }

                // when room has 4 doors
                if(doorList.size() == 4) {
                    gpSHCDoors.addRow(i, doorsTop, doorsBottom, doorsLeft, doorsRight);
                }
            }

            vboxSHCDoors.getChildren().clear();

            vboxSHCDoors.getChildren().add(gpSHCDoors);
            
            consoleLog("Successfully added house layout.");

        } else {
        	consoleLog("Add house layout failed, please turn on the simulation first.");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please turn on the simulation first.");
            alert.showAndWait();
        }
    }

    /**
     * Draws the room based on the cached static variables after this file has been submitted
     */
    public void drawRoomFromCache() throws FileNotFoundException {
        labelRoomName.setText("ROOM");
        labelLight.setText("LIGHT");
        labelWindow.setText("WINDOW");
        labelDoor.setText("DOOR");

        //show the away mode status on house layout
        if (awayMode) {
            labelAwayMode.setTextFill(Color. WHITE);
            labelAwayMode.setText("Away mode is on");
        }
        else {
            labelAwayMode.setTextFill(Color. WHITE);
            labelAwayMode.setText("Away mode is off");
        }

        gc = houseRender.getGraphicsContext2D();
        gc.setFont(new Font(11));
        gc.setFill(Color.WHITE);
        drawRoom(house, roomArray[0], new HashSet<>(), Position.NONE, 130, 190);

        // creating a room label which has the name of the room.
        GridPane gpSHCRooms = new GridPane();
        gpSHCRooms.setVgap(21.3);
        for (int i = 0 ; i < roomArray.length ; i++) {
            Label room = new Label();
            room.setText(roomArray[i].getName());
            gpSHCRooms.addRow(i, room);
        }

        vboxSHCRooms.getChildren().clear();

        vboxSHCRooms.getChildren().add(gpSHCRooms);

        GridPane gpSHCLights = new GridPane();
        gpSHCLights.setVgap(13);

        for (int i = 0 ; i < roomArray.length ; i++) {

            Image lightOn = new Image(new FileInputStream("src/main/resources/Images/lightOn.png"), 60, 27, true, false);
            Image lightOff = new Image(new FileInputStream("src/main/resources/Images/lightOff.png"), 60, 27, true, false);
            ImageView light = new ImageView(lightOff);
            int finalI = i;
            if (roomArray[finalI].getLightsOn() == 0)
                light.setImage(lightOff);
            else {
                light.setImage(lightOn);
            }
            light.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    if (roomArray[finalI].getLightsOn() == 0) {
                        roomArray[finalI].setLightsOn(1);
                        drawLight(roomArray[finalI]);
                        light.setImage(lightOn);
                    }
                    else {
                        roomArray[finalI].setLightsOn(0);
                        drawLight(roomArray[finalI]);
                        light.setImage(lightOff);
                    }
                }
            });
            gpSHCLights.addRow(i, light);
        }

        vboxSHCLights.getChildren().clear();

        vboxSHCLights.getChildren().add(gpSHCLights);

        // open/close window functionality
        GridPane gpSHCWindows = new GridPane();
        gpSHCWindows.setVgap(13);

        for (int i = 0 ; i < roomArray.length ; i++) {
            Image windowCloseTop = new Image(new FileInputStream("src/main/resources/Images/windowCloseTop.png"), 60, 27, true, false);
            Image windowCloseBottom = new Image(new FileInputStream("src/main/resources/Images/windowCloseBottom.png"), 60, 27, true, false);
            Image windowCloseLeft = new Image(new FileInputStream("src/main/resources/Images/windowCloseLeft.png"), 60, 27, true, false);
            Image windowCloseRight = new Image(new FileInputStream("src/main/resources/Images/windowCloseRight.png"), 60, 27, true, false);
            Image windowEmpty = new Image(new FileInputStream("src/main/resources/Images/windowEmpty.png"), 60, 27, true, false);
            Image windowOpenTop = new Image(new FileInputStream("src/main/resources/Images/windowOpenTop.png"), 60, 27, true, false);
            Image windowOpenBottom = new Image(new FileInputStream("src/main/resources/Images/windowOpenBottom.png"), 60, 27, true, false);
            Image windowOpenLeft = new Image(new FileInputStream("src/main/resources/Images/windowOpenLeft.png"), 60, 27, true, false);
            Image windowOpenRight = new Image(new FileInputStream("src/main/resources/Images/windowOpenRight.png"), 60, 27, true, false);

            ImageView windowsTop = new ImageView(windowCloseTop);
            ImageView windowsLeft = new ImageView(windowCloseLeft);
            ImageView windowsRight = new ImageView(windowCloseRight);
            ImageView windowsBottom = new ImageView(windowCloseBottom);
            ImageView windowsEmpty = new ImageView(windowEmpty);

            int finalI = i;
            ArrayList<Window> windowList = roomArray[i].getWindows();

            for (int j = 0; j < windowList.size(); j++) {
                int finalJ = j;

                if (windowList.get(finalJ).getPosition().toString() == "TOP") {
                    if (!windowList.get(finalJ).getOpenWindow())
                        windowsTop.setImage(windowCloseTop);
                    else {
                        windowsTop.setImage(windowOpenTop);
                    }
                    windowsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (windowList.get(finalJ).getPosition().toString() == "TOP" && !windowList.get(finalJ).getBlocking()) {
                                if (!windowList.get(finalJ).getOpenWindow()) {
                                    windowList.get(finalJ).setOpenWindow(true);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsTop.setImage(windowOpenTop);
                                } else {
                                    windowList.get(finalJ).setOpenWindow(false);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsTop.setImage(windowCloseTop);
                                }

                            } else {
                            	consoleLog("This window is blocked");
                                Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                alert.showAndWait();
                            }
                        }
                    });
                    if(windowList.get(finalJ).getBlocking()) {
						drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
					}
                }

                if (windowList.get(finalJ).getPosition().toString() == "LEFT") {
                    if (!windowList.get(finalJ).getOpenWindow())
                        windowsLeft.setImage(windowCloseLeft);
                    else {
                        windowsLeft.setImage(windowOpenLeft);
                    }
                    windowsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (windowList.get(finalJ).getPosition().toString() == "LEFT" && !windowList.get(finalJ).getBlocking()) {
                                if (!windowList.get(finalJ).getOpenWindow()) {
                                    windowList.get(finalJ).setOpenWindow(true);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsLeft.setImage(windowOpenLeft);
                                } else {
                                    windowList.get(finalJ).setOpenWindow(false);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsLeft.setImage(windowCloseLeft);
                                }
								
                            } else {
                            	consoleLog("This window is blocked");
                                Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                alert.showAndWait();
                            }
                        }
                    });
                    if(windowList.get(finalJ).getBlocking()) {
						drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
					}
                }

                if (windowList.get(finalJ).getPosition().toString() == "RIGHT") {
                    if (!windowList.get(finalJ).getOpenWindow())
                    	windowsRight.setImage(windowCloseRight);
                    else {
                    	windowsRight.setImage(windowOpenRight);
                    }
                    windowsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (windowList.get(finalJ).getPosition().toString() == "RIGHT" && !windowList.get(finalJ).getBlocking()) {
                                if (!windowList.get(finalJ).getOpenWindow()) {
                                    windowList.get(finalJ).setOpenWindow(true);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsRight.setImage(windowOpenRight);
                                } else {
                                    windowList.get(finalJ).setOpenWindow(false);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsRight.setImage(windowCloseRight);
                                }
								
                            } else {
                            	consoleLog("This window is blocked");
                                Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                alert.showAndWait();
                            }
                        }
                    });
                    if(windowList.get(finalJ).getBlocking()) {
						drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
					}
                }

                if (windowList.get(finalJ).getPosition().toString() == "BOTTOM") {
                    if (!windowList.get(finalJ).getOpenWindow())
                        windowsBottom.setImage(windowCloseBottom);
                    else {
                        windowsBottom.setImage(windowOpenBottom);
                    }
                    windowsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (windowList.get(finalJ).getPosition().toString() == "BOTTOM" && !windowList.get(finalJ).getBlocking()) {
                            	if (!windowList.get(finalJ).getOpenWindow()) {
                                    windowList.get(finalJ).setOpenWindow(true);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsBottom.setImage(windowOpenBottom);
                                } else {
                                    windowList.get(finalJ).setOpenWindow(false);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsBottom.setImage(windowCloseBottom);
                                }
								
                            } else {
                            	consoleLog("This window is blocked");
                                Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                alert.showAndWait();
                            }
                        }
                    });
                    if(windowList.get(finalJ).getBlocking()) {
						drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
					}
                }
            }

            // when room has 0 windows
            if(windowList.size() == 0) {
                gpSHCWindows.addRow(i, windowsEmpty);
            }

            // when room has 1 window
            if(windowList.size() == 1) {
                ImageView selectedWindow = new ImageView();

                if(roomArray[i].getWindows().get(0).getPosition().toString().equals("TOP"))
                    selectedWindow = windowsTop;
                if(roomArray[i].getWindows().get(0).getPosition().toString().equals("BOTTOM"))
                    selectedWindow = windowsBottom;
                if(roomArray[i].getWindows().get(0).getPosition().toString().equals("LEFT"))
                    selectedWindow = windowsLeft;
                if(roomArray[i].getWindows().get(0).getPosition().toString().equals("RIGHT"))
                    selectedWindow = windowsRight;
                gpSHCWindows.addRow(i, selectedWindow);
            }

            // when room has 2 windows
            if(windowList.size() == 2) {
                ImageView[] selectedWindows = new ImageView[2];

                for(int j = 0; j < selectedWindows.length; j++) {
                    if (roomArray[i].getWindows().get(j).getPosition().toString().equals("TOP"))
                        selectedWindows[j] = windowsTop;
                    if (roomArray[i].getWindows().get(j).getPosition().toString().equals("BOTTOM"))
                        selectedWindows[j] = windowsBottom;
                    if (roomArray[i].getWindows().get(j).getPosition().toString().equals("LEFT"))
                        selectedWindows[j] = windowsLeft;
                    if (roomArray[i].getWindows().get(j).getPosition().toString().equals("RIGHT"))
                        selectedWindows[j] = windowsRight;
                }
                gpSHCWindows.addRow(i, selectedWindows[0], selectedWindows[1]);
            }

            // when room has 3 windows
            if(windowList.size() == 3) {
                ImageView[] selectedWindows = new ImageView[3];

                for(int j = 0; j < selectedWindows.length; j++) {
                    if (roomArray[i].getWindows().get(j).getPosition().toString().equals("TOP"))
                        selectedWindows[j] = windowsTop;
                    if (roomArray[i].getWindows().get(j).getPosition().toString().equals("BOTTOM"))
                        selectedWindows[j] = windowsBottom;
                    if (roomArray[i].getWindows().get(j).getPosition().toString().equals("LEFT"))
                        selectedWindows[j] = windowsLeft;
                    if (roomArray[i].getWindows().get(j).getPosition().toString().equals("RIGHT"))
                        selectedWindows[j] = windowsRight;
                }
                gpSHCWindows.addRow(i, selectedWindows[0], selectedWindows[1], selectedWindows[2]);
            }

            // when room has 4 windows
            if(windowList.size() == 4) {
                gpSHCWindows.addRow(i, windowsTop, windowsBottom, windowsLeft, windowsRight);
            }
        }

        vboxSHCWindows.getChildren().clear();

        vboxSHCWindows.getChildren().add(gpSHCWindows);

        // open/close door functionality
        GridPane gpSHCDoors = new GridPane();
        gpSHCDoors.setVgap(13);

        for (int i = 0 ; i < roomArray.length ; i++) {
            Image doorCloseTop = new Image(new FileInputStream("src/main/resources/Images/DoorCloseTop.png"), 60, 27, true, false);
            Image doorCloseBottom = new Image(new FileInputStream("src/main/resources/Images/DoorCloseBottom.png"), 60, 27, true, false);
            Image doorCloseLeft = new Image(new FileInputStream("src/main/resources/Images/DoorCloseLeft.png"), 60, 27, true, false);
            Image doorCloseRight = new Image(new FileInputStream("src/main/resources/Images/DoorCloseRight.png"), 60, 27, true, false);
            Image doorOpenTop = new Image(new FileInputStream("src/main/resources/Images/doorOpenTop.png"), 60, 27, true, false);
            Image doorOpenBottom = new Image(new FileInputStream("src/main/resources/Images/doorOpenBottom.png"), 60, 27, true, false);
            Image doorOpenLeft = new Image(new FileInputStream("src/main/resources/Images/doorOpenLeft.png"), 60, 27, true, false);
            Image doorOpenRight = new Image(new FileInputStream("src/main/resources/Images/doorOpenRight.png"), 60, 27, true, false);

            ImageView doorsTop = new ImageView(doorCloseTop);
            ImageView doorsLeft = new ImageView(doorCloseLeft);
            ImageView doorsRight = new ImageView(doorCloseRight);
            ImageView doorsBottom = new ImageView(doorCloseBottom);

            int finalI = i;
            ArrayList<Door> doorList = roomArray[i].getDoors();

            for (int j = 0; j < doorList.size(); j++) {
                int finalJ = j;

                if (doorList.get(finalJ).getPosition().toString() == "TOP") {
                    String connectedRoom = doorList.get(finalJ).getConnection();
                    if (!doorList.get(finalJ).getOpenDoor())
                        doorsTop.setImage(doorCloseTop);
                    else {
                        doorsTop.setImage(doorOpenTop);
                    }

                    doorsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (doorList.get(finalJ).getPosition().toString() == "TOP" && !doorList.get(finalJ).getLock()) {
                                if (!doorList.get(finalJ).getOpenDoor()) {
                                    doorList.get(finalJ).setOpenDoor(true);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(true);
                                            }
                                        }
                                    }

                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsTop.setImage(doorOpenTop);
                                } else {
                                    doorList.get(finalJ).setOpenDoor(false);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(false);
                                            }
                                        }
                                    }

                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsTop.setImage(doorCloseTop);
                                }
                            } else {
                            	consoleLog("This door is locked");
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                alert.showAndWait();
                            }
                        }
                    });
                }

                if (doorList.get(finalJ).getPosition().toString() == "LEFT") {
                    String connectedRoom = doorList.get(finalJ).getConnection();
                    if (!doorList.get(finalJ).getOpenDoor())
                        doorsLeft.setImage(doorCloseLeft);
                    else {
                        doorsLeft.setImage(doorOpenLeft);
                    }

                    doorsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (doorList.get(finalJ).getPosition().toString() == "LEFT" && !doorList.get(finalJ).getLock()) {
                                if (!doorList.get(finalJ).getOpenDoor()) {
                                    doorList.get(finalJ).setOpenDoor(true);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(true);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsLeft.setImage(doorOpenLeft);
                                } else {
                                    doorList.get(finalJ).setOpenDoor(false);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(false);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsLeft.setImage(doorCloseLeft);
                                }
                            } else {
                            	consoleLog("This door is locked");
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                alert.showAndWait();
                            }
                        }
                    });
                }

                if (doorList.get(finalJ).getPosition().toString() == "RIGHT") {
                    String connectedRoom = doorList.get(finalJ).getConnection();
                    if (!doorList.get(finalJ).getOpenDoor())
                        doorsRight.setImage(doorCloseRight);
                    else {
                        doorsRight.setImage(doorOpenRight);
                    }

                    doorsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (doorList.get(finalJ).getPosition().toString() == "RIGHT" && !doorList.get(finalJ).getLock()) {
                                if (!doorList.get(finalJ).getOpenDoor()) {
                                    doorList.get(finalJ).setOpenDoor(true);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(true);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsRight.setImage(doorOpenRight);
                                } else {
                                    doorList.get(finalJ).setOpenDoor(false);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(false);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsRight.setImage(doorCloseRight);
                                }
                            } else {
                            	consoleLog("This door is locked");
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                alert.showAndWait();
                            }
                        }
                    });
                }

                if (doorList.get(finalJ).getPosition().toString() == "BOTTOM") {
                    String connectedRoom = doorList.get(finalJ).getConnection();
                    if (!doorList.get(finalJ).getOpenDoor())
                        doorsBottom.setImage(doorCloseBottom);
                    else {
                        doorsBottom.setImage(doorOpenBottom);
                    }

                    doorsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (doorList.get(finalJ).getPosition().toString() == "BOTTOM" && !doorList.get(finalJ).getLock()) {
                                if (!doorList.get(finalJ).getOpenDoor()) {
                                    doorList.get(finalJ).setOpenDoor(true);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(true);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsBottom.setImage(doorOpenBottom);
                                } else {
                                    doorList.get(finalJ).setOpenDoor(false);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(false);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsBottom.setImage(doorCloseBottom);
                                }
                            } else {
                            	consoleLog("This door is locked");
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                alert.showAndWait();
                            }
                        }
                    });
                }
            }

            // when room has 1 door
            if(doorList.size() == 1) {
                ImageView selectedDoors = new ImageView();

                if(roomArray[i].getDoors().get(0).getPosition().toString().equals("TOP"))
                    selectedDoors = doorsTop;
                if(roomArray[i].getDoors().get(0).getPosition().toString().equals("BOTTOM"))
                    selectedDoors = doorsBottom;
                if(roomArray[i].getDoors().get(0).getPosition().toString().equals("LEFT"))
                    selectedDoors = doorsLeft;
                if(roomArray[i].getDoors().get(0).getPosition().toString().equals("RIGHT"))
                    selectedDoors = doorsRight;
                gpSHCDoors.addRow(i, selectedDoors);
            }

            // when room has 2 doors
            if(doorList.size() == 2) {
                ImageView[] selectedDoors = new ImageView[2];

                for(int j = 0; j < selectedDoors.length; j++) {
                    if (roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                        selectedDoors[j] = doorsTop;
                    if (roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                        selectedDoors[j] = doorsBottom;
                    if (roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                        selectedDoors[j] = doorsLeft;
                    if (roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                        selectedDoors[j] = doorsRight;
                }
                gpSHCDoors.addRow(i, selectedDoors[0], selectedDoors[1]);
            }

            // when room has 3 doors
            if(doorList.size() == 3) {
                ImageView[] selectedDoors = new ImageView[3];

                for(int j = 0; j < selectedDoors.length; j++) {
                    if (roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                        selectedDoors[j] = doorsTop;
                    if (roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                        selectedDoors[j] = doorsBottom;
                    if (roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                        selectedDoors[j] = doorsLeft;
                    if (roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                        selectedDoors[j] = doorsRight;
                }
                gpSHCDoors.addRow(i, selectedDoors[0], selectedDoors[1], selectedDoors[2]);
            }

            // when room has 4 doors
            if(doorList.size() == 4) {
                gpSHCDoors.addRow(i, doorsTop, doorsBottom, doorsLeft, doorsRight);
            }
        }

        vboxSHCDoors.getChildren().clear();

        vboxSHCDoors.getChildren().add(gpSHCDoors);

        drawPeople();

        //draw number of person(s) in each room
        for (int i = 0; i < roomArray.length; i++) {
            if(userPositions.get(roomArray[i].getName()) != null){
                int numberOfPeople = userPositions.get(roomArray[i].getName());
                int[] positions = roomPosition.get(roomArray[i].getName());
                gc.fillText(String.valueOf(numberOfPeople) + " person(s)", positions[0] + 10, positions[1] + 35);
            }
        }
    }

    /**
     * calculates the number of people in each room
     */
    public void drawPeople () {
        Iterator<Map.Entry<String, String>> itr = userLocation.entrySet().iterator();
        Iterator<Map.Entry<String, String>> itr2 = userLocation.entrySet().iterator();

        while(itr2.hasNext())
        {
            Map.Entry<String, String> entry = itr2.next();
            userPositions.put(entry.getValue(), 0);
        }

        while(itr.hasNext())
        {
            Map.Entry<String, String> entry = itr.next();
            userPositions.put(entry.getValue(), userPositions.get(entry.getValue()) + 1);
        }
    }


    /**
     * This function draws windows on the house layout
     *
     * @param room room where window is situated
     * @param position position of the window in the room
     */
    public void drawWindows(Room room, String position) {
        int[] coordinates = roomPosition.get(room.getName());
        int x = coordinates[0];
        int y = coordinates[1];

        gc.setLineWidth(2);
        for (Window window : room.getWindows()) {
            if(window.getOpenWindow()) {
                gc.setStroke(Color.BLUE);
            }
            else{
                gc.setStroke(Color.BLACK);
            }
            switch (window.getPosition()) {
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
     * This function will draw the block icon on a window.
     *
     * @param room where blocking icon will be drawn.
     * @param position where window is
     */
    public void drawBlockWindow(Room room, Position position) throws FileNotFoundException {
        String name = room.getName();
        Image block;
        int[] coordinates = roomPosition.get(name);
        ArrayList<Window> windowList = room.getWindows();
        for (int i = 0; i < windowList.size(); i++) {
            if(windowList.get(i).getPosition().equals(position)) {
                if(position.toString().equals("TOP")) {
                    if(windowList.get(i).getBlocking()) {
                        block = new Image(new FileInputStream("src/main/resources/Images/alertIcon.png"), 60, 14, true, false);
                        gc.drawImage(block, coordinates[0] + 37, coordinates[1] - 7);
                    }
                }
                if(position.toString().equals("BOTTOM")) {
                    if(windowList.get(i).getBlocking()) {
                        block = new Image(new FileInputStream("src/main/resources/Images/alertIcon.png"), 60, 14, true, false);
                        gc.drawImage(block, coordinates[0] + 37, coordinates[1] + 83);
                    }
                }
                if(position.toString().equals("RIGHT")) {
                    if(windowList.get(i).getBlocking()) {
                        block = new Image(new FileInputStream("src/main/resources/Images/alertIcon.png"), 60, 14, true, false);
                        gc.drawImage(block, coordinates[0] + 83, coordinates[1] + 37);
                    }
                }
                if(position.toString().equals("LEFT")) {
                    if(windowList.get(i).getBlocking()) {
                        block = new Image(new FileInputStream("src/main/resources/Images/alertIcon.png"), 60, 14, true, false);
                        gc.drawImage(block, coordinates[0] - 7, coordinates[1] + 37);
                    }
                }
            }
        }
    }

    /**
     * This function will draw the lights with a given room.
     *
     * @param room where light will be drawn.
     */
    public void drawLight(Room room) {
        String name = room.getName();
        Image img = null;
        if (room.getLightsOn() == 0) {
            try {
                img = new Image(new FileInputStream("src/main/resources/Images/lightOff.png"), 60, 27, true, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int[] coordinates = roomPosition.get(name);
            gc.drawImage(img, coordinates[0] + 72, coordinates[1] + 2);
        }
        else {
            try {
                img = new Image(new FileInputStream("src/main/resources/Images/lightOn.png"), 60, 27, true, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int[] coordinates = roomPosition.get(name);
            gc.drawImage(img, coordinates[0] + 72, coordinates[1] + 2);
        }
    }

    /**
     * This function will draw the doors in a given room.
     *
     * @param room where door will be
     * @param position where door is situated
     */
    public void drawDoor(Room room, String position) {
        int[] coordinates = roomPosition.get(room.getName());
        int x = coordinates[0];
        int y = coordinates[1];

        gc.setLineWidth(2);
        for (Door door : room.getDoors()) {
            if (door.getOpenDoor()) {
                gc.setStroke(Color.web("#455A64"));
            } else {
                gc.setStroke(Color.SANDYBROWN);
            }
            switch (door.getPosition()) {
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
     * Method responsible for drawing the rooms in their correct location
     *
     * @param roomHashMap A map of the rooms and their names as the key
     * @param room        room to draw
     * @param visited     rooms that have been visited
     * @param previous    position of the previously visited room
     * @param x           x coordinate of the previously visited room
     * @param y           y coordinate of the previously visited room
     */
    public void drawRoom(Map<String, Room> roomHashMap, Room room, Set<Room> visited, Position previous, int x, int y) {
        visited.add(room);

        // if room is entrance or backyard no line is drawn
        if (room.getName().equals("Entrance") || room.getName().equals("Backyard")) {
            switch (previous) {
                case NONE -> {
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case BOTTOM -> {
                    y += ROOM_SIZE;
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case RIGHT -> {
                    x += ROOM_SIZE;
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case TOP -> {
                    y -= ROOM_SIZE;
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case LEFT -> {
                    x -= ROOM_SIZE;
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
            }
        }
        else {
            switch (previous) {
                case NONE -> {
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case BOTTOM -> {
                    y += ROOM_SIZE;
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case RIGHT -> {
                    x += ROOM_SIZE;
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case TOP -> {
                    y -= ROOM_SIZE;
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case LEFT -> {
                    x -= ROOM_SIZE;
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
            }
        }

        //updating a hashMap to get all the roomPosition with their coordinates.
        int[] coordinates = {x, y};
        roomPosition.put(room.getName(), coordinates);

        //draw the light for a room.
        drawLight(room);

        //draw the window for a room
        for(int i = 0; i < room.getWindows().size() ; i++) {
            drawWindows(room, room.getWindows().get(i).getPosition().toString());
        }

        //draw the door for a room
        for(int i = 0; i < room.getDoors().size() ; i++) {
            drawDoor(room, room.getDoors().get(i).getPosition().toString());
        }

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
     * This function loads the change date and time page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the scene file cannot be read
     */
    public void bt_changeDateTimeOnClick(ActionEvent event) throws IOException {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, unable to process action.");
    	} else {
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/changeDateTime.fxml"));
	        Parent root = loader.load();
	        Stage stage = new Stage();
	        stage.initStyle(StageStyle.TRANSPARENT);
	        stage.setScene(new Scene(root));
	        stage.show();
    	}
    }

    /**
     * This function loads the edit(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToEdit(ActionEvent event) throws IOException {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, enable to process action.");
    	} else {
	        if (Objects.nonNull(house)) {
	            Parent edit = FXMLLoader.load(getClass().getResource("/view/editSimulation.fxml"));
	            Scene editScene = new Scene(edit);
	
	            // stage info
	            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
	            window.setScene(editScene);
	            window.show();
	        } else {
	        	consoleLog("Please input the house to change location.");
	            Alert alert = new Alert(Alert.AlertType.WARNING, "Please input the house");
	            alert.showAndWait();
	        }
    	}

    }

    /**
     * Accessor for the current session's house plan
     *
     * @return Map of locations with their name as the key and the value being the individual {@link Room} plan
     */
    public static Map<String, Room> getHouse() {
        return house;
    }

    /**
     * Deletes the current house
     */
    public static void deleteHouse() {
        LoginInfoController.house = null;
    }

    /**
     * This function loads the scheduleLights pane
     *
     * @param event the event that triggers this function
     * @throws IOException if the view file is not found
     */
    public void scheduleLights(ActionEvent event) throws IOException {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, enable to process action.");
    	} else {
	        if (!awayMode){
	        	consoleLog("Away mode is turned off, cannot schedule the lights");
	            Alert alert = new Alert(Alert.AlertType.WARNING, "Away mode is turned off");
	            alert.showAndWait();
	            return;
	        }
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/lightsSchedule.fxml"));
	        Parent root = loader.load();
	        Stage stage = new Stage();
	        stage.initStyle(StageStyle.TRANSPARENT);
	        stage.setScene(new Scene(root));
	        stage.show();
    	}
    }

    /**
     * This function writes the light scheduling to the loginInfo SHP page and calls the method to
     * check the scheduling
     * 
     * @param room the name of the room in which the light will remain on
     * @param times the begin and end times at which the light remains on
     * @throws ParseException if the Date can't be parsed correctly
     */
    public void setRoomLightSchedule(String room, String times) throws ParseException {
        roomToLight.setText(roomToLight.getText() + "\n" + room + " " + times);

        String[] timeBeginEnd = times.split("-");

        Date beginTime=new SimpleDateFormat("HH:mm").parse(timeBeginEnd[0]);
        Date endTime=new SimpleDateFormat("HH:mm").parse(timeBeginEnd[1]);

        Date[] arr = new Date[]{beginTime, endTime};
        lightsSchedule.put(room, arr);

        lightScheduleLight(room, beginTime, endTime);
    }

    /**
     * This function is called when the time is changed. It checks through every entry to determine if some lights
     * need to be turned on based on the schedule
     * @throws ParseException if the Date can't be parsed correctly in lightScheduleLight
     */
    public void onTimeChangeLightRooms() throws ParseException {
        for (Map.Entry<String, Date[]> entry: lightsSchedule.entrySet()){
            lightScheduleLight(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
        }
    }

    /**
     * This function determines if the light should be turned on/off depending on the schedule
     *
     * @param room name of the room in which to turn on/off light
     * @param beginTime begin time of the schedule
     * @param endTime end time of the schedule
     * @throws ParseException if the Date can't be parsed correctly
     */
    public void lightScheduleLight(String room, Date beginTime, Date endTime) throws ParseException {

        String currentTimeWithoutSeconds ="";
        if (timeStr == null){
            currentTimeWithoutSeconds = time.toString().substring(33, 38);
        }
        else {
            currentTimeWithoutSeconds = timeStr.substring(0,4);
        }
        Date currentTime=new SimpleDateFormat("HH:mm").parse(currentTimeWithoutSeconds);

        if(endTime.after(currentTime) && currentTime.after(beginTime)){
            findRoomToLight(room, 1);
        }
        else {
            findRoomToLight(room, 0);
        }
    }

    /**
     * This method finds the room in roomArray sets the number of lights on to 1 and calls the drawLight method
     * @param room name of the room to turn the light on in
     */
    public void findRoomToLight(String room, int on){
        int index = 0;
        for (int i=0; i<roomArray.length; i++){
            if (roomArray[i].getName().equals(room)){
                index = i;
            }
        }
        roomArray[index].setLightsOn(on);
        drawLight(roomArray[index]);
    }
    
    /**
	 * Auto controll door lock states
	 * Room to auto locked: entrance, backyard and garage, +outside
	 */
	public void updateAutoDoors() {
		if(roomArray!=null && autoMode) {
			boolean closeDoor = false;
			Map<String, String> location = EditSimulationController.getUserLocations();
			String userRoomName = location.get(username);
			if(!userRoomName.equalsIgnoreCase("entrance") && !userRoomName.equalsIgnoreCase("backyard") && !userRoomName.equalsIgnoreCase("garage") && !userRoomName.equalsIgnoreCase("outside")) {
				closeDoor = true;
			} else closeDoor = false;
			for (int i = 0; i < roomArray.length; i++) {
				Room room = roomArray[i];
				if(room.getName().equalsIgnoreCase("entrance") || room.getName().equalsIgnoreCase("backyard") ||room.getName().equalsIgnoreCase("garage") || room.getName().equalsIgnoreCase("outside")) {
					ArrayList<Door> doorList = room.getDoors();
					for (int j = 0; j < doorList.size(); j++) { 
						if(closeDoor) {
							room.getDoors().get(j).setOpenDoor(false);
							room.getDoors().get(j).setLock(true);
						}
						else room.getDoors().get(j).setLock(false);
				      } 
					
				} 
			}
		}
	}
	
	/**
	 * Auto control lighting states of the house according to user location
	 */
	public void updateAutoLights() {
		if(roomArray!=null && autoMode) {
			Map<String, String> location = EditSimulationController.getUserLocations();
			String userRoomName = location.get(username);
			for (int i = 0; i < roomArray.length; i++) {
				if (roomArray[i].getName().equalsIgnoreCase((userRoomName))) {
					roomArray[i].setLightsOn(1);
				} else {
					roomArray[i].setLightsOn(0);
				}
			}
		}
	}

    /**
     * This method closes all the windows, doors and lights when away mode is turned on
     */
    public boolean closeWindowsDoorsLights(){
        if (roomArray !=  null) {
            for (Room r : roomArray) {
                ArrayList<Window> windowList = r.getWindows();
                for (Window w : windowList) {
                    if (w.getBlocking() && w.getOpenWindow()) {
                        consoleLog("Cannot activate away mode, one of the window is blocked by object.");
                        return false;
                    }
                }
            }
            for (Room r : roomArray) {
                r.setLightsOn(0);
                drawLight(r);

                ArrayList<Window> windowList = r.getWindows();
                for (Window w : windowList) {
                    w.setOpenWindow(false);
                    drawWindows(r, w.getPosition().toString());
                }
                ArrayList<Door> doorList = r.getDoors();
                for (Door d : doorList) {
                    d.setOpenDoor(false);
                    drawDoor(r, d.getPosition().toString());
                }
            }
        }
    	return true;
    }

    /**
     * This method sets the timeBeforeAlert variable
     */
    public void onSetTimeBeforeAlert(){
        if (!awayMode) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Away mode is turned off");
            alert.showAndWait();
            return;
        }
        timeBeforeAlert = timeBeforeAlertInput.getText();
    }


    /**
     * This function sends a notification to the user after the time delay has passed
     * after a user has been detected in the house while in away mode
     *
     * @param timeDelay the timeDelay to wait
     */
    public void sendNotification(String timeDelay){
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(Double.parseDouble(timeDelay)), e -> {
                    consoleLog("Notification sent to user. The time before alerting authorities of " + timeDelay
                            + " seconds has been elapsed.");
                })
        );
        timeline.play();
    }
}
