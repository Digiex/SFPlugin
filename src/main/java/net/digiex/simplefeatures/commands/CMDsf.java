package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CMDsf implements CommandExecutor {
	SFPlugin plugin;

	public CMDsf(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender.isOp()) {
			plugin.reloadConfig();
			sender.sendMessage(ChatColor.AQUA + "Config reloaded!");
		} else {
			sender.sendMessage(ChatColor.RED
					+ "You are not allowed to use this command");
		}
		return true;
	}

}
