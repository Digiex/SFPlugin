package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDme implements CommandExecutor {
	SFPlugin plugin;

	public CMDme(SFPlugin parent) {
		this.plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length < 1) {
				return false;
			}
			final StringBuilder message = new StringBuilder();
			message.append("* ");
			message.append(((Player) sender).getDisplayName());
			message.append(' ');
			for (int i = 0; i < args.length; i++) {
				message.append(args[i]);
				message.append(' ');
			}
			plugin.getServer().broadcastMessage(message.toString());
			return true;
		}
		return false;
	}

}
