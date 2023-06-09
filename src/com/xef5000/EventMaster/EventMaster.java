package com.xef5000.EventMaster;

import com.google.gson.JsonArray;
import com.xef5000.EventMaster.Commands.MainCommand;
import com.xef5000.EventMaster.Commands.MainCommandTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class EventMaster extends JavaPlugin {

    public static String PREFIX = "[EventMaster]";
    public static String COLOR_PREFIX;
    public ListManager listManager = new ListManager(this);

    @Override
    public void onEnable() {

        saveDefaultConfig();
        COLOR_PREFIX = getConfig().getString("prefix").replace("&", "§");

        System.out.println(PREFIX + " EventMaster has been started");
        getCommand("eventmaster").setExecutor(new MainCommand(listManager, this));
        getCommand("eventmaster").setTabCompleter(new MainCommandTabCompleter(listManager));

        listManager.createCustomList("ExampleList", new JsonArray());
        listManager.addCoordinateToCustomList("ExampleList", new Location(Bukkit.getWorld("world"), 10, 20, 31));
    }

}

