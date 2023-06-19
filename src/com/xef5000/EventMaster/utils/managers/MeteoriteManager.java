package com.xef5000.EventMaster.utils.managers;

import com.google.gson.*;
import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.events.Meteorite;
import com.xef5000.EventMaster.utils.Hologram;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;
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
    public HashMap<Location, Hologram> locationHologramHashMap = new HashMap<>();
    public List<Hologram> deserializedHolograms = new ArrayList<>();

    public MeteoriteManager(EventMaster eventMaster) {
        this.eventMaster = eventMaster;
    }

    /**Called when EventMaster#onEnable() */
    public void loadDataFile() {
        deserializedHolograms = deserializeHolograms();
        //System.out.println(deserializedHolograms.get(0).getListName() + " " + deserializeHolograms().get(0).getArmorStand().getCustomName());
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
        matchHolograms();
    }

    /**Called when EventMaster#onDisable() */
    public void flushMemoryToJson() {
        serializeHolograms(getAllHolograms());
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

    public HashMap<Location, Hologram> getLocationHologramHashMap() {
        return locationHologramHashMap;
    }

    /**
     * @deprecated This method is deprecated. Use the overloaded serializeHolograms(List<Hologram>) method instead.
     */
    @Deprecated
    public void serializeHolograms() {
        File file = new File(eventMaster.getDataFolder(), "holograms.ser");
        for (Meteorite meteorite : getMeteorites()) {
            for (Hologram hologram : meteorite.getHolograms()) {
                try {
                    serialize(hologram, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Deprecated
    public void serialize(Object obj, File file)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);

        fos.close();
    }

    public void serializeHolograms(List<Hologram> holograms) {
        File file = new File(eventMaster.getDataFolder(), "holograms.ser");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(holograms);
            oos.close();
            fos.close();
            System.out.println("Holograms serialized and saved as holograms.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Hologram> getAllHolograms() {
        List<Hologram> output = new ArrayList<>();
        for (Meteorite meteorite : getMeteorites()) {
            output.addAll(meteorite.getHolograms());
        }
        return output;
    }

    /**
     * @deprecated This method is deprecated. Use the deserializeHolograms() method instead.
     */
    @Deprecated
    public Object deserialize(File file) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    @SuppressWarnings("unchecked")
    public List<Hologram> deserializeHolograms() {
        List<Hologram> holograms = new ArrayList<>();
        File file = new File(eventMaster.getDataFolder(), "holograms.ser");

        if (!file.exists()) {
            try {
                file.createNewFile();
                return holograms;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (file.length() == 0) {
            return holograms;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            holograms = (List<Hologram>) ois.readObject();
            ois.close();
            fis.close();
        } catch (EOFException e) {
            System.out.println("You can probably ignore this");
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return removeDuplicates(holograms);
    }

    public List<Hologram> removeDuplicates(List<Hologram> input) {
        ArrayList<Hologram> inputt = new ArrayList<>(input); // Step 1, removing duplicates
        List<Hologram> output = new ArrayList<>();
        String lastNameAdded = "";
        for (Hologram hologram : inputt) {
            if (!hologram.getListName().equalsIgnoreCase(lastNameAdded)) {
                output.add(hologram);
                lastNameAdded = hologram.getListName();
            }
        }
        return output;
    }

    public void matchHolograms() {
        for (String listName : getActiveMeteorites().keySet()) {
            for (Hologram hologram : deserializedHolograms) {
                if (hologram.getListName().equals(listName)) {
                    for (Location location : getActiveMeteorites().get(listName)) {
                        if ((location.getX() + 0.5f)== hologram.getLocation().getX() &&
                                (location.getY() - 0.6)== hologram.getLocation().getY() &&
                                (location.getZ() + 0.5f) == hologram.getLocation().getZ()) {
                            locationHologramHashMap.put(location, hologram);
                        }
                    }
                }
            }
        }
    }

    public void addDeserializedHolograms(String listName, Meteorite meteoriteEvent) {
        if (deserializedHolograms.isEmpty()) return;

        Iterator<Hologram> iterator = deserializedHolograms.iterator();
        while (iterator.hasNext()) {
            Hologram hologram = iterator.next();
            if (hologram.getListName().equals(listName)) {
                meteoriteEvent.getHolograms().add(hologram);
                iterator.remove();
            }
        }
    }
}
