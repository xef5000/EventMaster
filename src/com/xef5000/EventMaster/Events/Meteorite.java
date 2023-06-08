package com.xef5000.EventMaster.Events;

import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.Utils.Hologram;
import com.xef5000.EventMaster.Utils.Shockwave.Ripple;
import com.xef5000.EventMaster.Utils.Shockwave.ShockwaveHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Meteorite {

    public static void sendMeteorite(Location blockPosition, EventMaster eventMaster) {
        World world = blockPosition.getWorld();
        if (eventMaster.getConfig().getBoolean("meteorite-shockwave")) {
            //ShockwaveHandler sh = new ShockwaveHandler(eventMaster, 5);
           // sh.createShockwave(blockPosition.clone().add(0, -1, 0));

            Ripple rippleEffect = new Ripple(blockPosition.clone().add(0, -1, 0), 3.0, 0.15);
            rippleEffect.runTaskTimer(eventMaster, 10L, 1L);
        }

        world.getBlockAt(blockPosition).setType(Material.valueOf(eventMaster.getConfig().getString("meteorite-block")));
        if (eventMaster.getConfig().getBoolean("meteorite-lightning")) world.strikeLightningEffect(blockPosition);

        if (eventMaster.getConfig().getBoolean("meteorite-hologram")) {
            Hologram hologram = new Hologram(eventMaster.getConfig().getString("meteorite-hologram-text").replace("&", "§"), blockPosition.clone().add(0.5, -0.6, 0.5));
            hologram.spawn();
        }
    }


}
