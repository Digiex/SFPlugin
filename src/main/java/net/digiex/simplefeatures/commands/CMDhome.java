package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFHome;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.ChatColor;
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
		String homename = player.getLocation().getWorld().getName();
		if (player.getWorld().getName().contains("_nether")
				|| player.getWorld().getName().contains("_skylands")) {
			homename = "Survival";
		}
		if (args.length > 0) {
			homename = args[0];
		}
		if (SFTeleportTask.teleporters.containsKey(player.getName())) {
			player.sendMessage(ChatColor.GRAY
					+ "Teleport already in progress, use /abort to Cancel");
			return true;
		}
		SFHome home = plugin.getDatabase().find(SFHome.class).where()
				.ieq("worldName", homename).ieq("playerName", player.getName())
				.findUnique();
		if (home == null) {
			sender.sendMessage(ChatColor.RED + "No home for world called "
					+ homename + "!");
			return true;
		}
		if (SFPlugin.worldBorderPlugin != null) {
			BorderData bData = SFPlugin.worldBorderPlugin.GetWorldBorder(home
					.getWorldName());
			if (!bData.insideBorder(home.getLocation())) {
				player.sendMessage(ChatColor.RED
						+ "You seem to want to go somewhere, but sadly it's outside of the border.");
				return true;
			}
		}
		int taskId = plugin
				.getServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(
						plugin,
						new SFTeleportTask(player, player, null, home
								.getLocation(), false, null,
								"Teleporting to home"));
		SFTeleportTask.teleporters.put(player.getName(), taskId);
		return true;
	}
}
