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
						player.sendMessage(ChatColor.GRAY + to.getDisplayName()
								+ " is already teleporting, try again later.");
						return true;
					}
					if (player.getName().equals(to.getName())) {
						player.sendMessage(ChatColor.GRAY
								+ "You cannot teleport to yourself, silly.");
						return true;
					}
					if (SFPlugin.worldBorderPlugin != null) {
						BorderData bData = SFPlugin.worldBorderPlugin
								.GetWorldBorder(to.getWorld().getName());
						if (bData != null) {
							if (!bData.insideBorder(to.getLocation())) {
								player.sendMessage(ChatColor.RED
										+ "You seem to want to go somewhere, but sadly it's outside of the border.");
								return true;
							}
						}
					}
					player.sendMessage(ChatColor.GRAY + "Requesting!");
					sfp.teleport(player, to, player.getLocation(), true,
							player.getDisplayName()
									+ " wants to teleport you to them",
							"Teleporting to " + player.getDisplayName());

					return true;
				}
			}
		}
		return false;
	}
}