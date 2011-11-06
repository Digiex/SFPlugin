package net.digiex.simplefeatures.commands;

import java.util.List;
import java.util.Set;

import net.digiex.simplefeatures.SFHome;
import net.digiex.simplefeatures.SFInventory;
import net.digiex.simplefeatures.SFLocation;
import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDcleanup implements CommandExecutor {
	SFPlugin plugin;

	public CMDcleanup(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!sender.hasPermission(new Permission("sf.cleanup",
				PermissionDefault.OP))) {
			sender.sendMessage("No permission to do that!");
			return true;
		}
		Set<OfflinePlayer> ignore = plugin.getServer().getWhitelistedPlayers();
		int i = 0;
		List<SFInventory> invs = plugin.getDatabase().find(SFInventory.class)
				.findList();
		for (SFInventory inv : invs) {
			if (!ignore.contains(plugin.getServer().getOfflinePlayer(
					inv.getPlayerName()))) {
				i++;
				plugin.getDatabase().delete(inv);
			}
		}
		sender.sendMessage(i + " Inventories cleared.");
		i = 0;
		List<SFHome> homes = plugin.getDatabase().find(SFHome.class).findList();
		for (SFHome home : homes) {
			if (!ignore.contains(plugin.getServer().getOfflinePlayer(
					home.getPlayerName()))) {
				i++;
				plugin.getDatabase().delete(home);
			}
		}
		sender.sendMessage(i + " Homes cleared.");
		i = 0;
		List<SFMail> mails = plugin.getDatabase().find(SFMail.class).findList();
		for (SFMail mail : mails) {
			if (!ignore.contains(plugin.getServer().getOfflinePlayer(
					mail.getToPlayer()))) {
				i++;
				plugin.getDatabase().delete(mail);
			}
		}
		sender.sendMessage(i + " Mails cleared.");
		i = 0;
		List<SFLocation> locs = plugin.getDatabase().find(SFLocation.class)
				.findList();
		for (SFLocation loc : locs) {
			if (!ignore.contains(plugin.getServer().getOfflinePlayer(
					loc.getPlayerName()))) {
				i++;
				plugin.getDatabase().delete(loc);
			}
		}
		sender.sendMessage(i + " last locations cleared.");
		i = 0;
		return true;
	}

}
