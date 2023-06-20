package com.xef5000.EventMaster.listeners;

import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.events.Meteorite;
import com.xef5000.EventMaster.utils.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class RightClickListener implements Listener {

    public EventMaster eventMaster;

    public RightClickListener(EventMaster eventMaster) {
        this.eventMaster = eventMaster;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
            Location location = null;
            String listName = null;
            Meteorite meteorite = null;

            System.out.println(eventMaster.meteoriteManager.getActiveMeteorites().keySet());

            bigloop:
            for (String name : eventMaster.meteoriteManager.getActiveMeteorites().keySet()) {
                for (Location loc : eventMaster.meteoriteManager.getActiveMeteorites().get(name)) {
                    if (loc.getWorld() == event.getClickedBlock().getWorld() &&
                            loc.getBlockX() == event.getClickedBlock().getLocation().getBlockX() &&
                            loc.getBlockY() == event.getClickedBlock().getLocation().getBlockY() &&
                            loc.getBlockZ() == event.getClickedBlock().getLocation().getBlockZ()) {
                        listName = name;
                        location = loc.clone();
                        meteoriteEventLoop:
                        for (Meteorite meteoriteEvent : eventMaster.meteoriteManager.getMeteorites()) {
                            for (Location locc : meteoriteEvent.getLocations()) {
                                if (locc.getBlockX() == location.getBlockX() && locc.getBlockY() == location.getBlockY() && locc.getBlockZ() == location.getBlockZ()) {
                                    //System.out.println("We found the corresponding event");
                                    meteorite = meteoriteEvent;
                                    break meteoriteEventLoop;
                                }
                            }
                        }
                        eventMaster.meteoriteManager.removeLocation(name, location);
                        break bigloop;
                    }
                }
            }
            System.out.println("Is location null? " + (location==null));
            if (location == null) return;
            assert listName != null;
            // When player right clicks block, and it's a meteorite, from now on we will have the correct listname of that block.
            event.getPlayer().sendMessage("You clicked a meteorite of the list " + listName);
            if (meteorite != null) {
                for (Hologram hologram : meteorite.getHolograms()) {
                    if (hologram.getLocation().getX() == (location.getX() + 0.5f) && hologram.getLocation().getY() == (location.getY() - 0.6) && hologram.getLocation().getZ() == (location.getZ() + 0.5f)) {
                        //loc.clone().add(0.5, -0.6, 0.5))
                        hologram.despawn();
                        //System.out.println("DESPAWNED - found the meteorite event");
                        giveMeteoriteRewards(event.getPlayer(), listName);
                        meteorite.getHolograms().remove(hologram);
                        meteorite.getLocations().remove(location);
                        break;
                    }
                }
            } else {
                for (Location loc : eventMaster.meteoriteManager.getLocationHologramHashMap().keySet()) {
                    Hologram hologram = eventMaster.meteoriteManager.getLocationHologramHashMap().get(loc);
                    if ((hologram.getLocation().getX() == (location.getX() + 0.5f) &&
                            hologram.getLocation().getY() == (location.getY() - 0.6) &&
                            hologram.getLocation().getZ() == (location.getZ() + 0.5f))) {
                        hologram.despawn();
                        eventMaster.meteoriteManager.getLocationHologramHashMap().remove(loc);
                        eventMaster.meteoriteManager.deserializedHolograms.remove(hologram);
                        giveMeteoriteRewards(event.getPlayer(), listName);
                        //System.out.println("DESPAWNED - matched with the list");
                        break;
                    }
                }
            }

            event.getClickedBlock().setType(Material.AIR);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void giveMeteoriteRewards(Player player, String listName) {
        ArrayList<String> lootList = (ArrayList<String>) eventMaster.getConfig().getList("meteorite-lists." + listName + ".loot");
        ArrayList<String> cmdList = (ArrayList<String>) eventMaster.getConfig().getList("meteorite-lists." + listName + ".commands");

        // give items reward
        for (String line : lootList) {
            String[] arguments = line.split(":");
            int randomInt = new SplittableRandom().nextInt(1, 101);
            int percent = Integer.parseInt(arguments[0]);
            if (!(randomInt <= percent)) continue;

            int amtLow = Integer.parseInt(arguments[1].split("-")[0]);
            int amtHigh = Integer.parseInt(arguments[1].split("-")[1]);
            int amt = new SplittableRandom().nextInt(amtLow, amtHigh + 1);

            ItemStack is = new ItemStack(Material.valueOf(arguments[2]), amt);
            player.getInventory().addItem(is);
        }

        // execute commands
        for (String line : cmdList) {
            int percent = Integer.parseInt(line.split(":")[0]);
            int randomInt = new SplittableRandom().nextInt(1, 101);
            if (!(randomInt <= percent)) continue;

            eventMaster.getServer().dispatchCommand(eventMaster.getServer().getConsoleSender(), (line.substring(line.indexOf(":") + 1).trim()));
        }
    }
}
