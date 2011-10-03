package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFHome;
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

public class CMDhome implements CommandExecutor {

    SFPlugin plugin;

    public CMDhome(SFPlugin parent) {
        this.plugin = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        String name = null;
        if (args.length > 0) {
            name = args[0];
        }
        Player player = SFPlugin.getPlayer(sender, name);
        if (player == null) {
            return true;
        } else if ((player != sender) && (!sender.isOp())) {
            sender.sendMessage(ChatColor.RED
                    + "You don't have permission to go to other players homes");
            return true;
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "I don't know how to move you!");
            return true;
        }
        if (plugin.teleporters.containsKey(player)) {
            player.sendMessage(ChatColor.GRAY + "Teleport already in progress, use /abort to Cancel");
            return true;
        }
        SFHome home = plugin.getDatabase().find(SFHome.class).where().ieq("worldName", player.getLocation().getWorld().getName()).ieq("playerName", player.getName()).findUnique();
        if (home == null) {
            sender.sendMessage(ChatColor.RED + "I don't know where that is!");
            return true;
        }
        SFTeleport teleport = new SFTeleport(plugin, TeleportTypes.home);
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.hasPermission(new Permission("sf.tpoverride", PermissionDefault.OP))) {
            teleport.setTimer(false);
        }
        teleport.setHome(home.getLocation());
        teleport.setFrom(player);
        teleport.startTeleport();
        plugin.teleporters.put(player, teleport);
        return true;
    }
}
