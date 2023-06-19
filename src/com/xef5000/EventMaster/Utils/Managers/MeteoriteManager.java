package com.xef5000.EventMaster.Utils.Managers;

import com.google.gson.*;
import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.Events.Meteorite;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MeteoriteManager {

    // What we'll need to do json side:
    // - on enable plugin: load json file into memory (HashMap<String, ArrayList<Location>>)
    // - on event (list): add location of every meteorite block to memory (HashMap<String, ArrayList<Location>>)
    // - on disable plugin: flush memory (HashMap<String, ArrayList<Location>>) into json file
    // Functions needed:
    // - addLocation(String list, Location loc) @ Will be used when using Meteorite#sendMeteorite or Meteorite#sendMeteorites
    // - removeLocation(String list, Location loc) @ Will be used when using RightClickListener#onInteract to mention the meteorite block doesn't exist anymore

    private EventMaster eventMaster;
    public ArrayList<Meteorite> meteorites = new ArrayList<>();
    public HashMap<String, ArrayList<Location>> activeMeteorites = new HashMap<>();

    public MeteoriteManager(EventMaster eventMaster) {
        this.eventMaster = eventMaster;
    }

    /**Called when EventMaster#onEnable() */
    public void loadDataFile() {
        File directory = eventMaster.getDataFolder();
        File dataFile = new File(directory, "data.json");
        // create file it if it doesn't exist
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                try (FileWriter writer = new FileWriter(dataFile)){
                    dataFile.createNewFile();
                    new GsonBuilder().setPrettyPrinting().create().toJson(new JsonObject(), writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try (FileReader reader = new FileReader(dataFile)) {
            JsonElement fileElement = new JsonParser().parse(reader);

            if (fileElement == null || fileElement.isJsonNull()) {
                throw new JsonParseException("File is null!");
            }

            JsonObject fileObject = fileElement.getAsJsonObject();
            activeMeteorites = deserializeHashMap(fileObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Called when EventMaster#onDisable() */
    public void flushMemoryToJson() {
        File directory = eventMaster.getDataFolder();
        File dataFile = new File(directory, "data.json");
        // Empty the file or create it if it doesn't exist
        try {
            dataFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Write memory to the file
        try (FileWriter writer = new FileWriter(dataFile)){
            dataFile.createNewFile();
            new GsonBuilder().setPrettyPrinting().create().toJson(serializeHashMap(activeMeteorites), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonObject serializeHashMap(HashMap<String, ArrayList<Location>> map) {
        JsonObject output = new JsonObject();
        for (String listName : map.keySet()) {
            JsonArray locList = new JsonArray();
            for (Location location : map.get(listName)) {
                LinkedList<Integer> coordinates = new LinkedList<> ();
                coordinates.add(location.getBlockX());
                coordinates.add(location.getBlockY());
                coordinates.add(location.getBlockZ());

                JsonElement element = new Gson().toJsonTree(coordinates);
                locList.add(element);
            }
            output.add(listName, locList);
        }
        return output;
    }

    private HashMap<String, ArrayList<Location>> deserializeHashMap(JsonObject object) {
        HashMap<String, ArrayList<Location>> output = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            //entry:
            // spawn:
            //  [
            //   [0, 0, 0],
            //   [1, 1, 1]
            //  ]
            JsonArray locList = entry.getValue().getAsJsonArray();
            World world = eventMaster.listManager.getWorldOfList(entry.getKey());
            ArrayList<Location> locations = new ArrayList<>();
            for (int i = 0; i < locList.size(); i++) {
                JsonArray coords = locList.get(i).getAsJsonArray();
                int x = coords.get(0).getAsInt();
                int y = coords.get(1).getAsInt();
                int z = coords.get(2).getAsInt();
                locations.add(new Location(world, x, y, z));
            }



            output.put(entry.getKey(), locations);
        }
        return output;
    }

    public void addLocation(String listName, Location location) {
        if (activeMeteorites.get(listName) != null) {
            activeMeteorites.get(listName).add(location);
        } else {
            activeMeteorites.put(listName, new ArrayList<>(Collections.singletonList(location)));
        }
    }

    public void removeLocation(String listName, Location location) {
        activeMeteorites.get(listName).remove(location);
    }

    public HashMap<String, ArrayList<Location>> getActiveMeteorites() {
        return activeMeteorites;
    }

    public void addMeteorite(Meteorite meteorite) {
        meteorites.add(meteorite);
    }

    public ArrayList<Meteorite> getMeteorites() {
        return meteorites;
    }
}
