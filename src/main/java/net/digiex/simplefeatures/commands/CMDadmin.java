package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.listeners.PListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
			SFPlayer sfp = new SFPlayer(p);

			if (sfp.isAdmin()) {
				p.setOp(!p.isOp());
				PListener.updatePlayerNameColour(p, plugin);
				String node;
				if (p.isOp()) {
					p.sendMessage(ChatColor.YELLOW
							+ sfp.translateString("admin.turnedon"));
					node = "admin.playerenabled";
				} else {
					p.sendMessage(ChatColor.YELLOW
							+ sfp.translateString("admin.turnedoff"));
					node = "admin.playerdisabled";
				}
				p.getWorld().strikeLightningEffect(p.getLocation());
				for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
					SFPlayer sfp1 = new SFPlayer(pl);
					pl.sendMessage(ChatColor.GRAY
							+ sfp1.translateStringFormat(node,
									p.getDisplayName() + ChatColor.GRAY));
				}
				return true;
			} else {
				sender.sendMessage(ChatColor.RED
						+ sfp.translateString("general.nopermission"));
				return true;
			}
		}
		return false;
	}
}
