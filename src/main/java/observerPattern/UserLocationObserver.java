package observerPattern;

import controller.LoginInfoController;

import java.util.Map;

public class UserLocationObserver extends Observer{

    public UserLocationObserver(Subject subject){
        this.subject = subject;
        this.subject.attach(this);
    }

    public void update() {
        Map<String, String> userLocations  = subject.getUserLocation();
        for (String person : userLocations.keySet()){
            if (LoginInfoController.isAwayMode() && !userLocations.get(person).equals("Outside")) {
                System.out.println("hello");
                LoginInfoController.consoleLogFile(person + " was detected in the " +
                        userLocations.get(person)+ " during Away Mode");
            }
        }
    }
}

