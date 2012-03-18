package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDxp implements CommandExecutor {

	SFPlugin plugin;

	public CMDxp(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player p = null;
		if (sender.isOp()) {
			if (args.length > 0) {
				p = SFPlugin.getPlayer(sender, args[0]);
				if (p == null) {
					sender.sendMessage(ChatColor.RED + "No player called "
							+ args[0] + " found!");
					return true;
				}
				sender.sendMessage(ChatColor.YELLOW + "Statistics for player "
						+ p.getDisplayName());

			}
		}
		if (p == null && sender instanceof Player) {
			p = (Player) sender;
		}
		if (p != null) {
			sender.sendMessage(ChatColor.YELLOW + "Total experience: "
					+ ChatColor.AQUA + p.getTotalExperience());
			sender.sendMessage(ChatColor.YELLOW + "Experience Level: "
					+ ChatColor.AQUA + p.getLevel());
			sender.sendMessage(ChatColor.YELLOW + "Next level complete: "
					+ ChatColor.AQUA + Math.round(p.getExp() * 100) + "%");
			return true;
		}
		return false;
	}
}
