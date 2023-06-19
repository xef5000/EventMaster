package com.xef5000.EventMaster.Commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.Events.Meteorite;
import com.xef5000.EventMaster.Utils.Managers.ListManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class MainCommand implements CommandExecutor {
    public ListManager listManager;
    public EventMaster eventMaster;
    public ArrayList<String> blacklistedListNames = new ArrayList<String>() {{
        // ONLY USE LOWER CASE LETTERS.
        add("default");
    }};

    public MainCommand(ListManager listManager, EventMaster eventMaster) {
        this.listManager = listManager;
        this.eventMaster = eventMaster;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cYou must be a player to run this command!");
            return false;
        }

        if ((args.length == 1 && args[0].equalsIgnoreCase("help") ) || !(args.length >= 1)) {
            sendHelpMessage(commandSender);
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("event")) {
            commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cPossible arguments: meteorite");
            return true;
        } else if (args.length > 1) {
            if (args.length == 2 && args[0].equalsIgnoreCase("event") && args[1].equalsIgnoreCase("meteorite")) {
                // Help
                commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cPossible arguments: createlist, list, lists, admin");

                return true;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("event") && args[1].equalsIgnoreCase("meteorite") && args[2].equalsIgnoreCase("admin")) {
                //EVENT METEORITE - admin event
                World world = ((Player) commandSender).getWorld();
                Location location = ((Player) commandSender).getTargetBlock((Set< Material >) null, 100).getLocation();
                location.setY(location.getY() + 1);
                Meteorite meteorite = new Meteorite(location, eventMaster);
                meteorite.sendMeteorite();


                //Meteorite.sendMeteorite(location, eventMaster, eventMaster.getConfig().getBoolean("meteorite-shockwave"), eventMaster.getConfig().getBoolean("meteorite-lightning"), eventMaster.getConfig().getBoolean("meteorite-hologram"), true);
                Bukkit.broadcastMessage(EventMaster.COLOR_PREFIX + " §cEvent §eMétéorite at §f" + location.getX() + "§c, §f" + location.getY() + "§c, §f" + location.getZ() + " §c!");
                return true;
            }
            if (args[0].equalsIgnoreCase("event") && args[1].equalsIgnoreCase("meteorite") && args[2].equalsIgnoreCase("createlist")) {
                //EVENT METEORITE - createlist
                if (args.length == 3) {
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cUsage: /eventmaster event meteorite createlist <name>");
                    return false;
                }
                if (listManager.lists.containsKey(args[3])) {
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cERROR: This list already exists! §f" + args[3]);
                    return false;
                }
                if (blacklistedListNames.contains(args[3].toLowerCase())) {
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cERROR: You cannot name a list this way! §f" + args[3]);
                    return false;
                }
                listManager.createCustomList(args[3], new JsonArray());
                createDefaultListSettings(args[3]);
                commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §aSuccessfuly created a the list §f" + args[3]);
                return true;
            }
            if (args[0].equalsIgnoreCase("event") && args[1].equalsIgnoreCase("meteorite") && args[2].equalsIgnoreCase("list")) {
                if (args.length <= 4) {
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cUsage: /eventmaster event meteorite list <name> <add/remove/delete/start/edit>");
                    commandSender.sendMessage("§7- §cadd: §7add the block you are looking at to the list");
                    commandSender.sendMessage("§7- §cremove: §7removes the block you are looking at from the list");
                    commandSender.sendMessage("§7- §cdelete: §7deletes the entire list §4(PERMANENT)");
                    commandSender.sendMessage("§7- §cstart: §7start an event with that list");
                    commandSender.sendMessage("§7- §b(BETA) §cedit : §7toggle edit mode for that list");
                    return false;
                }
                String listName = args[3];
                String action = args[4];

                if (!listManager.lists.containsKey(listName)) {
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cERROR: This list does not exist! §f" + listName);
                    return false;
                }


                if (action.equalsIgnoreCase("add")) {
                    Location location = ((Player) commandSender).getTargetBlock((Set< Material >) null, 100).getLocation();
                    for (JsonElement element : listManager.lists.get(listName)) {
                        if (!element.isJsonArray()) continue;
                        JsonArray coords = element.getAsJsonArray();
                        int x = coords.get(0).getAsInt();
                        int y = coords.get(1).getAsInt() - 1;
                        int z = coords.get(2).getAsInt();
                        if (location.getX() == x && location.getY() == y && location.getZ() == z) {
                            commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cERROR: This coordinate is already in the list §f" + listName);
                            return false;
                        }
                    }
                    location.setY(location.getY() + 1);
                    listManager.addCoordinateToCustomList(listName, location);
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §aSuccessfully added §f" + location.getX() + "§a, §f" + location.getY() + "§a, §f" + location.getZ() + "§a to§f " + listName);
                    return true;
                }
                if (action.equalsIgnoreCase("remove")) {
                    boolean isAlreadyInList = false;
                    Location location = ((Player) commandSender).getTargetBlock((Set< Material >) null, 100).getLocation();
                    for (JsonElement element : listManager.lists.get(listName)) {
                        JsonArray coords = element.getAsJsonArray();
                        int x = coords.get(0).getAsInt();
                        int y = coords.get(1).getAsInt() - 1;
                        int z = coords.get(2).getAsInt();
                        if (location.getX() == x && location.getY() == y && location.getZ() == z) {
                            isAlreadyInList = true;
                            break;
                        }
                    }
                    if (!isAlreadyInList) {
                        commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cERROR: This coordinate is not in the list §f" + listName);
                        return false;
                    }
                    listManager.removeCoordinateFromCustomList(listName, location);
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §aSuccessfully removed §f" + location.getX() + "§a, §f" + location.getY() + "§a, §f" + location.getZ() + "§a from§f " + listName);
                }
                if (action.equalsIgnoreCase("delete")) {
                    listManager.deleteCustomList(listName);
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §aSuccessfully removed the list §f" + listName);
                    return true;
                }
                if (action.equalsIgnoreCase("start")) {
                    System.out.println("Trying to start with the list " + listName);
                    if (!listManager.lists.containsKey(listName)) {
                        commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cERROR: This list does not exist! §f" + listName);
                        return false;
                    }
                    Meteorite meteorite = new Meteorite(listName, eventMaster);
                    meteorite.sendMeteorites();
                    eventMaster.meteoriteManager.addMeteorite(meteorite);
                    //Meteorite.startEvent(listManager, listName);
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("event") && args[1].equalsIgnoreCase("meteorite") && args[2].equalsIgnoreCase("lists")) {
                if (listManager.lists.size() < 1) {
                    commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §cERROR: There are no existing list. Create one using /eventmaster event meteorite createlist <name>");
                    return false;
                }

                StringBuilder sb = new StringBuilder();
                for (String name : listManager.lists.keySet()) {
                    sb.append(name).append(", ");
                }
                sb.replace(sb.length()-2, sb.length()-1, "");
                commandSender.sendMessage(EventMaster.COLOR_PREFIX + " §7" + sb);
                return true;
            }

        }


        //commandSender.sendMessage("Uwu ça fonctionne §9uwu");
        return false;
    }

    private void sendHelpMessage(CommandSender player) {
        player.sendMessage("§f---------------");
        player.sendMessage("§6/eventmaster help §f- §7Shows this help menu");
        player.sendMessage("§6/eventmaster event §f- §7Lists all possible events");
        player.sendMessage("§f---------------");
    }

    private void createDefaultListSettings(String listName) {
        eventMaster.getConfig().set("meteorite-lists." + listName + ".meteorite-hologram-text", "&dMeteorite");
        eventMaster.getConfig().set("meteorite-lists." + listName + ".meteorite-block", "OBSIDIAN");
        eventMaster.getConfig().set("meteorite-lists." + listName + ".meteorite-hologram", true);
        eventMaster.getConfig().set("meteorite-lists." + listName + ".meteorite-lightning", true);
        eventMaster.getConfig().set("meteorite-lists." + listName + ".meteorite-shockwave", false);
        eventMaster.getConfig().set("meteorite-lists." + listName + ".meteorite-fall", false);
        eventMaster.getConfig().set("meteorite-lists." + listName + ".percent-land", 100);
        eventMaster.getConfig().set("meteorite-lists." + listName + ".loot", new ArrayList<>(Arrays.asList("10:1-2:DIAMOND_ORE", "75:10-16:DIRT")));
        eventMaster.saveConfig();
    }
}
