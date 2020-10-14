package entity;

public class Door {
    private Position position;
    private String connection;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public Door(String connection) {
        this.connection = connection;
    }

    public Door(Position position, String connection) {
        this.position = position;
        this.connection = connection;
    }
}
