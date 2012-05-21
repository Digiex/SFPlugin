package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wimbli.WorldBorder.BorderData;

public class CMDhome implements CommandExecutor {

	SFPlugin plugin;

	public CMDhome(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			return true;
		}
		World homeworld = player.getLocation().getWorld();
		if (args.length > 0) {
			homeworld = plugin.getServer().getWorld(args[0]);
		}
		SFPlayer sfp = SFPlayer.getSFPlayer(player);
		if (sfp.isTeleporting()) {
			player.sendMessage(ChatColor.GRAY
					+ sfp.translateString("teleport.inprogress"));
			return true;
		}
		Location homeLoc = sfp.getHomeLoc(homeworld);
		if (homeLoc == null) {
			sender.sendMessage(ChatColor.RED
					+ sfp.translateString("home.nohome"));
			return true;
		}
		if (SFPlugin.worldBorderPlugin != null) {
			BorderData bData = SFPlugin.worldBorderPlugin
					.GetWorldBorder(homeLoc.getWorld().getName());
			if (bData != null) {
				if (!bData.insideBorder(homeLoc)) {
					player.sendMessage(ChatColor.RED
							+ sfp.translateString("teleport.outsideofborder"));
					return true;
				}
			}
		}
		sfp.teleport(homeLoc,
				ChatColor.YELLOW + sfp.translateString("home.tpingto"));
		return true;
	}
}
