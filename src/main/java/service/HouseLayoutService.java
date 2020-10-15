package service;

import entity.Door;
import entity.Position;
import entity.Room;
import entity.Window;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class HouseLayoutService {

    /**
     * Method creating the rooms for the house
     *
     * @param file The file used to describe the house's rooms
     * @return An array of {@link Room} that comprises the house
     * @throws IOException Thrown if the file cannot be read
     */
    public static Room[] parseHouseLayout(File file) throws IOException {
        String marshalled = Files.readString(file.toPath(), StandardCharsets.US_ASCII);

        JSONObject parsed = new JSONObject(marshalled);

        JSONArray roomsJSON = parsed.getJSONArray("rooms");
        Room[] roomsArray = new Room[roomsJSON.length()];

        for (int i=0; i<roomsJSON.length(); i++){
            JSONObject room = roomsJSON.getJSONObject(i);
            String name = roomsJSON.getJSONObject(i).getString("name");
            int lightsTotal = roomsJSON.getJSONObject(i).getInt("lights");

            JSONArray windowsJSON = room.getJSONArray("windows");
            ArrayList<Window> windows = new ArrayList<>();

            JSONArray doorsJSON = room.getJSONArray("doors");
            ArrayList<Door> doors = new ArrayList<>();

            for (int j=0; j<windowsJSON.length();j++){
                int position = windowsJSON.getJSONObject(j).getInt("position");
                windows.add(new Window(getPosition(position)));
            }

            for (int j=0; j<doorsJSON.length();j++){
                int position = doorsJSON.getJSONObject(j).getInt("position");
                String connection = doorsJSON.getJSONObject(j).getString("connection");
                switch (position) {
                    case 0:
                        doors.add(new Door(Position.NONE, connection));
                        break;
                    case 1:
                        doors.add(new Door(Position.TOP, connection));
                    case 2:
                        doors.add(new Door(Position.RIGHT, connection));
                        break;
                    case 3:
                        doors.add(new Door(Position.BOTTOM, connection));
                        break;
                    case 4:
                        doors.add(new Door(Position.LEFT, connection));
                        break;
                }
            }
            roomsArray[i] = new Room(name, windows, doors, lightsTotal);
        }
        return roomsArray;
    }

    /**
     * @param x position index
     * @return Position enum value
     */
    public static Position getPosition(int x) {
        switch (x){
            case 0 -> {return Position.NONE;}
            case 1 -> {return Position.TOP;}
            case 2 -> {return Position.RIGHT;}
            case 3 -> {return Position.BOTTOM;}
            case 4 -> {return Position.LEFT;}
        }
        return Position.NONE;
    }

}
