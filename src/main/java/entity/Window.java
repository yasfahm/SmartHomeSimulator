package entity;

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
     * @return position of the window
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position position of the window
     */
    public void setPosition(Position position) {
        this.position = position;
    }
}
