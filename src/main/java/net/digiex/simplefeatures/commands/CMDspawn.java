package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import net.digiex.simplefeatures.TeleportTask;
import org.bukkit.GameMode;

public class CMDspawn implements CommandExecutor {

    SFPlugin plugin;

    public CMDspawn(SFPlugin parent) {
        this.plugin = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.teleporters.containsKey(player.getName())) {
                if (player.hasPermission(new Permission("sf.tpoverride", PermissionDefault.OP))) {
                    TeleportTask task = plugin.teleporters.get(player.getName());
                    int id = task.getId();
                    plugin.getServer().getScheduler().cancelTask(id);
                } else {
                    player.sendMessage(ChatColor.GRAY + "Teleport already in progress, use /abort to Cancel");
                    return true;
                }
            }
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage(ChatColor.GRAY + "Spawn!");
                return true;
            }
            if (player.hasPermission(new Permission("sf.tpoverride", PermissionDefault.OP))) {
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage(ChatColor.GRAY + "Spawn!");
                return true;
            }
            TeleportTask task = new TeleportTask(plugin, player, null, null, null, false, false, false, false, true);
            int id = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task);
            task.setId(id);
            plugin.teleporters.put(player.getName(), task);
            return true;
        }
        return false;
    }
}
