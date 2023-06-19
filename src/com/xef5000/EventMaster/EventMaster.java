package com.xef5000.EventMaster;

import com.google.gson.JsonArray;
import com.xef5000.EventMaster.Commands.MainCommand;
import com.xef5000.EventMaster.Listeners.EntityChangeBlockEventListener;
import com.xef5000.EventMaster.Listeners.EntitySpawnListener;
import com.xef5000.EventMaster.Listeners.RightClickListener;
import com.xef5000.EventMaster.Utils.Managers.ListManager;
import com.xef5000.EventMaster.Utils.Managers.MeteoriteManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class EventMaster extends JavaPlugin {

    public static String PREFIX = "[EventMaster]";
    public static String COLOR_PREFIX;
    public ListManager listManager = new ListManager(this);
    public MeteoriteManager meteoriteManager = new MeteoriteManager(this);

    @Override
    public void onEnable() {

        saveDefaultConfig();
        COLOR_PREFIX = getConfig().getString("prefix").replace("&", "§");

        System.out.println(PREFIX + " EventMaster has been started");
        getCommand("eventmaster").setExecutor(new MainCommand(listManager, this));

        listManager.loadFiles();

        listManager.createCustomList("ExampleList", new JsonArray());
        listManager.addCoordinateToCustomList("ExampleList", new Location(Bukkit.getWorld("world"), 10, 20, 31));

        meteoriteManager.loadDataFile();

        getServer().getPluginManager().registerEvents(new EntityChangeBlockEventListener(this), this);
        getServer().getPluginManager().registerEvents(new RightClickListener(this), this);
        getServer().getPluginManager().registerEvents(new EntitySpawnListener(), this);

    }

    @Override
    public void onDisable() {
        listManager.loadFiles();
        listManager.save();

        meteoriteManager.flushMemoryToJson();
    }

}

