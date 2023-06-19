package com.xef5000.EventMaster.Utils.Shockwave;

import com.xef5000.EventMaster.EventMaster;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Ripple extends BukkitRunnable {

    private final Location centerLoc;
    private double radiusStart;
    private double radius;
    private final double interval;
    private double height;
    private List<BlockState> blocks;
    private EventMaster plugin;


    public Ripple(EventMaster plugin, Location centerLoc, double radius, double radiusStart, double interval, double height) {
        this.plugin = plugin;
        this.centerLoc = centerLoc;
        this.radiusStart = radiusStart;
        this.radius = radius;
        this.interval = interval;
        this.height = height;
        this.blocks = getTotalBlocks(centerLoc);
    }

    @Override
    public void run() {
        World world = centerLoc.getWorld();
        double x = centerLoc.getBlockX();
        double y = centerLoc.getBlockY();
        double z = centerLoc.getBlockZ();

        for (double theta = 0; theta <= 2 * Math.PI; theta += interval) {
            double dx = radiusStart * Math.cos(theta);
            double dz = radiusStart * Math.sin(theta);

            for (int i = 0; i < height; i++) {
                Location newLoc = new Location(world, x + dx, y + i, z + dz);
                FallingBlock fallingBlock = (FallingBlock) world.spawnEntity(newLoc, EntityType.FALLING_BLOCK);
                fallingBlock.setMetadata("OrigPos", new FixedMetadataValue(plugin, new int[]{newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ()}));


                fallingBlock.setVelocity(new Vector(0, 0.2, 0));
                fallingBlock.setDropItem(false);
                fallingBlock.setCustomName("eventmaster-meteorite-shockwave");
            }


            //Location newLoc1 = new Location(world, x + dx, y + 1, z + dz);
            //FallingBlock fallingBlock1 = (FallingBlock) world.spawnEntity(newLoc1, EntityType.FALLING_BLOCK);
            //fallingBlock1.setVelocity(new Vector(0, 0.2, 0));
        }

        radiusStart += interval;
        if (radiusStart >= radius) { // Adjust the maximum radius as needed
            this.cancel(); // Stop the ripple effect
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new BlockRebuilder(blocks), 100L);
        }
    }

    private List<BlockState> getTotalBlocks(final Location loc) {
        final List<BlockState> blocks = new ArrayList<BlockState>();
        final World world = loc.getWorld();
        final int lx = loc.getBlockX();
        final int ly = loc.getBlockY();
        final int lz = loc.getBlockZ();
        BlockState bs;
        Vector blockLocation;
        final Vector measureVector = loc.toVector();

        blocks.add(world.getBlockAt(loc).getState());
        for(int y = 0; y<=height; y++) {
            for(double x = -radius; x<radius; x++) {
                for(double z = -radius; z<radius; z++) {
                    blockLocation = new Vector(lx+x, ly, lz+z);
                    if(measureVector.distance(blockLocation) < radius) {
                        bs = world.getBlockAt((int)(lx+x), ly+y, (int)(lz+z)).getState();
                        if(bs.getType() != Material.AIR) {
                            blocks.add(bs);
                        }
                    }
                }
            }
        }
        return blocks;
    }
}
