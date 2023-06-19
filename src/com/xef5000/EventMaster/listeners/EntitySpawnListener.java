package com.xef5000.EventMaster.listeners;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event) {
        if (event.getEntity() instanceof FallingBlock) System.out.println("FALLING BLODCKKS");
        if (event.getEntityType() == EntityType.FALLING_BLOCK) System.out.println("FALLING BLODCK");
        if (!(event.getEntityType() == EntityType.FALLING_BLOCK && getNMSName(event).equalsIgnoreCase("eventmaster-meteorite-shockwave")) ) return;
        FallingBlock entity = (FallingBlock) event.getEntity();
        entity.getLocation().getBlock().setType(entity.getMaterial());
        System.out.println(entity.getMaterial().toString());
    }

    private String getNMSName(EntitySpawnEvent event) {
        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        CraftEntity craftEntity = (CraftEntity) fallingBlock;
        net.minecraft.server.v1_8_R3.Entity nmsEntity = craftEntity.getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        nmsEntity.e(nbt); // Save entity data to NBT compound
        return nbt.getString("CustomName");
    }
}
