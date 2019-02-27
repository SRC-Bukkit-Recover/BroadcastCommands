package me.hsgamer.broadcastcommands.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.broadcastcommands.BroadcastCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class Command extends BukkitCommand {
    private List<String> messages;
    private CommandType type;
    private String permission;
    private String receiverPermission;
    private String world;

    public Command(String label, List<String> messages, CommandType type, String permission, String receiverPermission, String world) {
        super(label);
        this.messages = messages;
        this.type = type;
        this.permission = permission;
        this.receiverPermission = receiverPermission;
        this.world = world;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (permission != null && !(sender instanceof ConsoleCommandSender || sender.hasPermission(permission))) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to do this");
        } else {
            switch (type) {
                case ME: {
                    sendMessage(sender, messages);
                    return true;
                }
                case OP: {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            sendMessage(player, messages);
                        }
                    }
                    sendMessage(sender, messages);
                    return true;
                }
                case EVERYONE: {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sendMessage(player, messages);
                    }
                    if (sender instanceof ConsoleCommandSender) {
                        sendMessage(sender, messages);
                    }
                    return true;
                }
                case PERMISSION: {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission(receiverPermission)) {
                            sendMessage(player, messages);
                        }
                    }
                    sendMessage(sender, messages);
                    return true;
                }
                case WORLD: {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getWorld().getName().equals(world)) {
                            sendMessage(player, messages);
                        }
                    }
                    sendMessage(sender, messages);
                    return true;
                }
            }
        }
        return true;
    }

    private void sendMessage(CommandSender receiver, List<String> messages) {
        for (String message : messages) {
            String temp = message;
            if (BroadcastCommands.isPlaceholderAPI() && (receiver instanceof Player)) {
                temp = PlaceholderAPI.setPlaceholders((Player) receiver, temp);
            }
            receiver.sendMessage(temp);
        }
    }

    private void sendMessage(Player receiver, List<String> messages) {
        for (String message : messages) {
            String temp = message;
            if (BroadcastCommands.isPlaceholderAPI()) {
                temp = PlaceholderAPI.setPlaceholders(receiver, temp);
            }
            receiver.sendMessage(temp);
        }
    }
}
