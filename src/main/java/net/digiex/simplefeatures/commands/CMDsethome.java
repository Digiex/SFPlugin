package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
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
			if (!player.hasPermission(new Permission("sf.sethome",
					PermissionDefault.OP))) {
				player.sendMessage(ChatColor.RED + "Use a bed to set a home!");
				return true;
			}
			SFPlayer sfp = new SFPlayer(player, plugin);
			sfp.setHome(player.getLocation());
			return true;
		}
		return false;
	}
}
