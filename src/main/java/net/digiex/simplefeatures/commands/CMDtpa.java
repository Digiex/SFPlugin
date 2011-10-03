package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTeleport;
import net.digiex.simplefeatures.SFTeleport.TeleportTypes;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

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
                if (plugin.teleporters.containsKey(player)) {
                    player.sendMessage(ChatColor.GRAY + "Teleport already in progress, use /abort to Cancel");
                    return true;
                }
                Player to = plugin.getServer().getPlayer(args[0]);
                if (to != null) {
                    if (player.getName().equals(to.getName())) {
                        player.sendMessage(ChatColor.GRAY + "You cannot teleport to yourself, silly.");
                        return true;
                    }
                    SFTeleport teleport = new SFTeleport(plugin, TeleportTypes.tpa);
                    if (player.getGameMode().equals(GameMode.CREATIVE) || player.hasPermission(new Permission("sf.tpoverride", PermissionDefault.OP))) {
                        teleport.setTimer(false);
                    }
                    player.sendMessage(ChatColor.GRAY + "Requesting!");
                    teleport.setFrom(player);
                    teleport.setTo(to);
                    teleport.startTeleport();
                    plugin.teleporters.put(player, teleport);
                    return true;
                }
            }
        }
        return false;
    }
}