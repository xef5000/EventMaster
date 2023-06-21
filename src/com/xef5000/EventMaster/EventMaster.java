package com.xef5000.EventMaster;

import com.google.gson.JsonArray;
import com.xef5000.EventMaster.commands.MainCommand;
import com.xef5000.EventMaster.events.MeteoriteEventScheduler;
import com.xef5000.EventMaster.listeners.EntityChangeBlockEventListener;
import com.xef5000.EventMaster.listeners.EntitySpawnListener;
import com.xef5000.EventMaster.listeners.RightClickListener;
import com.xef5000.EventMaster.utils.language.Lang;
import com.xef5000.EventMaster.utils.managers.ListManager;
import com.xef5000.EventMaster.utils.managers.MeteoriteManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventMaster extends JavaPlugin {

    public static String PREFIX = "[EventMaster]";
    public static String COLOR_PREFIX;
    public ListManager listManager = new ListManager(this);
    public MeteoriteManager meteoriteManager = new MeteoriteManager(this);
    public static YamlConfiguration LANG;
    public static File LANG_FILE;
    public static Logger log;
    private ArrayList<Integer> taskIDSchedulers = new ArrayList<>();

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

        log = getLogger();
        loadLang();
        restartSchedulers();
    }

    @Override
    public void onDisable() {
        listManager.loadFiles();
        listManager.save();

        meteoriteManager.flushMemoryToJson();
    }

    public void softReload() {
        // No need to flush memory or read files, just reloading the basics
        reloadConfig();
        loadLang();
        restartSchedulers();
        COLOR_PREFIX = getConfig().getString("prefix").replace("&", "§");
    }


    public void restartSchedulers() {
        for (Integer taskID : taskIDSchedulers) {
            Bukkit.getScheduler().cancelTask(taskID);
        }

        for (String list : listManager.lists.keySet()) {
            if (!getConfig().contains("meteorite-lists." + list + ".schedule") || !getConfig().getBoolean("meteorite-lists." + list + ".schedule")) continue;
            int delayInMinutes = getConfig().getInt("meteorite-lists." + list + ".schedule-delay");
            MeteoriteEventScheduler scheduler = new MeteoriteEventScheduler(list, this);
            taskIDSchedulers.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, scheduler, 20L * 60 * delayInMinutes, 20L * 60 * delayInMinutes));
            System.out.println("Starting scheduler for list " + list + " at intervals of "+delayInMinutes+" minutes");
        }

    }

    /**
     * Load the lang.yml file.
     * @return The lang.yml config.
     */
    public void loadLang() {
        File lang = new File(getDataFolder(), "lang.yml");
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(lang);
        if (!lang.exists()) {
            try {
                langConfig.save(lang);
            } catch(IOException e) {
                e.printStackTrace(); // So they notice
                log.severe("Couldn't create language file.");
                log.severe("This is a fatal error. Now disabling");
                this.setEnabled(false); // Without it loaded, we can't send the messages
            }
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for(Lang item:Lang.values()) {
            if (conf.getString(item.getPath()) == null) {
                conf.set(item.getPath(), item.getDefault());
            }
        }
        Lang.setFile(conf);
        EventMaster.LANG = conf;
        EventMaster.LANG_FILE = lang;
        try {
            conf.save(getLangFile());
        } catch(IOException e) {
            log.log(Level.WARNING, "PluginName: Failed to save lang.yml.");
            log.log(Level.WARNING, "PluginName: Report this stack trace to <your name>.");
            e.printStackTrace();
        }
    }

    /**
     * Gets the lang.yml config.
     * @return The lang.yml config.
     */
    public YamlConfiguration getLang() {
        return LANG;
    }

    /**
     * Get the lang.yml file.
     * @return The lang.yml file.
     */
    public File getLangFile() {
        return LANG_FILE;
    }

}

