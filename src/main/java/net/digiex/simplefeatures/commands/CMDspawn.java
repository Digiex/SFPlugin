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

public class CMDspawn implements CommandExecutor {

	SFPlugin plugin;

	public CMDspawn(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			SFPlayer sfp = new SFPlayer(player, plugin);
			if (sfp.isTeleporting()) {
				player.sendMessage(ChatColor.GRAY
						+ "Teleport already in progress, use /abort to Cancel");
				return true;
			}
			Location spawnLoc = player.getWorld().getSpawnLocation();
			if (player.getWorld().getName().contains("_nether")
					|| player.getWorld().getName().contains("_the_end")) {
				spawnLoc = plugin.getServer().getWorlds().get(0)
						.getSpawnLocation();
			}
			if (args.length > 0) {
				World world = null;
				for (World w : plugin.getServer().getWorlds()) {
					String wname = w.getName();
					if (w.getName().contains("_nether")) {
						wname = "Nether";
					}
					if (w.getName().contains("_the_end")) {
						wname = "End";
					}
					if (wname.equalsIgnoreCase(args[0])) {
						world = w;
					} else if (wname.toLowerCase().indexOf(
							args[0].toLowerCase()) != -1) {
						world = w;
					}
				}
				if (world != null) {
					if (world.getName().contains("_nether")) {
						if (!((Player) sender).isOp()) {
							sender.sendMessage(ChatColor.RED
									+ "Use a Nether Portal in Survival to enter Nether");
							return true;
						} else {
							sender.sendMessage("Wait! You need to use nether portals!!! Oh you're an OP... Sorry, my mistake.");
						}
					}
					if (world.getName().contains("_the_end")) {
						if (!((Player) sender).isOp()) {
							sender.sendMessage(ChatColor.RED
									+ "Use a Enderportal in Survival to enter The End");
							return true;
						} else {
							sender.sendMessage("Wait! You need to use Enderportals!!! Oh you're an OP... Sorry, my mistake.");
						}
					}
					spawnLoc = world.getSpawnLocation();
				}
			}
			if (SFPlugin.worldBorderPlugin != null) {
				BorderData bData = SFPlugin.worldBorderPlugin
						.GetWorldBorder(spawnLoc.getWorld().getName());
				if (bData != null) {
					if (!bData.insideBorder(spawnLoc)) {
						player.sendMessage(ChatColor.RED
								+ "You seem to want to go somewhere, but sadly it's outside of the border.");
						return true;
					}
				}
			}
			sfp.teleport(player, null, spawnLoc, false, null,
					"Teleporting to spawn of " + spawnLoc.getWorld().getName());

			return true;
		}
		return false;
	}
}
