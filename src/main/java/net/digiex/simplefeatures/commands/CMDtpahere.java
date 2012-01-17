package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wimbli.WorldBorder.BorderData;

public class CMDtpahere implements CommandExecutor {

	SFPlugin plugin;

	public CMDtpahere(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length > 0) {
				Player to = SFPlugin.getPlayer(sender, args[0]);
				SFPlayer sfp = new SFPlayer(to);
				if (to != null) {
					if (sfp.isTeleporting()) {
						player.sendMessage(ChatColor.GRAY
								+ sfp.translateStringFormat(
										"teleport.playertping",
										to.getDisplayName()));
						return true;
					}
					if (player.getName().equals(to.getName())) {
						player.sendMessage(ChatColor.GRAY
								+ sfp.translateString("teleport.cannottptoself"));
						return true;
					}
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
					}
					player.sendMessage(ChatColor.GRAY
							+ sfp.translateString("teleport.requesting"));
					sfp.teleport(
							player,
							to,
							player.getLocation(),
							true,
							new SFPlayer(to).translateStringFormat(
									"teleport.tpahere", player.getDisplayName()),
							sfp.translateStringFormat("teleport.tpingto",
									player.getDisplayName()));

					return true;
				}
			}
		}
		return false;
	}
}