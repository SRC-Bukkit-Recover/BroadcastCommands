package me.hsgamer.broadcastcommands;

import me.hsgamer.broadcastcommands.commands.CommandType;
import me.hsgamer.broadcastcommands.commands.Commands;
import me.hsgamer.broadcastcommands.commands.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BroadcastCommands extends JavaPlugin {
    private static Field KNOWN_COMMANDS_FIELD;
    private static CommandMap BUKKIT_COMMAND_MAP;
    private static BroadcastCommands instance;
    private static HashMap<String, Commands> registered = new HashMap<>();

    public static BroadcastCommands getInstance() {
        return instance;
    }

    public static HashMap<String, Commands> getRegistered() {
        return registered;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.getConfig().options().copyHeader(true);
        saveDefaultConfig();
        registerCommandMap();
        for (String string : getConfig().getStringList("register-commands")) {
            List<String> messages = colorize(getConfig().getStringList("commands." + string + ".text"));
            CommandType type = getCommandType(getConfig().getString("commands." + string + ".send-to").toLowerCase());
            String permission = getConfig().getString("commands." + string + ".permission", null);
            String receiverPermission = (type == CommandType.PERMISSION) ? (getConfig().getString("commands." + string + ".send-to").split(":"))[1] : null;
            String world = (type == CommandType.WORLD) ? (getConfig().getString("commands." + string + ".send-to").split(":"))[1] : null;
            register(string, messages, type, permission, receiverPermission, world);
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

    private static void registerCommandMap() {
        try {
            Method getCommandMapMethod = Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap");
            BUKKIT_COMMAND_MAP = (CommandMap) getCommandMapMethod.invoke(Bukkit.getServer());

            KNOWN_COMMANDS_FIELD = SimpleCommandMap.class.getDeclaredField("knownCommands");
            KNOWN_COMMANDS_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    private CommandType getCommandType(String string) {
        if (string.startsWith("everyone")) return CommandType.EVERYONE;
        if (string.startsWith("op")) return CommandType.OP;
        if (string.startsWith("permission")) return CommandType.PERMISSION;
        if (string.startsWith("me")) return CommandType.ME;
        if (string.startsWith("world")) return CommandType.WORLD;
        return null;
    }

    private void register(String name, List<String> messages, CommandType type, String permission, String receiverPermission, String world) {
        Commands command = new Commands(name, messages, type, permission, receiverPermission, world);
        if (registered.containsValue(command)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Duplicated " + ChatColor.WHITE + name + ChatColor.RED + " ! Ignored");
            return;
        }

        BUKKIT_COMMAND_MAP.register(getInstance().getName(), command);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + name + " successfully registered");
    }

    private void unregisterCommand(Commands command) {
        try {
            Map<?, ?> knownCommands = (Map<?, ?>) KNOWN_COMMANDS_FIELD.get(BUKKIT_COMMAND_MAP);

            knownCommands.values().removeIf(command::equals);

            command.unregister(BUKKIT_COMMAND_MAP);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        for (String name : registered.keySet()) {
            unregisterCommand(registered.remove(name));
        }
        reloadConfig();
        for (String string : getConfig().getStringList("register-commands")) {
            List<String> messages = colorize(getConfig().getStringList("commands." + string + ".text"));
            CommandType type = getCommandType(getConfig().getString("commands." + string + ".send-to").toLowerCase());
            String permission = getConfig().getString("commands." + string + ".permission", null);
            String receiverPermission = (type == CommandType.PERMISSION) ? (getConfig().getString("commands." + string + ".send-to").split(":"))[1] : null;
            String world = (type == CommandType.WORLD) ? (getConfig().getString("commands." + string + ".send-to").split(":"))[1] : null;
            register(string, messages, type, permission, receiverPermission, world);
        }
    }
}
