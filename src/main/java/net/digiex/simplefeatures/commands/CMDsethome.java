package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDsethome implements CommandExecutor {
	SFPlugin plugin;

	public CMDsethome(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			SFPlayer sfp = new SFPlayer(player);
			if (sfp.getTempHomeLocation() != null) {
				Location newHomeLoc = sfp.getTempHomeLocation();
				if (newHomeLoc.toVector().distance(
						player.getLocation().toVector()) > 15) {
					player.sendMessage(ChatColor.RED
							+ "You are too far away from your bed. Setting home cancelled.");
					sfp.setTempHomeLocation(null);
				} else {
					sfp.setHome(newHomeLoc);
					player.sendMessage(ChatColor.YELLOW
							+ "Home set to your bed");
					sfp.setTempHomeLocation(null);
					return true;
				}
			} else if (!player.hasPermission(new Permission("sf.sethome",
					PermissionDefault.OP))) {
				player.sendMessage(ChatColor.RED
						+ sfp.translateString("sethome.usebed"));
				return true;
			}
			sfp.setHome(player.getLocation());
			return true;
		}
		return false;
	}
}
