package me.hsgamer.broadcastcommands;

import me.hsgamer.broadcastcommands.commands.CommandType;
import me.hsgamer.broadcastcommands.commands.Commands;
import me.hsgamer.broadcastcommands.commands.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class BroadcastCommands extends JavaPlugin {
    private static BroadcastCommands instance;
    private static List<String> registered = new ArrayList<>();

    public static BroadcastCommands getInstance() {
        return instance;
    }

    public static List<String> getRegistered() {
        return registered;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.getConfig().options().copyHeader(true);
        saveDefaultConfig();
        for (String string : getConfig().getStringList("register-commands")) {
            List<String> messages = colorize(getConfig().getStringList("commands." + string + ".text"));
            CommandType type = getCommandType(getConfig().getString("commands." + string + ".send-to").toLowerCase());
            String permission = getConfig().getString("commands." + string + ".permission", null);
            String receiverPermission = (type == CommandType.PERMISSION) ? (getConfig().getString("commands." + string + ".send-to").split(":"))[1] : null;
            if (register(string, messages, type, permission, receiverPermission)) {
                registered.add(string);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + string + " successfully registered");
            }
        }
        getCommand("broadcastcommands").setExecutor(new PluginCommand());
    }

    @Override
    public void onDisable() {
        instance = null;
        registered.clear();
    }

    private List<String> colorize(List<String> input) {
        List<String> output = new ArrayList<>();
        input.forEach((string) -> output.add(ChatColor.translateAlternateColorCodes('&', string)));
        return output;
    }

    private CommandType getCommandType(String string) {
        if (string.startsWith("everyone")) return CommandType.EVERYONE;
        if (string.startsWith("op")) return CommandType.OP;
        if (string.startsWith("permission")) return CommandType.PERMISSION;
        if (string.startsWith("me")) return CommandType.ME;
        return null;
    }

    private boolean register(String name, List<String> messages, CommandType type, String permission, String receiverPermission) {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register("broadcastcommands", new Commands(name, messages, type, permission, receiverPermission));
            return true;
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error when registering " + name);
            e.printStackTrace();
            return false;
        }
    }
}
