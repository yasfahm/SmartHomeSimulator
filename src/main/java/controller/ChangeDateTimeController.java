package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

/**
 * Controller responsible for the control and flow of the Date Time Popup scene
 */
public class ChangeDateTimeController implements Initializable{
	
	private LoginInfoController parentController;
	
	public void setParentController(LoginInfoController loginInfoController) {
		this.parentController = loginInfoController;
	}
	
    @FXML
    private ComboBox<String> cb_year;
    ObservableList<String> li_year;

    @FXML
    private ComboBox<String> cb_month;
	ObservableList<String> li_month = FXCollections.observableArrayList(
			"January", "February", "March", "April", "May", "June", "July", "August", 
			"September", "October", "November", "December");

    @FXML
    private ComboBox<Integer> cb_date;
    ObservableList<Integer> li_date;

    @FXML
    private ComboBox<Integer> cb_hour;
	ObservableList<Integer> li_hour = FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10,11,12);

    @FXML
    private ComboBox<Integer> cb_minute;
    ObservableList<Integer> li_minute = FXCollections.observableArrayList(
    		1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 
    		20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 
    		36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
            52, 53, 54, 55, 56, 57, 58, 59, 60);

    @FXML
    private ComboBox<String> cb_ampm;
    ObservableList<String> li_ampm = FXCollections.observableArrayList("AM","PM");
    

    @FXML
    void bt_onChangeClick(ActionEvent event) throws IOException {
    	String date = cb_year.getValue() + "/" + cb_month.getValue() + "/" + cb_date.getValue();
    	String time = cb_hour.getValue() + ":" + cb_minute.getValue() + " " + cb_ampm.getValue();
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loginInfo.fxml"));
    	loader.load();
    	LoginInfoController controller = loader.getController();
		controller.setDate(date);
		
		parentController.setDate(date);
		parentController.setTime(time);
		
		Stage stage = (Stage) cb_year.getScene().getWindow();
		stage.close();
    }
    
    @FXML
    void bt_onCancelClick(ActionEvent event) {
    	Stage stage = (Stage) cb_year.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void cb_onMonthChange(ActionEvent event) {
    	li_date = getDateList();
		cb_date.setItems(li_date);
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setDefaultValue();
		li_year = getYearList();
		cb_year.setItems(li_year);
		cb_month.setItems(li_month);
		li_date = getDateList();
		cb_date.setItems(li_date);
		cb_hour.setItems(li_hour);
		cb_minute.setItems(li_minute);
		cb_ampm.setItems(li_ampm);
		
	}
	
	private ObservableList<String> getYearList(){
		int range = 10;		// range of year +- range
		
		int year = (Calendar.getInstance().get(Calendar.YEAR)) + range;
		String[] arr = new String[range*2];
		for(int i = 0; i < arr.length; i++) {
			year--;
			arr[i] = Integer.toString(year);
		}
		return FXCollections.observableArrayList(arr);
	}
	
	private ObservableList<Integer> getDateList(){
		int length;
		int month = Month.valueOf(cb_month.getValue().toUpperCase()).getValue();
		if (month == 4 || month == 6 || month == 9 || month == 11)  {
			length = 30;
		} else if (month == 2) {
			length = 29;
		} else {
			length = 31;
		}
		Integer[] arr = IntStream.of(IntStream.range(1, length+1).toArray()).boxed().toArray( Integer[]::new );
		return FXCollections.observableArrayList(arr);
	}
	
	/**
	 * This function will set default value of the pane
	 */
	private void setDefaultValue() {
		LocalDate currentDate = LocalDate.now();
		cb_year.setValue(Integer.toString(currentDate.getYear()));
		cb_month.setValue(new DateFormatSymbols().getMonths()[currentDate.getMonthValue()-1]);
		cb_date.setValue(currentDate.getDayOfMonth());
		cb_hour.setValue(Integer.parseInt(new SimpleDateFormat("hh").format(new Date())));
		cb_minute.setValue(Integer.parseInt(new SimpleDateFormat("mm").format(new Date())));
		cb_ampm.setValue(new SimpleDateFormat("a").format(new Date()));
	}
 

}
