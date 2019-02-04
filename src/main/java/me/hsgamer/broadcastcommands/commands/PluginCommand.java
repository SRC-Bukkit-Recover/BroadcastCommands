package me.hsgamer.broadcastcommands.commands;

import me.hsgamer.broadcastcommands.BroadcastCommands;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PluginCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("BroadcastCommands.help")) {
                    sender.sendMessage(ChatColor.AQUA + "/" + label + " help");
                    sender.sendMessage(ChatColor.AQUA + "/" + label + " list");
                    sender.sendMessage(ChatColor.AQUA + "/" + label + " reload");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have the permission to do this");
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("BroadcastCommands.list")) {
                    sender.sendMessage(ChatColor.BOLD + "Registered Commands:");
                    sender.sendMessage(BroadcastCommands.getRegistered().keySet().toString());
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have the permission to do this");
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("BroadcastCommands.reload")) {
                    BroadcastCommands.getInstance().reload();
                    sender.sendMessage(ChatColor.GREEN + "Reloaded");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have the permission to do this");
                }
            }
        } else {
            sender.sendMessage(ChatColor.BLUE + "-------------------------------------");
            sender.sendMessage(ChatColor.GOLD + "Plugin: " + ChatColor.WHITE + BroadcastCommands.getInstance().getDescription().getName());
            sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.WHITE + BroadcastCommands.getInstance().getDescription().getVersion());
            sender.sendMessage(ChatColor.GOLD + "Authors: " + ChatColor.WHITE + BroadcastCommands.getInstance().getDescription().getAuthors().toString());
            sender.sendMessage(ChatColor.BLUE + "-------------------------------------");
        }
        return true;
    }
}
