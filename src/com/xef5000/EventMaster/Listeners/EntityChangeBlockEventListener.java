package com.xef5000.EventMaster.Listeners;

import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.Utils.Hologram;
import com.xef5000.EventMaster.Utils.Shockwave.Ripple;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlockEventListener implements Listener {

    private EventMaster main;

    public EntityChangeBlockEventListener(EventMaster eventMaster) {
        this.main = eventMaster;
    }

    // For meteorite events
    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (!(event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().getCustomName().startsWith("eventmaster-meteorite-internal"))) return;
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

        if (shockwave) {
            Ripple rippleEffect = new Ripple(main, loc.clone().add(0, -1, 0), 7.0, 3.5, 0.15, 2);
            rippleEffect.runTaskTimer(main, 10L, 1L);
        }

        if (lightning)
            loc.getWorld().strikeLightningEffect(loc);

        if (hologram) {
            Hologram holograma = new Hologram(hologramString.toString().replace("&", "§"), loc.clone().add(0.5, -0.6, 0.5));
            holograma.spawn();
        }

    }
}
