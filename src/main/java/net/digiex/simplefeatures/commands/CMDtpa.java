package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wimbli.WorldBorder.BorderData;

public class CMDtpa implements CommandExecutor {

	SFPlugin plugin;

	public CMDtpa(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			SFPlayer sfp = SFPlayer.getSFPlayer(player);
			if (args.length > 0) {
				if (sfp.isTeleporting()) {
					player.sendMessage(ChatColor.GRAY
							+ sfp.translateString("teleport.inprogress"));
					return true;
				}
				Player to = plugin.getServer().getPlayer(args[0]);
				if (to != null) {
					if (player.getName().equals(to.getName())) {
						player.sendMessage(ChatColor.GRAY
								+ sfp.translateString("teleport.cannottptoself"));
						return true;
					}
					player.sendMessage(ChatColor.GRAY
							+ sfp.translateString("teleport.requesting"));
					if (SFPlugin.worldBorderPlugin != null) {
						BorderData bData = SFPlugin.worldBorderPlugin
								.GetWorldBorder(to.getWorld().getName());
						if (bData != null) {
							if (!bData.insideBorder(to.getLocation())) {
								player.sendMessage(ChatColor.RED
										+ sfp.translateString("teleport.outsideofborder"));
								return true;
							}
						}
					}/*
					 * sfp.teleport( player, to, to.getLocation(), true,
					 * SFPlayer.getSFPlayer(to).translateStringFormat(
					 * "teleport.tpa", player.getDisplayName()),
					 * sfp.translateStringFormat("teleport.tpingto",
					 * to.getDisplayName()));
					 */

					return true;
				}
			}
		}
		return false;
	}
}