package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.TeleportConfirmTask;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDtpa implements CommandExecutor {

    SFPlugin plugin;

    public CMDtpa(SFPlugin parent) {
        this.plugin = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (plugin.teleporters.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.GRAY + "You cannot teleport again this quickly, learn to walk");
                    return true;
                }
                Player to = plugin.getServer().getPlayer(args[0]);
                if (to != null) {
                    TeleportConfirmTask task = new TeleportConfirmTask(player, to, false, plugin);
                    int id = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task);
                    task.setId(id);
                    plugin.teleporters.put(player.getName(), task);
                    player.sendMessage(ChatColor.GRAY + "Requesting!");
                    return true;
                }
            }
        }
        return false;
    }
}