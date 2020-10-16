package entity;

import constants.Position;

/**
 * class Door to represent a Door object in the house
 */
public class Door {
    private Position position;
    private String connection;

    /**
     * Constructor for class Door
     *
     * @param position   position of the door
     * @param connection room the door connects to
     */
    public Door(Position position, String connection) {
        this.position = position;
        this.connection = connection;
    }

    /**
     * Constructor for class Door
     *
     * @param connection room the door connects to
     */
    public Door(String connection) {
        this.connection = connection;
    }

    /**
     * @return position of the door
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position position of the door
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * @return room the door connects to
     */
    public String getConnection() {
        return connection;
    }

    /**
     * @param connection room the door connects to
     */
    public void setConnection(String connection) {
        this.connection = connection;
    }

}
