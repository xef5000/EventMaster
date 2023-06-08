package com.xef5000.EventMaster.Commands;

import com.xef5000.EventMaster.ListManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCommandTabCompleter implements TabCompleter {
    private ListManager listManager;

    public MainCommandTabCompleter(ListManager listManager) {
        this.listManager = listManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> outputs = new ArrayList<>();
        if (args.length == 0) {
            outputs.add("help");
            outputs.add("event");
            StringUtil.copyPartialMatches(args[0], outputs, completions);
            Collections.sort(completions);
            return completions;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("event")) {
                completions.add("meteorite");
                Collections.sort(completions);
                return completions;
            }
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("meteorite")) {
                completions.add("createlist");
                completions.add("list");
                Collections.sort(completions);
                return completions;
            }
        }
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("createlist")) {
                return completions;
            }
            if (args[2].equalsIgnoreCase("list")) {
                completions.addAll(listManager.lists.keySet());
                Collections.sort(completions);
                return completions;
            }
        }
        if (args.length == 4) {
            if (args[2].equalsIgnoreCase("list")) {
                completions.add("add");
                completions.add("remove");
                completions.add("delete");
                completions.add("edit");
                Collections.sort(completions);
                return completions;
            }
        }

        return null;
    }
}
