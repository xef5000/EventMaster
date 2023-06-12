package com.xef5000.EventMaster;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.util.*;

public class ListManager {
    public EventMaster main;
    public HashMap<String, JsonArray> lists = new HashMap<>();

    public ListManager(EventMaster eventMaster) {
        this.main = eventMaster;
    }

    public void createCustomList(String name, JsonArray array) {
        if (name.toLowerCase().contains(".json")) return;
        File directory = new File(main.getDataFolder() + "\\lists");
        File listFile = new File(directory, name + ".json");
        if (!listFile.exists()) {
            listFile.getParentFile().mkdirs();
            try {
                listFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try (FileWriter writer = new FileWriter(listFile)){
            listFile.createNewFile(); // clears the file
            new Gson().toJson(array, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lists.put(name, array);
    }

    public void deleteCustomList(String name) {
        lists.remove(name);
        save();
    }

    public void addCoordinateToCustomList(String name, Location location) {
        JsonArray list = lists.get(name);
        //JsonObject worldName = new JsonObject();
        //worldName.addProperty("world", location.getWorld().toString());

        LinkedList<Integer> coordinates = new LinkedList<> ();
        coordinates.add((int) location.getX());
        coordinates.add((int) location.getY());
        coordinates.add((int) location.getZ());

        JsonElement element = new Gson().toJsonTree(coordinates);
        list.add(element);
        //JsonArray savedList = deepCopy(list, JsonArray.class);
        boolean foundJsonObject = false;
        for (JsonElement jsonElement : list) {
            if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().get("world").getAsJsonPrimitive() != null) {
                jsonElement.getAsJsonObject().addProperty("world", location.getWorld().getName());
                foundJsonObject = true;
                break;
            }
        }
        if (!foundJsonObject) {
            JsonObject worldName = new JsonObject();
            worldName.addProperty("world", location.getWorld().getName());
            list.add(worldName);
        }

        save();
    }

    public void removeCoordinateFromCustomList(String name, Location location) {
        JsonArray list = lists.get(name);
        JsonArray newList = new JsonArray();

        for (JsonElement element : list) {
            if (!element.isJsonArray()) continue;
            JsonArray coords = element.getAsJsonArray();
            int x = coords.get(0).getAsInt();
            int y = coords.get(1).getAsInt() - 1;
            int z = coords.get(2).getAsInt();
            if (location.getX() == x && location.getY() == y && location.getZ() == z) {
                continue;
            }
            newList.add(element);
        }
        lists.remove(name);
        lists.put(name, newList);
        save();
    }

    public void loadFiles() {
        ArrayList<File> files = (ArrayList<File>) listFilesForFolder(new File(main.getDataFolder() + "\\lists"));
        for (File file : files) {
            String fileName = file.getName();
            System.out.println("Found the file " + fileName);
            try (FileReader reader = new FileReader(file)) {
                JsonElement fileElement = new JsonParser().parse(reader);

                if (fileElement == null || fileElement.isJsonNull()) {
                    throw new JsonParseException("File is null!");
                }

                JsonArray parsedArray = fileElement.getAsJsonArray();
                lists.put(fileName.replace(".json", ""), parsedArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<File> listFilesForFolder(final File folder) {
        ArrayList<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                files.add(fileEntry);
            }
        }
        return files;
    }

    public LinkedList<Location> getLocationsFromList(String listName) {
        if (!lists.containsKey(listName)) {
            System.out.println("Error: list does not exist");
            return null;
        }
        LinkedList<Location> locations = new LinkedList<>();
        JsonArray jsonArray = lists.get(listName);

        String worldname = null;

        for (JsonElement jsonElement : jsonArray) {
            if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().get("world").getAsJsonPrimitive().getAsString() != null)
                worldname = jsonElement.getAsJsonObject().get("world").getAsJsonPrimitive().getAsString();

        }

        for (int i = 0; i < jsonArray.size(); i++) {
            if (!jsonArray.get(i).isJsonArray()) continue;
            JsonArray locationArray = (JsonArray) jsonArray.get(i);
            int x = locationArray.get(0).getAsInt();
            int y = locationArray.get(1).getAsInt();
            int z = locationArray.get(2).getAsInt();
            locations.add(new Location(Bukkit.getWorld(worldname), x, y, z));
        }

        return locations;
    }

    public void save() {
        HashMap<String, JsonArray> lists1 = (HashMap<String, JsonArray>) lists.clone();
        lists.clear();
        for (String name : lists1.keySet()) {
            createCustomList(name, lists1.get(name));
        }
    }

    public <T> T deepCopy(T object, Class<T> type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJson(object, type), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
