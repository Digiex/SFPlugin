package net.digiex.simplefeatures.commands;

import java.util.ArrayList;
import java.util.List;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class CMDhelp implements CommandExecutor {
	SFPlugin plugin;
	public boolean showpermless = true;
	public final Yaml yaml = new Yaml(new SafeConstructor());

	public CMDhelp(SFPlugin parent) {
		this.plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		int page = 1;
		@SuppressWarnings("unused")
		String match = "";
		try {
			if (args.length > 0) {
				match = args[0].toLowerCase();
				page = Integer.parseInt(args[args.length - 1]);
				if (args.length == 1) {
					match = "";
				}
			}

		} catch (Exception ex) {
			if (args.length == 1) {
				match = args[0].toLowerCase();
			}
		}

		List<String> lines;
		// TODO: Add help this way at some point but make it work without
		// permissions also
		// try {
		// lines = getHelpLines(sender, match);
		// } catch (Exception e) {
		// sender.sendMessage("Help failed: " + e.getMessage());
		// e.printStackTrace();
		// return true;
		// }
		lines = new ArrayList<String>();
		lines.add(ChatColor.RED + "home:" + ChatColor.YELLOW
				+ " Teleport to home");
		lines.add(ChatColor.RED + "who:" + ChatColor.YELLOW
				+ " Show player list");
		lines.add(ChatColor.RED + "world:" + ChatColor.YELLOW
				+ " Teleport across worlds");
		lines.add(ChatColor.RED + "spawn:" + ChatColor.YELLOW
				+ " Teleport to the world spawn");
		lines.add(ChatColor.RED + "tpa:" + ChatColor.YELLOW
				+ " Teleport to player");
		lines.add(ChatColor.RED + "tpahere:" + ChatColor.YELLOW
				+ " Teleport a player here");
		lines.add(ChatColor.RED + "msg, m:" + ChatColor.YELLOW
				+ " Send a private message");
		lines.add(ChatColor.RED + "reply, r:" + ChatColor.YELLOW
				+ " reply to a message");
		lines.add(ChatColor.RED + "lastmsgs:" + ChatColor.YELLOW
				+ " Show last messages");
		lines.add(ChatColor.RED + "me:" + ChatColor.YELLOW
				+ " Express yourself");
		lines.add(ChatColor.RED + "listhomes:" + ChatColor.YELLOW
				+ " List your homes");
		if (lines.isEmpty()) {
			sender.sendMessage("No help found");
			return true;
		}

		final int start = (page - 1) * 9;
		final int pages = lines.size() / 9 + (lines.size() % 9 > 0 ? 1 : 0);

		sender.sendMessage(ChatColor.GREEN + "Page " + page + " of " + pages);
		for (int i = start; i < lines.size() && i < start + 9; i++) {
			sender.sendMessage(lines.get(i));
		}

		return true;
	}

}
