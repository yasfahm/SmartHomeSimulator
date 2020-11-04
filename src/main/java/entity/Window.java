package entity;

import constants.Position;

/**
 * class Window to represent a Window object in the house
 */
public class Window {
    private Position position;
    private boolean blocking;
    private boolean open;

    /**
     * Constructor for class Window
     *
     * @param position
     */
    public Window(Position position, boolean blocking, boolean open) {
        this.position = position;
        this.blocking = blocking;
        this.open = open;
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

    /**
     * Get the opening of the window
     *
     * @return opening of the window
     */
    public boolean getOpenWindow() {
        return open;
    }

    /**
     * Set the opening of the window
     *
     * @param open opening  of the window
     */
    public void setOpenWindow(boolean open) {
        this.open = open;
    }

}