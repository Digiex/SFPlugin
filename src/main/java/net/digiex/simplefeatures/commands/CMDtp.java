package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDtp implements CommandExecutor {

	SFPlugin plugin;

	public CMDtp(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Player from = p;
			Player to = null;
			if (p.isOp()) {
				if (args.length == 1) {
					to = SFPlugin.getPlayer(sender, args[0]);
				} else if (args.length == 2) {
					from = SFPlugin.getPlayer(sender, args[0]);
					to = SFPlugin.getPlayer(sender, args[1]);
				}
				if (from != null && to != null) {
					from.teleport(to);
					sender.sendMessage("Teleported ;)");
					return true;
				}
			} else {
				p.sendMessage("Please use /TPA to Teleport to players");
				return true;
			}
		} else {
			if (args.length == 2) {
				Player from = SFPlugin.getPlayer(sender, args[0]);
				Player to = SFPlugin.getPlayer(sender, args[1]);
				if (from != null && to != null) {
					from.teleport(to);
					sender.sendMessage("Teleported ;)");
					return true;
				}
			}
		}
		return false;
	}
}
