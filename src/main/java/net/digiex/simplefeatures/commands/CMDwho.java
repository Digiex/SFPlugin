package net.digiex.simplefeatures.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDwho  implements CommandExecutor {
	SFPlugin plugin;

	public CMDwho(SFPlugin parent) {
		this.plugin = parent;
	}


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission(new Permission("sfp.who",PermissionDefault.TRUE))) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to view the online players");
                return true;
            }

            PerformPlayerList(sender, args);
            return true;
        } else if (args.length == 1) {
            if (!sender.hasPermission(new Permission("sfp.whois",PermissionDefault.OP))) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to view their details");
                return true;
            }

            PerformWhois(sender, args);
            return true;
        }

        return false;
    }

    private void PerformWhois(CommandSender sender, String[] args) {
		String name = null;
		if (args.length > 0) {
			name = args[0];
		}
        Player player = SFPlugin.getPlayer(sender, name);

        if (player != null) {
        	Map<String, String> result = new HashMap<String, String>();
            result.put("Display Name", player.getDisplayName());
            result.put("World", player.getWorld().getName());
            result.put("IP",player.getAddress().getAddress().getHostAddress());
            result.put("Health", player.getHealth()+"/20");
            result.put("Food", player.getFoodLevel()+"/20");
            result.put("Location", "x"+player.getLocation().getBlockX()+", y"+player.getLocation().getBlockY()+", z"+player.getLocation().getBlockZ());

            if (!ChatColor.stripColor(player.getDisplayName()).equalsIgnoreCase(player.getName())) {
                result.put("Username", player.getName());
            }

            sender.sendMessage("------ WHOIS report ------");
            Set<String> keys = result.keySet();

            for (String key : keys) {
                sender.sendMessage(key + ": " + result.get(key));
            }
        }
    }

    private void PerformPlayerList(CommandSender sender, String[] args) {
        String result = "";
        Player[] players = plugin.getServer().getOnlinePlayers();
        int count = 0;

        for (Player player : players) {
            String name = player.getDisplayName();

            if (name.length() > 0) {
                if (result.length() > 0) result += ", ";
                result += name;
                count++;
            }
        }

        if (count == 0) {
            sender.sendMessage("There's currently nobody playing on this server!");
        } else if (count == 1) {
            sender.sendMessage("There's only one player online: " + result);
        } else {
            sender.sendMessage("Online players: " + result);
        }
    }


}
