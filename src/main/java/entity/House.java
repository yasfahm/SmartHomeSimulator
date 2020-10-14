package entity;

public class House {
    private Room[] rooms;

    public Room[] getRooms() {
        return rooms;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    public House(Room[] rooms) {
        this.rooms = rooms;
    }
}
