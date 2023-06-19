package com.xef5000.EventMaster.Listeners;

import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.Events.Meteorite;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RightClickListener implements Listener {

    public EventMaster eventMaster;

    public RightClickListener(EventMaster eventMaster) {
        this.eventMaster = eventMaster;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        Location location = null;
        String listName = null;
        bigloop:
        for (String name : eventMaster.meteoriteManager.getActiveMeteorites().keySet()) {
            for (Location loc : eventMaster.meteoriteManager.getActiveMeteorites().get(name)) {
                if (loc.getWorld() == event.getClickedBlock().getWorld() && loc.getBlockX() == event.getClickedBlock().getLocation().getBlockX() && loc.getBlockY() == event.getClickedBlock().getLocation().getBlockY() && loc.getBlockZ() == event.getClickedBlock().getLocation().getBlockZ()) {
                    listName = name;
                    location = loc.clone();
                    eventMaster.meteoriteManager.removeLocation(name, location);
                    break bigloop;
                }
            }
        }
        if (location == null) return;
        assert listName != null;
        // When player right clicks block, and it's a meteorite, from now on we will have the correct listname of that block.
        event.getPlayer().sendMessage("You clicked a meteorite of the list " + listName);


    }

}
