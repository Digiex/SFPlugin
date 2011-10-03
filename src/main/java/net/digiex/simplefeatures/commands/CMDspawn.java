package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTeleport;
import net.digiex.simplefeatures.SFTeleport.TeleportTypes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
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
            if (plugin.teleporters.containsKey(player)) {
                player.sendMessage(ChatColor.GRAY + "Teleport already in progress, use /abort to Cancel");
                return true;
            }
            SFTeleport teleport = new SFTeleport(plugin, TeleportTypes.spawn);
            if (player.getGameMode().equals(GameMode.CREATIVE) || player.hasPermission(new Permission("sf.tpoverride", PermissionDefault.OP))) {
                teleport.setTimer(false);
            }
            teleport.setFrom(player);
            teleport.startTeleport();
            plugin.teleporters.put(player, teleport);
            return true;
        }
        return false;
    }
}
