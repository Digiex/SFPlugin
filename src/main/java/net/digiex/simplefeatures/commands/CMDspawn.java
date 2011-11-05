package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDspawn implements CommandExecutor {

	SFPlugin plugin;

	public CMDspawn(SFPlugin parent) {
		this.plugin = parent;
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
			int taskId = plugin
					.getServer()
					.getScheduler()
					.scheduleAsyncDelayedTask(
							plugin,
							new SFTeleportTask(player, player, null, spawnLoc,
									false, null, "Teleporting to spawn"));
			SFTeleportTask.teleporters.put(player.getName(), taskId);

			return true;
		}
		return false;
	}
}
