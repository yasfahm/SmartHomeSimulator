package entity;

import java.util.ArrayList;

/**
 * class Room to represent Room objects in the house
 */
public class Room {
    private String name;
    private ArrayList<Window> windows;
    private ArrayList<Door> doors;
    private int lightsOn;
    private int lightsTotal;

    /**
     * Constructor for class Room
     *
     * @param name        name of the room
     * @param windows     windows in the room
     * @param doors       doors in the room
     * @param lightsTotal total lights in the room
     */
    public Room(String name, ArrayList<Window> windows, ArrayList<Door> doors, int lightsTotal) {
        this.name = name;
        this.windows = windows;
        this.doors = doors;
        this.lightsTotal = lightsTotal;
    }

    /**
     * @return name of the room
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name of the room
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return number of lights on
     */
    public int getLightsOn() {
        return lightsOn;
    }

    /**
     * @param lightsOn number of lights on
     */
    public void setLightsOn(int lightsOn) {
        this.lightsOn = lightsOn;
    }

    /**
     * @return total number of lights
     */
    public int getLightsTotal() {
        return lightsTotal;
    }

    /**
     * @param lightsTotal total number of lights
     */
    public void setLightsTotal(int lightsTotal) {
        this.lightsTotal = lightsTotal;
    }

    /**
     * @return windows in the room
     */
    public ArrayList<Window> getWindows() {
        return windows;
    }

    /**
     * @param windows windows in the room
     */
    public void setWindows(ArrayList<Window> windows) {
        this.windows = windows;
    }

    /**
     * @return doors in the room
     */
    public ArrayList<Door> getDoors() {
        return doors;
    }

    /**
     * @param doors doors in the room
     */
    public void setDoors(ArrayList<Door> doors) {
        this.doors = doors;
    }

}
