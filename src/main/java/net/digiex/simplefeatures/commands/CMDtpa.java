package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

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
                if (SFTeleportTask.teleporters.contains(player.getName())) {
                    player.sendMessage(ChatColor.GRAY + "Teleport already in progress, use /abort to Cancel");
                    return true;
                }
                Player to = plugin.getServer().getPlayer(args[0]);
                if (to != null) {
                    if (player.getName().equals(to.getName())) {
                        player.sendMessage(ChatColor.GRAY + "You cannot teleport to yourself, silly.");
                        return true;
                    }
                    player.sendMessage(ChatColor.GRAY + "Requesting!");
                    
                    plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new SFTeleportTask(player, player, to, to.getLocation(), true, player.getDisplayName()+" wants to teleport to you", "Teleporting to "+to.getDisplayName()));
                   

                    return true;
                }
            }
        }
        return false;
    }
}