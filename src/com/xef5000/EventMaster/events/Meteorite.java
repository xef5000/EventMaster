package com.xef5000.EventMaster.events;

import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.utils.Hologram;
import com.xef5000.EventMaster.utils.shockwave.Ripple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class Meteorite implements Listener {

    private static boolean meteoriteLanded = false;
    private final EventMaster main;
    private boolean shockwave;
    private boolean lightning;
    private boolean hologram;
    private String hologramString;
    private boolean falling;
    private int percent;
    private ArrayList<Location> locations;
    private ArrayList<Hologram> holograms;
    private String listName;
    private Material material;

    /**Admin meteorite with custom settings*/
    public Meteorite(Location blockPosition, EventMaster eventMaster, boolean shockwave, boolean lightning, boolean hologram, boolean falling, String hologramString) {
        this.main = eventMaster;
        this.locations = new ArrayList<>();
        this.locations.add(blockPosition);
        this.shockwave = shockwave;
        this.lightning = lightning;
        this.hologram = hologram;
        this.hologramString = hologramString;
        this.holograms = new ArrayList<>();
        this.falling = falling;
        this.percent = 100;
        this.listName = null;
        this.material = Material.valueOf(main.getConfig().getString("meteorite-block"));
    }

    /**Admin meteorite with default config settings*/
    public Meteorite(Location blockPosition, EventMaster eventMaster) {
        this.main = eventMaster;
        this.locations = new ArrayList<>();
        this.locations.add(blockPosition);
        this.shockwave = eventMaster.listManager.main.getConfig().getBoolean("meteorite-shockwave");
        this.lightning = eventMaster.listManager.main.getConfig().getBoolean("meteorite-lightning");
        this.hologram = eventMaster.listManager.main.getConfig().getBoolean("meteorite-hologram");
        this.hologramString = eventMaster.listManager.main.getConfig().getString("meteorite-hologram-text");
        this.holograms = new ArrayList<>();
        this.falling = eventMaster.listManager.main.getConfig().getBoolean("meteorite-fall");
        this.percent = 100;
        this.listName = null;
        this.material = Material.valueOf(main.getConfig().getString("meteorite-block"));
    }

    /**Meteorite event from a list*/
    public Meteorite(String listName, EventMaster eventMaster) {
        if (!eventMaster.listManager.lists.containsKey(listName))
            throw new IllegalArgumentException("No list exist with the name " + listName);
        this.main = eventMaster;
        this.locations = new ArrayList<>();
        this.locations.addAll(eventMaster.listManager.getLocationsFromList(listName));
        System.out.println("Found " + locations.size() + " coordinates for " + listName);
        this.shockwave = eventMaster.listManager.main.getConfig().getBoolean("meteorite-lists." + listName + ".meteorite-shockwave");
        this.lightning = eventMaster.listManager.main.getConfig().getBoolean("meteorite-lists." + listName + ".meteorite-lightning");
        this.hologram = eventMaster.listManager.main.getConfig().getBoolean("meteorite-lists." + listName + ".meteorite-hologram");
        this.hologramString = eventMaster.listManager.main.getConfig().getString("meteorite-lists." + listName + ".meteorite-hologram-text");
        this.holograms = new ArrayList<>();
        this.falling = eventMaster.listManager.main.getConfig().getBoolean("meteorite-lists." + listName + ".meteorite-fall");
        this.percent = eventMaster.listManager.main.getConfig().getInt("meteorite-lists." + listName + ".percent-land");
        this.listName = listName;
        this.material = Material.valueOf(eventMaster.listManager.main.getConfig().getString("meteorite-lists." + listName + ".meteorite-block"));

        addDeserializedHolograms(listName);
    }

    /**Method for sending meteorites in list event*/
    public void sendMeteorites() {
        System.out.println("Sending meteorites.....");
        for (Location loc : locations) {
            //System.out.println("LOC");
            int randomInt = new SplittableRandom().nextInt(1, 101);
            System.out.println("Generated #" + randomInt + "(" + percent + ") " + !(randomInt <= percent));
            //int random_int = (int)Math.floor(Math.random() * (100 - 1) + 1);
            if (!(randomInt <= percent)) continue;
            System.out.println("Percentage test passed");
            // From here we only have meteorites that we know we will spawn
            World world = loc.getWorld();

            if (this.falling) {
                Location newLoc = loc.clone().add(0, 5, 0);
                FallingBlock fallingBlock = world.spawnFallingBlock(newLoc, Material.valueOf(main.getConfig().getString("meteorite-block")), (byte) 0);
                fallingBlock.setTicksLived(1);


                String stringBuilder = "eventmaster-meteorite-internallist:" +
                        (listName) +
                        (shockwave ? "-true" : "-false") +
                        (lightning ? "-true" : "-false") +
                        (hologram ? "-true-" : "-false") +
                        "-" + hologramString;

                fallingBlock.setCustomName(stringBuilder);
                fallingBlock.setVelocity(new Vector(0, -0.2, 0));
                fallingBlock.setDropItem(false);
                main.meteoriteManager.addLocation(listName, loc);
            } else {
                world.getBlockAt(loc).setType(material);
                if (shockwave) {
                    Ripple rippleEffect = new Ripple(main, loc.clone().add(0, -1, 0), 7.0, 3.5, 0.15, 2);
                    rippleEffect.runTaskTimer(main, 10L, 1L);
                }

                if (lightning)
                    world.strikeLightningEffect(loc);

                if (hologram) {
                    Hologram holograma = new Hologram(hologramString.replace("&", "§"), loc.clone().add(0.5, -0.6, 0.5), listName);
                    holograma.spawn();
                    holograms.add(holograma);
                }
                System.out.println("Added to " + listName + " " + loc);
                main.meteoriteManager.addLocation(listName, loc);
            }
/*
            if (shockwave) {
                Ripple rippleEffect = new Ripple(main, loc.clone().add(0, -1, 0), 7.0, 3.5, 0.15, 2);
                rippleEffect.runTaskTimer(main, 10L, 1L);
            }

            if (lightning)
                world.strikeLightningEffect(loc);

            if (hologram) {
                Hologram holograma = new Hologram(hologramString.replace("&", "§"), loc.clone().add(0.5, -0.6, 0.5));
                holograma.spawn();
            }

 */


        }
    }

    public Meteorite sendMeteorite() {
        Location loc = locations.get(0);
        World world = loc.getWorld();

        if (this.falling) {
            Location newLoc = loc.clone().add(0, 5, 0);
            FallingBlock fallingBlock = world.spawnFallingBlock(newLoc, material, (byte) 0);
            fallingBlock.setTicksLived(1);

            String stringBuilder = "eventmaster-meteorite-internal" +
                    (shockwave ? "-true" : "-false") +
                    (lightning ? "-true" : "-false") +
                    (hologram ? "-true-" : "-false") +
                    "-" + hologramString;

            fallingBlock.setCustomName(stringBuilder);
            fallingBlock.setVelocity(new Vector(0, -0.2, 0));
            fallingBlock.setDropItem(false);
        } else {
            world.getBlockAt(loc).setType(Material.valueOf(main.getConfig().getString("meteorite-block")));
            if (shockwave) {
                Ripple rippleEffect = new Ripple(main, loc.clone().add(0, -1, 0), 7.0, 3.5, 0.15, 2);
                rippleEffect.runTaskTimer(main, 10L, 1L);
            }

            if (lightning)
                world.strikeLightningEffect(loc);

            if (hologram) {
                Hologram holograma = new Hologram(hologramString.replace("&", "§"), loc.clone().add(0.5, -0.6, 0.5), listName);
                holograma.spawn();
            }
            main.meteoriteManager.addLocation("default", loc);
        }
/*
        if (shockwave) {
            Ripple rippleEffect = new Ripple(main, loc.clone().add(0, -1, 0), 7.0, 3.5, 0.15, 2);
            rippleEffect.runTaskTimer(main, 10L, 1L);
        }

        if (lightning)
            world.strikeLightningEffect(loc);

        if (hologram) {
            Hologram holograma = new Hologram(hologramString.replace("&", "§"), loc.clone().add(0.5, -0.6, 0.5));
            holograma.spawn();
        }

 */
        return this;
    }


    //TODO: Make the only argument the list name (+blockPos, +eventMaster) so we can retrieve all the information directly into this method instead of another method
    //TODO: Make meteorite object and create constructor
    public static void sendMeteorite(Location blockPosition, EventMaster eventMaster, boolean shockwave, boolean lightning, boolean hologram, boolean falling) {
        World world = blockPosition.getWorld();

        FallingBlock fallingBlock = null;
        if (falling) { // Falling meteorite boolean
            Location newLoc = blockPosition.clone().add(0, 5, 0);
            fallingBlock = world.spawnFallingBlock(newLoc, Material.valueOf(eventMaster.getConfig().getString("meteorite-block")), (byte) 0);
            fallingBlock.setTicksLived(1);
            fallingBlock.setCustomName("eventmaster-meteorite-internal");
            fallingBlock.setVelocity(new Vector(0, -0.2, 0));
            fallingBlock.setDropItem(false);
            meteoriteLanded = false;
        } else {
            world.getBlockAt(blockPosition).setType(Material.valueOf(eventMaster.getConfig().getString("meteorite-block")));
        }

        if (fallingBlock != null) {
            while (!meteoriteLanded) {
                //System.out.println("XYZ " + fallingBlock.getLocation().getBlockX() + " " + fallingBlock.getLocation().getY() + " " + fallingBlock.getLocation().getBlockZ());
                continue;
            }
            System.out.println("DIED");
        }



    }

    /*
    public static void startEvent(ListManager listManager, String listName) {
        LinkedList<Location> locations = listManager.getLocationsFromList(listName);
        int percent = listManager.main.getConfig().getInt("meteorite-lists." + listName + ".percent-land");
        boolean shockwave = listManager.main.getConfig().getBoolean("meteorite-lists." + listName + ".meteorite-shockwave");
        boolean lightning = listManager.main.getConfig().getBoolean("meteorite-lists." + listName + ".meteorite-lightning");
        boolean hologram = listManager.main.getConfig().getBoolean("meteorite-lists." + listName + ".meteorite-hologram");

        for (Location loc : locations) {
            int random_int = (int)Math.floor(Math.random() * (100 - 1) + 1);
            if (random_int <= percent)
                sendMeteorite(loc, listManager.main, shockwave, lightning, hologram, true);
        }
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().getCustomName().equals("eventmaster-meteorite-internal")) return;
        if (shockwave) {
            //ShockwaveHandler sh = new ShockwaveHandler(eventMaster, 5);
            //sh.createShockwave(blockPosition.clone().add(0, -1, 0));

            Ripple rippleEffect = new Ripple(eventMaster, blockPosition.clone().add(0, -1, 0), 7.0, 3.5, 0.15, 2);
            rippleEffect.runTaskTimer(eventMaster, 10L, 1L);
        }


        if (lightning) world.strikeLightningEffect(blockPosition);

        if (hologram) {
            Hologram holograma = new Hologram(eventMaster.getConfig().getString("meteorite-hologram-text").replace("&", "§"), blockPosition.clone().add(0.5, -0.6, 0.5));
            holograma.spawn();
        }
    }

     */

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public ArrayList<Hologram> getHolograms() {
        return holograms;
    }

    public String getListName() {
        return listName;
    }

    private void addDeserializedHolograms(String listName) {
        main.meteoriteManager.addDeserializedHolograms(listName, this);
        /*
        if (deserializedHolograms.isEmpty()) return;
        for (Hologram hologram : deserializedHolograms) {
            if (hologram.getListName().equals(listName)) {
                holograms.add(hologram);
            }
        }

         */
    }
}
