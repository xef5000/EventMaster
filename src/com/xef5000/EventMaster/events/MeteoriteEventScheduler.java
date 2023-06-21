package com.xef5000.EventMaster.events;

import com.xef5000.EventMaster.EventMaster;
import com.xef5000.EventMaster.utils.language.Lang;
import com.xef5000.EventMaster.utils.managers.ListManager;
import org.bukkit.Bukkit;

public class MeteoriteEventScheduler implements Runnable{

    private final String listName;
    private final EventMaster eventMaster;

    public MeteoriteEventScheduler(String listName, EventMaster eventMaster) {
        this.listName = listName;
        this.eventMaster = eventMaster;
    }

    @Override
    public void run() {
        ListManager listManager = eventMaster.listManager;
        System.out.println("Trying to start with the list " + listName);
        if (!listManager.lists.containsKey(listName))
            throw new IllegalArgumentException("This list does not exist!");

        Meteorite meteorite = new Meteorite(listName, eventMaster);
        meteorite.sendMeteorites();
        eventMaster.meteoriteManager.addMeteorite(meteorite);
        Bukkit.broadcastMessage(EventMaster.COLOR_PREFIX + " " + Lang.METEORITE_EVENT_LIST.toString().replace("%list", listName));
    }
}
