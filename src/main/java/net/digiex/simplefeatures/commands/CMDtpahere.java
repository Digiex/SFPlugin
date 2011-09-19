package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.TeleportConfirmTask;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDtpahere implements CommandExecutor {

    SFPlugin plugin;

    public CMDtpahere(SFPlugin parent) {
        this.plugin = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                Player to = plugin.getServer().getPlayer(args[0]);
                if (plugin.teleporters.containsKey(to.getName())) {
                    if (to.hasPermission("Permission node here to please")) {
                        TeleportConfirmTask task = plugin.teleporters.get(to.getName());
                        int id = task.getId();
                        plugin.getServer().getScheduler().cancelTask(id);
                    } else {
                        player.sendMessage(ChatColor.GRAY + to.getName() + " cannot teleport this quickly, he/she must learn to walk");
                        return true;
                    }
                }
                if (to != null) {
                    TeleportConfirmTask task = new TeleportConfirmTask(player, to, true, plugin);
                    int id = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task);
                    task.setId(id);
                    plugin.teleporters.put(to.getName(), task);
                    player.sendMessage(ChatColor.GRAY + "Requesting!");
                    return true;
                }
            }
        }
        return false;
    }
}