package net.digiex.simplefeatures.commands;

import java.util.Set;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.listeners.PListener;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CMDadmin implements CommandExecutor {
	SFPlugin plugin;

	public CMDadmin(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			ConfigurationSection cs = plugin.getConfig()
					.getConfigurationSection("admins");
			if (cs != null) {
				Set<String> admins = cs.getKeys(false);
				if (admins != null) {
					if (admins.contains(p.getName())) {
						p.setOp(!p.isOp());
						PListener.updatePlayerNameColour(p, plugin);
						if (p.isOp()) {
							p.sendMessage(ChatColor.YELLOW
									+ "Admin mode turned on!");
						} else {
							p.sendMessage(ChatColor.YELLOW
									+ "Admin mode turned off!");
						}
						return true;
					} else {
						sender.sendMessage(ChatColor.RED
								+ "You are not allowed to do that, sir!");
						return true;
					}
				}
			}
		}
		return false;
	}
}
