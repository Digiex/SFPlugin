package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			if (SFTeleportTask.teleporters.containsKey(player.getName())) {
				player.sendMessage(ChatColor.GRAY
						+ "Teleport already in progress, use /abort to Cancel");
				return true;
			}
			Location spawnLoc = player.getWorld().getSpawnLocation();
			if (player.getWorld().getName().contains("_nether")
					|| player.getWorld().getName().contains("_skylands")) {
				spawnLoc = plugin.getServer().getWorld("Survival")
						.getSpawnLocation();
			}
			if (args.length > 0) {
				World world = null;
				for (World w : plugin.getServer().getWorlds()) {
					String wname = w.getName();
					if (w.getName().contains("_nether")) {
						wname = "Nether";
					}
					if (w.getName().contains("_skylands")) {
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
					if (world.getName().contains("_skylands")) {
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
			int taskId = plugin
					.getServer()
					.getScheduler()
					.scheduleAsyncDelayedTask(
							plugin,
							new SFTeleportTask(player, player, null, spawnLoc,
									false, null, "Teleporting to spawn of "
											+ spawnLoc.getWorld().getName()));
			SFTeleportTask.teleporters.put(player.getName(), taskId);

			return true;
		}
		return false;
	}
}
