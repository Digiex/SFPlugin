package net.digiex.simplefeatures.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTranslation;

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
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String langid = "en_US";
		if (sender instanceof Player) {
			langid = new SFPlayer((Player) sender).getLanguage();
		}
		SFTranslation t = SFTranslation.getInstance();
		if (args.length == 0) {
			if (!sender.hasPermission(new Permission("sfp.who",
					PermissionDefault.TRUE))) {
				sender.sendMessage(ChatColor.RED
						+ t.translateKey("general.nopermission", langid));
				return true;
			}

			PerformPlayerList(sender, args, langid);
			return true;
		} else if (args.length == 1) {
			if (args[0].equals("me")
					&& sender.hasPermission(new Permission("sfp.whois.self",
							PermissionDefault.TRUE))) {
				PerformWhois(sender, new String[] { sender.getName() }, langid);
				return true;
			}
			if (!sender.hasPermission(new Permission("sfp.whois",
					PermissionDefault.OP))) {
				sender.sendMessage(ChatColor.RED
						+ t.translateKey("general.nopermission", langid));
				return true;
			}

			PerformWhois(sender, args, langid);
			return true;
		}

		return false;
	}

	private void PerformPlayerList(CommandSender sender, String[] args,
			String langid) {
		SFTranslation t = SFTranslation.getInstance();
		sender.sendMessage(ChatColor.BLUE
				+ t.translateKeyFormat(
						"who.header",
						langid,
						(ChatColor.RED + ""
								+ plugin.getServer().getOnlinePlayers().length + ChatColor.BLUE),
						ChatColor.RED + "" + plugin.getServer().getMaxPlayers()
								+ ChatColor.BLUE));

		Map<String, List<Player>> sort = new HashMap<String, List<Player>>();
		for (Player p : plugin.getServer().getOnlinePlayers()) {

			String world = p.getWorld().getName();
			if (world.contains("_nether")) {
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
			// Collections.sort(players); TODO: Make this work
			boolean first = true;
			for (Player player : players) {
				if (!first) {
					groupString.append(", ");
				} else {
					first = false;
				}
				if (player.isSleeping()) {
					groupString.append(ChatColor.GRAY + "["
							+ t.translateKey("who.sleeping", langid) + "]");
				}
				if (player.isSleepingIgnored()) {
					groupString.append(ChatColor.GRAY + "["
							+ t.translateKey("who.afk", langid) + "]");
				}
				groupString.append(ChatColor.WHITE + player.getDisplayName());
				groupString.append(ChatColor.WHITE);
			}
			sender.sendMessage(groupString.toString());
		}

	}

	private void PerformWhois(CommandSender sender, String[] args, String langid) {
		SFTranslation t = SFTranslation.getInstance();
		String name = null;
		if (args.length > 0) {
			name = args[0];
		}
		Player player = SFPlugin.getPlayer(sender, name);

		if (player != null) {

			sender.sendMessage("------ " + t.translateKey("who.whois", langid)
					+ " ------");
			if (!ChatColor.stripColor(player.getDisplayName())
					.equalsIgnoreCase(player.getName())) {
				sender.sendMessage(t.translateKey("who.username", langid) + " "
						+ player.getName());
			}
			sender.sendMessage(t.translateKey("who.displayname", langid) + " "
					+ player.getDisplayName());
			sender.sendMessage(t.translateKey("who.world", langid) + " "
					+ player.getWorld().getName());
			sender.sendMessage("IP: "
					+ player.getAddress().getAddress().getHostAddress());
			sender.sendMessage(t.translateKey("who.health", langid) + ": "
					+ player.getHealth() + "/20");
			sender.sendMessage(t.translateKey("who.food", langid) + ": "
					+ player.getFoodLevel() + "/20");
			sender.sendMessage(t.translateKey("who.location", langid) + ": "
					+ "x" + player.getLocation().getBlockX() + ", y"
					+ player.getLocation().getBlockY() + ", z"
					+ player.getLocation().getBlockZ());

		}
	}

}
