package net.digiex.simplefeatures.commands;

import java.util.List;

import net.digiex.simplefeatures.SFHome;
import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDlisthomes implements CommandExecutor {
	SFPlugin plugin;

	public CMDlisthomes(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String name = null;
		if (args.length > 0) {
			name = args[0];
		}
		Player player = SFPlugin.getPlayer(sender, name);
		if (player == null) {
			return true;
		}
		SFPlayer sfp = new SFPlayer(player);
		List<SFHome> homes = plugin.getDatabase().find(SFHome.class).where()
				.ieq("playerName", player.getName()).findList();
		if (homes.isEmpty() && player.getBedSpawnLocation() == null) {
			if (sender == player) {
				sender.sendMessage(ChatColor.RED
						+ sfp.translateString("listhomes.nohomes"));
			} else {
				sender.sendMessage(ChatColor.RED
						+ sfp.translateString("listhomes.nohomesplayer"));
			}
		} else {
			String result = "";
			for (SFHome home : homes) {
				if (result.length() > 0) {
					result += ", ";
				}
				result += home.getWorldName();
			}
			if (player.getBedSpawnLocation() != null) {
				if (result.length() > 0) {
					result += ", ";
				}
			}
			sender.sendMessage(sfp.translateString("listhomes.allhomes") + " "
					+ result);
		}
		return true;
	}
}