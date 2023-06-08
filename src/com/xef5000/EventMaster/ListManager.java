package com.xef5000.EventMaster;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ListManager {
    public EventMaster main;
    public HashMap<String, JsonArray> lists = new HashMap<>();

    public ListManager(EventMaster eventMaster) {
        this.main = eventMaster;
    }

    public void createCustomList(String name, JsonArray array) {
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
    }

    public void addCoordinateToCustomList(String name, Location location) {
        JsonArray list = lists.get(name);
        LinkedHashSet<Integer> coordinates = new LinkedHashSet<>(Arrays.asList((int) location.getX(), (int) location.getY(), (int) location.getZ()));

        JsonElement element = new Gson().toJsonTree(coordinates);
        list.add(element);
        save();
    }

    public void removeCoordinateFromCustomList(String name, Location location) {
        JsonArray list = lists.get(name);
        JsonArray newList = new JsonArray();

        for (JsonElement element : list) {
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

    public void save() {
        HashMap<String, JsonArray> lists1 = (HashMap<String, JsonArray>) lists.clone();
        lists.clear();
        for (String name : lists1.keySet()) {
            createCustomList(name, lists1.get(name));
        }
    }


}
