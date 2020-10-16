package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

/**
 * Controller responsible for the control and flow of the Date Time Popup scene
 */
public class ChangeDateTimeController implements Initializable {


    /**
     * declaring variables
     */
    @FXML
    private ComboBox<String> cb_year;
    @FXML
    private ComboBox<String> cb_month;
    @FXML
    private ComboBox<Integer> cb_date;
    @FXML
    private ComboBox<Integer> cb_hour;
    @FXML
    private ComboBox<Integer> cb_minute;

    private LoginInfoController parentController;
    private ObservableList<String> li_year;
    private final ObservableList<String> li_month = FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December");
    private ObservableList<Integer> li_date;
    private final ObservableList<Integer> li_hour = FXCollections.observableArrayList(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
    private final ObservableList<Integer> li_minute = FXCollections.observableArrayList(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
            52, 53, 54, 55, 56, 57, 58, 59);

    /**
     * Function from interface Initializable, initialize default actions
     */
    public void initialize(URL arg0, ResourceBundle arg1) {
        setDefaultValue();
        setComboBoxValue();
    }

    /**
     * This function will assign items to combo box
     */
    private void setComboBoxValue() {
        li_year = getYearList(); // get dynamic year list
        cb_year.setItems(li_year);
        cb_month.setItems(li_month);
        li_date = getDateList(); // get dynamic date list
        cb_date.setItems(li_date);
        cb_hour.setItems(li_hour);
        cb_minute.setItems(li_minute);
    }

    /**
     * This function will set default value of the pane
     */
    private void setDefaultValue() {
        LocalDate currentdate = LocalDate.now();
        cb_year.setValue(Integer.toString(currentdate.getYear()));
        cb_month.setValue(new DateFormatSymbols().getMonths()[currentdate.getMonthValue() - 1]);
        cb_date.setValue(currentdate.getDayOfMonth());
        LocalTime currentTime = LocalTime.now();
        cb_hour.setValue(currentTime.getHour());
        cb_minute.setValue(currentTime.getMinute());
    }

    /**
     * This function assign controller variable of the loginInfo pane
     *
     * @param loginInfoController
     */
    public void setParentController(LoginInfoController loginInfoController) {
        this.parentController = loginInfoController;
    }

    /**
     * This function submit the change to parent controller
     *
     * @param event
     * @throws IOException
     */
    public void bt_onChangeClick(ActionEvent event) throws IOException {

        String date = cb_year.getValue() + " - " + cb_month.getValue() + " - " + cb_date.getValue();

        LocalTime currentTime = LocalTime.now();
        String time = String.format("%02d:%02d:%02d", cb_hour.getValue(), cb_minute.getValue(), currentTime.getSecond());

        String result = date + " " + time; // in the format "yyyy - MMMM - dd HH:mm:ss"
        parentController.setTime(result);

        Stage stage = (Stage) cb_year.getScene().getWindow();
        stage.close();
    }

    /**
     * This function simply close the pane without any change
     *
     * @param event
     */
    public void bt_onCancelClick(ActionEvent event) {
        Stage stage = (Stage) cb_year.getScene().getWindow();
        stage.close();
    }


    /**
     * This function set dynamic date as month change
     *
     * @param event
     */
    public void cb_onMonthChange(ActionEvent event) {
        li_date = getDateList();
        cb_date.setItems(li_date);
    }

    /**
     * This function assigns the proper years range to List
     */
    private ObservableList<String> getYearList() {
        int range = 10;        // range of year +- range

        int year = (Calendar.getInstance().get(Calendar.YEAR)) + range;
        String[] arr = new String[range * 2];
        for (int i = 0; i < arr.length; i++) {
            year--;
            arr[i] = Integer.toString(year);
        }
        return FXCollections.observableArrayList(arr);
    }

    /**
     * This function assigns the proper date range to List
     */
    private ObservableList<Integer> getDateList() {
        int length;
        int month = Month.valueOf(cb_month.getValue().toUpperCase()).getValue();
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            length = 30;
        } else if (month == 2) {
            length = 29;
        } else {
            length = 31;
        }
        Integer[] arr = IntStream.of(IntStream.range(1, length + 1).toArray()).boxed().toArray(Integer[]::new);
        return FXCollections.observableArrayList(arr);
    }
}
