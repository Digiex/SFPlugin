package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDspawn implements CommandExecutor {

    SFPlugin plugin;

    public CMDspawn(SFPlugin parent) {
        this.plugin = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (SFTeleportTask.teleporters.contains(player.getName())) {
                player.sendMessage(ChatColor.GRAY + "Teleport already in progress, use /abort to Cancel");
                return true;
            }
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new SFTeleportTask(player, player, null, player.getWorld().getSpawnLocation(), false, null, "Teleporting to spawn"));
            
            return true;
        }
        return false;
    }
}
