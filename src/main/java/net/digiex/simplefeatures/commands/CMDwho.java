package net.digiex.simplefeatures.commands;


import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDwho implements CommandExecutor {
	SFPlugin plugin;

	public CMDwho(SFPlugin parent) {
		this.plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length == 0) {
			if (!sender.hasPermission(new Permission("sfp.who",
					PermissionDefault.TRUE))) {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to view the online players");
				return true;
			}

			PerformPlayerList(sender, args);
			return true;
		} else if (args.length == 1) {
			if (!sender.hasPermission(new Permission("sfp.whois",
					PermissionDefault.OP))) {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to view their details");
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

			sender.sendMessage("------ WHOIS report ------");
			if (!ChatColor.stripColor(player.getDisplayName())
					.equalsIgnoreCase(player.getName())) {
				sender.sendMessage("Username: " + player.getName());
			}
			sender.sendMessage("Display Name: " + player.getDisplayName());
			sender.sendMessage("World: " + player.getWorld().getName());
			sender.sendMessage("IP: "
					+ player.getAddress().getAddress().getHostAddress());
			sender.sendMessage("Health: " + player.getHealth() + "/20");
			sender.sendMessage("Food: " + player.getFoodLevel() + "/20");
			sender.sendMessage("Location: " + "x"
					+ player.getLocation().getBlockX() + ", y"
					+ player.getLocation().getBlockY() + ", z"
					+ player.getLocation().getBlockZ());

		}
	}

	private void PerformPlayerList(CommandSender sender, String[] args) {
		String result = "";
		Player[] players = plugin.getServer().getOnlinePlayers();
		int count = 0;

		for (Player player : players) {
			String name = player.getDisplayName();

			if (name.length() > 0) {
				if (result.length() > 0)
					result += ", ";
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
