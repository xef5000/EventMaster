package com.xef5000.EventMaster.Utils.Shockwave;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Ripple extends BukkitRunnable {

    private final Location centerLoc;
    private double radius;
    private final double interval;


    public Ripple(Location centerLoc, double radius, double interval) {
        this.centerLoc = centerLoc;
        this.radius = radius;
        this.interval = interval;
    }

    @Override
    public void run() {
        World world = centerLoc.getWorld();
        double x = centerLoc.getX();
        double y = centerLoc.getY();
        double z = centerLoc.getZ();

        for (double theta = 0; theta <= 2 * Math.PI; theta += interval) {
            double dx = radius * Math.cos(theta);
            double dz = radius * Math.sin(theta);

            Location newLoc = new Location(world, x + dx, y, z + dz);
            Location newLoc1 = new Location(world, x + dx, y + 1, z + dz);

            FallingBlock fallingBlock = (FallingBlock) world.spawnEntity(newLoc, EntityType.FALLING_BLOCK);
            FallingBlock fallingBlock1 = (FallingBlock) world.spawnEntity(newLoc1, EntityType.FALLING_BLOCK);
            fallingBlock.setVelocity(new Vector(0, 0.2, 0));
            fallingBlock1.setVelocity(new Vector(0, 0.2, 0));
        }

        radius += interval;
        if (radius > 10) { // Adjust the maximum radius as needed
            this.cancel(); // Stop the ripple effect
        }
    }
}
