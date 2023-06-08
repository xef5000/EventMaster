package com.xef5000.EventMaster.Utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

    private String text;
    private Location location;

    public Hologram(String text, Location location) {
        this.text = text;
        this.location = location;
    }

    public void spawn() {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        hologram.setCustomName(text);
        hologram.setVisible(false);
        hologram.setCustomNameVisible(true);
        hologram.setGravity(false);
    }
}
