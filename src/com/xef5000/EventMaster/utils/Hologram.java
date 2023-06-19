package com.xef5000.EventMaster.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hologram implements Serializable {

    private String text;
    private transient Location location;
    private transient ArmorStand armorStand;
    private String listName;

    public Hologram(String text, Location location, String listName) {
        this.text = text;
        this.location = location;
        this.listName = listName;
    }

    public void spawn() {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        hologram.setCustomName(text);
        hologram.setVisible(false);
        hologram.setCustomNameVisible(true);
        hologram.setGravity(false);
        this.armorStand = hologram;
    }

    public void despawn() {
        this.armorStand.remove();
    }

    public String getText() {
        return text;
    }

    public Location getLocation() {
        return location;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public String getListName() {
        return listName;
    }

    @Override
    public String toString() {
        return "Hologram{text="+text+",listName="+listName+",location="+location.toString()+",entity="+armorStand.toString()+"}";
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeObject(location.getWorld().getName());
        out.writeDouble(location.getX());
        out.writeDouble(location.getY());
        out.writeDouble(location.getZ());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();


        String worldName = (String) in.readObject();
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();

        location = new Location(Bukkit.getWorld(worldName), x, y, z);
        System.out.println("LOC: " + location);


        for (Entity entity : getNearbyEntities(location, 1)) {
            System.out.println("X:"+(entity.getLocation().getX() == location.getX())+" Y:"+(entity.getLocation().getY() == location.getY())+" Z:"+(entity.getLocation().getZ() == location.getZ()));
            if (entity.getLocation().getX() == location.getX() &&
                    entity.getLocation().getY() == location.getY() &&
                    entity.getLocation().getZ() == location.getZ()) {
                armorStand = (ArmorStand) entity;
                System.out.println("ADDED ARMOR STAND CNAME:" + armorStand.getCustomName());
                break;
            }
        }

    }

    public List<Entity> getNearbyEntities(Location where, int range) {
        List<Entity> found = new ArrayList<>();

        for (Entity entity : where.getWorld().getEntities()) {
            if (isInBorder(where, entity.getLocation(), range)) {
                found.add(entity);
            }
        }
        return found;
    }
    public boolean isInBorder(Location center, Location notCenter, int range) {
        int x = center.getBlockX(), z = center.getBlockZ();
        int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ();

        return x1 >= (x - range) && x1 <= (x + range) && z1 >= (z - range) && z1 <= (z + range);
    }
}
