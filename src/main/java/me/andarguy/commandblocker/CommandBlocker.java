package me.andarguy.commandblocker;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.*;

public final class CommandBlocker extends JavaPlugin implements Listener {

    private FileConfiguration configuration;
    private List<CommandGroup> commandGroups;

    @Override
    public void onEnable() {
        configSetup();
        loadCommandGroups();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onPlayerCmd(PlayerCommandPreprocessEvent e) {
        Set<String> allowedCommands = getAllowedCommands(e.getPlayer());
        String command = e.getMessage().split(" ")[0].substring(1);
        if (!allowedCommands.contains(command)) {
            e.getPlayer().sendMessage(Objects.requireNonNull(this.configuration.getString("msg.restrictedCommand")));
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerCmdSend(PlayerCommandSendEvent e) {
        Set<String> allowedCommands = getAllowedCommands(e.getPlayer());
        e.getCommands().removeIf(o -> !allowedCommands.contains(o));
    }


    private void configSetup() {
        if (!Path.of(this.getDataFolder().getPath(), "config.yml").toFile().exists())
            this.saveResource("config.yml", false);
        this.configuration = getConfig();
        this.configuration.options().copyDefaults(true);
        this.saveConfig();
    }

    private void loadCommandGroups() {
        commandGroups = new ArrayList<>();
        for (String group : Objects.requireNonNull(this.configuration.getConfigurationSection("groups")).getKeys(false))
            commandGroups.add(new CommandGroup(group, this.configuration.getString("groups." + group + ".permission"), this.configuration.getStringList("groups." + group + ".commands")));
    }

    private Set<String> getAllowedCommands(Player p) {
        Set<String> allowedCommands = new HashSet<>();
        for (CommandGroup commandGroup : commandGroups) {
            String permission = commandGroup.getPermission();
            List<String> commands = commandGroup.getCommands();
            if (p.hasPermission(permission)) allowedCommands.addAll(commands);
        }
        return allowedCommands;
    }
}
