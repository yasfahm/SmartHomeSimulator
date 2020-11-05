package controller;

import constants.Position;
import entity.Door;
import entity.Room;
import entity.Window;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import service.ConsoleService;
import service.HouseLayoutService;
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
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginInfoController implements Initializable {

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

    private static String userParent;
    private static Map<String, Room> house;
    private static Room[] roomArray;
    private static String username;
    private static boolean awayMode;
    private static BooleanProperty booleanProperty;
    private final Text toggleText = new Text();
    private static String consoleLog = "";

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

            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
            SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
            long sysmillis = System.currentTimeMillis();
            timeInMillis = sysmillis;
            Date d = new Date(sysmillis);
            this.date.setText(formatDate.format(d));
            this.time.setText(formatTime.format(d));

        }

        if (Objects.nonNull(house)) {
            rooms.getItems().addAll(house.keySet());
            rooms.getSelectionModel().selectFirst();
        }
      
        console.appendText(consoleLog);
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
     * On Click function for the temperature label
     *
     * @param event The event that triggered the onClick method
     */
    public void temperatureOnClick(MouseEvent event) {
        invisibleContainer.getChildren().add(temperature);
        textFieldTemperature.setText(temperature.getText());
        textFieldTemperature.setPrefWidth(20 + (temperature.getText().length() * 5));
        hBoxTemperature.getChildren().add(0, textFieldTemperature);

        textFieldTemperature.requestFocus();

        textFieldTemperature.setOnAction(e -> {  // on enter key
            changeTemperatureOnEnter();
        });
    }

    /**
     * On Enter functionality for the textFieldTemperature
     */
    protected void changeTemperatureOnEnter() {
        if (textFieldTemperature.getText().matches("-?\\d+") && textFieldTemperature.getText().length() != 0) {

            int new_temp = Integer.parseInt(textFieldTemperature.getText());
            System.out.println("its an integer " + new_temp);

            invisibleContainer.getChildren().add(textFieldTemperature);
            hBoxTemperature.getChildren().add(0, temperature);
            temperature.setText(textFieldTemperature.getText());
            textFieldTemperature.clear();
            temperatureInInt = Integer.parseInt(temperature.getText());
        } else {
            consoleLog("Please enter a valid temperature input.");
        }
    }

    public void onMouseClickAwayToggleON(MouseEvent event) {
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
            awayMode = true;
            awayModeON.setSelected(true);
        } else {
            consoleLog("Unable to turn Away Mode ON, there is someone in the house");
            awayMode = false;
            awayModeON.setSelected(false);
            awayModeOFF.setSelected(true);
        }
    }

    public void onMouseClickAwayToggleOFF(MouseEvent event) {
        awayMode = false;
        awayModeOFF.setSelected(true);
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
                    username = newValue;
                    selectedUser.getSelectionModel().select(newValue);
                    userRole.setText(listOfUsers.get(newValue));
                    Map userLocs = EditSimulationController.getUserLocations();
                    loc.setText(Objects.isNull(userLocs) ? "Outside" : userLocs.get(username).toString());
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
        this.console.setText(consoleLog);
    }

    /**
     * This function is responsible for updating the cached logs
     *
     * @param str String to append onto the console
     */
    private static void updateConsoleLog(String str) {
        String toAppend = "[" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString() + "] " + str + "\n";
        consoleLog += toAppend;
        ConsoleService.exportConsole(toAppend);
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

            vboxSHCRooms.getChildren().add(gpSHCRooms);

            GridPane gpSHCLights = new GridPane();
            gpSHCLights.setVgap(13);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Image lightOn = new Image(new FileInputStream("src/main/resources/Images/lightOn.png"), 60, 27, true, false);
                Image lightOff = new Image(new FileInputStream("src/main/resources/Images/lightOff.png"), 60, 27, true, false);
                ImageView light = new ImageView(lightOff);
                int finalI = i;
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

            vboxSHCLights.getChildren().add(gpSHCLights);

            // open/close window functionality
            GridPane gpSHCWindows = new GridPane();
            gpSHCWindows.setVgap(13);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Image windowOpenTop = new Image(new FileInputStream("src/main/resources/Images/windowOpenTop.png"), 60, 27, true, false);
                Image windowCloseTop = new Image(new FileInputStream("src/main/resources/Images/windowCloseTop.png"), 60, 27, true, false);
                Image windowOpenBottom = new Image(new FileInputStream("src/main/resources/Images/windowOpenBottom.png"), 60, 27, true, false);
                Image windowCloseBottom = new Image(new FileInputStream("src/main/resources/Images/windowCloseBottom.png"), 60, 27, true, false);
                Image windowOpenLeft = new Image(new FileInputStream("src/main/resources/Images/windowOpenLeft.png"), 60, 27, true, false);
                Image windowCloseLeft = new Image(new FileInputStream("src/main/resources/Images/windowCloseLeft.png"), 60, 27, true, false);
                Image windowOpenRight = new Image(new FileInputStream("src/main/resources/Images/windowOpenRight.png"), 60, 27, true, false);
                Image windowCloseRight = new Image(new FileInputStream("src/main/resources/Images/windowCloseRight.png"), 60, 27, true, false);
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

                    if (windowList.get(finalJ).getPosition().toString() == "TOP") {
                        windowsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "TOP") {
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
                            }
                        });
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "LEFT") {
                        windowsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "LEFT") {
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
                            }
                        });
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "RIGHT") {
                        windowsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "RIGHT") {
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
                            }
                        });
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "BOTTOM") {
                        windowsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "BOTTOM") {
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
                            }
                        });
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

            vboxSHCWindows.getChildren().add(gpSHCWindows);

            // open/close door functionality
            GridPane gpSHCDoors = new GridPane();
            gpSHCDoors.setVgap(13);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Image doorOpenTop = new Image(new FileInputStream("src/main/resources/Images/DoorOpenTop.png"), 60, 27, true, false);
                Image doorCloseTop = new Image(new FileInputStream("src/main/resources/Images/DoorCloseTop.png"), 60, 27, true, false);
                Image doorOpenBottom = new Image(new FileInputStream("src/main/resources/Images/DoorOpenBottom.png"), 60, 27, true, false);
                Image doorCloseBottom = new Image(new FileInputStream("src/main/resources/Images/DoorCloseBottom.png"), 60, 27, true, false);
                Image doorOpenLeft = new Image(new FileInputStream("src/main/resources/Images/DoorOpenLeft.png"), 60, 27, true, false);
                Image doorCloseLeft = new Image(new FileInputStream("src/main/resources/Images/DoorCloseLeft.png"), 60, 27, true, false);
                Image doorOpenRight = new Image(new FileInputStream("src/main/resources/Images/DoorOpenRight.png"), 60, 27, true, false);
                Image doorCloseRight = new Image(new FileInputStream("src/main/resources/Images/DoorCloseRight.png"), 60, 27, true, false);
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

            vboxSHCDoors.getChildren().add(gpSHCDoors);

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please turn on the simulation first");
            alert.showAndWait();
        }
    }

    /**
     * Draws the room based on the cached static variables after this file has been submitted
     */
    public void drawRoomFromCache() throws FileNotFoundException {
        gc = houseRender.getGraphicsContext2D();
        gc.setFont(new Font(11));
        drawRoom(house, roomArray[0], new HashSet<>(), Position.NONE, 130, 190);
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
     * This function will draw the lights with a given room.
     *
     * @param room where light will be drawn.
     */
    public void drawLight(Room room){
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
                gc.setStroke(Color.WHITE);
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

        //setting a default value for light status which means that is off.
        room.setLightsOn(0);

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/changeDateTime.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
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
        if (Objects.nonNull(house)) {
            Parent edit = FXMLLoader.load(getClass().getResource("/view/editSimulation.fxml"));
            Scene editScene = new Scene(edit);

            // stage info
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(editScene);
            window.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please input the house");
            alert.showAndWait();
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
        if (!awayMode){
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

    /**
     * This function writes the light scheduling to the loginInfo SHP page
     *
     * @param room the name of the room in which the light will remain on
     * @param times the begin and end times at which the light remains on
     */
    public void setRoomLightSchedule(String room, String times) {
        roomToLight.setText(roomToLight.getText() + "\n" + room + " " + times);
    }
}
