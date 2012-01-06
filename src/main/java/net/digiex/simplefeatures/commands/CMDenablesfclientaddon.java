package net.digiex.simplefeatures.commands;

import java.util.logging.Level;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CMDenablesfclientaddon implements CommandExecutor {

	SFPlugin plugin;

	public CMDenablesfclientaddon(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SFPlugin.clientAddons.add(sender.getName());
		if (args.length > 0) {
			SFPlugin.log(Level.INFO, sender.getName()
					+ " has the SFPlugin client mod version " + args[0] + "!");
		} else {
			SFPlugin.log(Level.INFO, sender.getName()
					+ " has UNKNOWN version of the SFPlugin client mod!");
		}
		return true;
	}

}
