package observerPattern;

import controller.EditSimulationController;

import java.util.Map;

public class Subject {

    private Map<String, String> userLocations = EditSimulationController.getUserLocations();;
    private UserLocationObserver observer;

    public void setUserLocations(String key, String value){
        this.userLocations.put(key, value);
    }

    public Map<String, String> getUserLocation() {
        return userLocations;
    }

    public void attach(UserLocationObserver userLocationObserver){
        this.observer = userLocationObserver;
    }

    public void notifyAllObservers(){
        observer.update();
        }
    }