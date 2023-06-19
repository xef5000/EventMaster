package com.xef5000.EventMaster.listeners;

import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.utils.Hologram;
import com.xef5000.EventMaster.utils.shockwave.Ripple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;

public class EntityChangeBlockEventListener implements Listener {

    private EventMaster main;

    public EntityChangeBlockEventListener(EventMaster eventMaster) {
        this.main = eventMaster;
    }

    // For meteorite events
    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equalsIgnoreCase("eventmaster-meteorite-shockwave")) {
            System.out.println(event.getTo() + " -> " + ((FallingBlock)event.getEntity()).getMaterial());
            if (event.getTo() == Material.AIR) {
                event.getBlock().setType(Material.STONE);

            } else {
                event.getEntity().remove();
                Location loc = event.getBlock().getLocation();
                int[] origloc = getNMSOrigLoc(event);
                if (loc.getBlockX() == origloc[0] && loc.getBlockY() == origloc[1] && loc.getBlockZ() == origloc[2]) {
                    //Block landed at same position it started from
                } else {
                    //Block landed at other random spot
                    Location origLoc = new Location(loc.getWorld(), origloc[0], origloc[1], origloc[2]);
                    origLoc.getBlock().setType(((FallingBlock) event.getEntity()).getMaterial());
                    event.setCancelled(true);
                }

            }

            return;
        }
        if (!((event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().getCustomName() != null) &&  (event.getEntity().getCustomName().startsWith("eventmaster-meteorite-internal") || event.getEntity().getCustomName().startsWith("eventmaster-meteorite-internallist")) )) return;
        // eventmaster-meteorite-internal-true-true-true-Text
        // just-an-id-SHOCKWAVE-LIGHTNING-HOLOGRAM-HOLOGRAM_TEXT
        String[] values = event.getEntity().getCustomName().split("-");
        StringBuilder hologramString = new StringBuilder();
        for (int i = 6; i < values.length; i++) {
            hologramString.append(values[i]);
        }
        boolean shockwave = Boolean.parseBoolean(values[3]);
        boolean lightning = Boolean.parseBoolean(values[4]);
        boolean hologram = Boolean.parseBoolean(values[5]);
        Location loc = event.getBlock().getLocation();
        String listName = (event.getEntity().getCustomName().startsWith("eventmaster-meteorite-internallist")) ? values[2].split(":")[1] : null;

        if (shockwave) {
            Ripple rippleEffect = new Ripple(main, loc.clone().add(0, -1, 0), 7.0, 3.5, 0.15, 2);
            rippleEffect.runTaskTimer(main, 10L, 1L);
        }

        if (lightning)
            loc.getWorld().strikeLightningEffect(loc);

        if (hologram) {
            Hologram holograma = new Hologram(hologramString.toString().replace("&", "§"), loc.clone().add(0.5, -0.6, 0.5), listName);
            holograma.spawn();
        }

        if (event.getEntity().getCustomName().startsWith("eventmaster-meteorite-internallist")) {
            // here we will need to store the meteorite's position in a permanent config file that goes through restarts

            event.getBlock().setMetadata("list", new FixedMetadataValue(main, listName));
        }

    }

    private int[] getNMSOrigLoc(EntityChangeBlockEvent event) {
        //FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        //CraftEntity craftEntity = (CraftEntity) fallingBlock;
        //net.minecraft.server.v1_8_R3.Entity nmsEntity = craftEntity.getHandle();
        //NBTTagCompound nbt = new NBTTagCompound();
        //nmsEntity.e(nbt); // Save entity data to NBT compound
        //System.out.println("NBT: " + nbt);
        System.out.println(Arrays.toString(((int[]) event.getEntity().getMetadata("OrigPos").get(0).value())));
        //return nbt.getIntArray("OrigBlock");
        return ((int[]) event.getEntity().getMetadata("OrigPos").get(0).value());
    }


}
