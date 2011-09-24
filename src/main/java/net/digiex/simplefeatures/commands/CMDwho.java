package net.digiex.simplefeatures.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		StringBuilder online = new StringBuilder();
		online.append(ChatColor.BLUE).append("There are ")
				.append(ChatColor.RED)
				.append(plugin.getServer().getOnlinePlayers().length);
		online.append(ChatColor.BLUE).append(" out of a maximum ")
				.append(ChatColor.RED)
				.append(plugin.getServer().getMaxPlayers());
		online.append(ChatColor.BLUE).append(" players online.");
		sender.sendMessage(online.toString());

		Map<String, List<Player>> sort = new HashMap<String, List<Player>>();
		for (Player p : plugin.getServer().getOnlinePlayers()) {

			String world = p.getWorld().getName();
			if(world.contains("_nether")){
				world = "Nether";
			}
			List<Player> list = sort.get(world);
			if (list == null) {
				list = new ArrayList<Player>();
				sort.put(world, list);
			}
			list.add(p);
		}
		String[] worlds = sort.keySet().toArray(new String[0]);
		Arrays.sort(worlds, String.CASE_INSENSITIVE_ORDER);
		for (String world : worlds) {
			StringBuilder groupString = new StringBuilder();
			groupString.append(world).append(": ");
			List<Player> players = sort.get(world);
			//Collections.sort(players); TODO: Make this work
			boolean first = true;
			for (Player player : players) {
				if (!first) {
					groupString.append(", ");
				} else {
					first = false;
				}
				if (player.isSleeping()) {
					groupString.append(ChatColor.GRAY+"[SLEEPING");
					if (player.isSleepingIgnored()) {
						groupString.append(" (ignored)");
					}
					groupString.append("]"+ChatColor.WHITE);
				}
				groupString.append(player.getDisplayName());
				groupString.append(ChatColor.WHITE);
			}
			sender.sendMessage(groupString.toString());
		}

	}

}
