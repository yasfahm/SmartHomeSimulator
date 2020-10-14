package entity;

import java.util.ArrayList;

public class Room {
    private String name;
    private ArrayList<Window> windows;
    private ArrayList<Door> doors;
    private int lightsOn;
    private int lightsTotal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLightsOn() {
        return lightsOn;
    }

    public void setLightsOn(int lightsOn) {
        this.lightsOn = lightsOn;
    }

    public int getLightsTotal() {
        return lightsTotal;
    }

    public void setLightsTotal(int lightsTotal) {
        this.lightsTotal = lightsTotal;
    }

    public ArrayList<Window> getWindows() {
        return windows;
    }

    public void setWindows(ArrayList<Window> windows) {
        this.windows = windows;
    }

    public ArrayList<Door> getDoors() {
        return doors;
    }

    public void setDoors(ArrayList<Door> doors) {
        this.doors = doors;
    }

    public Room(String name, ArrayList<Window> windows, ArrayList<Door> doors, int lightsTotal) {
        this.name = name;
        this.windows = windows;
        this.doors = doors;
        this.lightsTotal = lightsTotal;
    }
}
