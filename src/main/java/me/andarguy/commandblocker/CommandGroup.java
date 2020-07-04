package me.andarguy.commandblocker;

import java.util.List;

public class CommandGroup {
    private final String name;
    private final String permission;
    private final List<String> commands;

    public CommandGroup(String name, String permission, List<String> commands) {
        this.name = name;
        this.permission = permission;
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getCommands() {
        return commands;
    }
}
