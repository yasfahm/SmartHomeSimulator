package entity;

import constants.Position;

/**
 * class Window to represent a Window object in the house
 */
public class Window {
    private Position position;

    /**
     * Constructor for class Window
     *
     * @param position
     */
    public Window(Position position) {
        this.position = position;
    }

    /**
     * Get the position of the window
     *
     * @return position of the window
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Set the position of the window
     *
     * @param position position of the window
     */
    public void setPosition(Position position) {
        this.position = position;
    }
}
