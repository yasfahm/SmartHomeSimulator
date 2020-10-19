package entity;

import constants.Position;

/**
 * class Window to represent a Window object in the house
 */
public class Window {
    private Position position;
    private boolean blocking;

    /**
     * Constructor for class Window
     *
     * @param position
     */
    public Window(Position position, boolean blocking) {
        this.position = position;
        this.blocking = blocking;
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

    /**
     * Set the blocking of the window
     *
     * @param blocking blocking  of the window
     */
    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    /**
     * Get the blocking of the window
     *
     * @return blocking of the window
     */
    public boolean getBlocking() {
        return blocking;
    }

}
