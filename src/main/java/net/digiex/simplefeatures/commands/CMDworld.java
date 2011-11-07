package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFLocation;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDworld implements CommandExecutor {

	SFPlugin plugin;

	public CMDworld(SFPlugin parent) {
		plugin = parent;
	}

	private void ListWorlds(CommandSender sender, String tried) {
		if (tried.length() > 0) {
			sender.sendMessage(ChatColor.RED + "World \"" + tried
					+ "\" was not found. Check Spelling.");
		}
		sender.sendMessage(ChatColor.GREEN + "Available worlds:");
		for (World w : plugin.getServer().getWorlds()) {
			if (w.getName().contains("_nether")) {
				boolean allownether = false;
				if (sender instanceof Player) {
					allownether = ((Player) sender).isOp();
				} else {
					allownether = true;
				}
				if (allownether) {
					sender.sendMessage(ChatColor.GRAY + "Nether");
				}
			} else if (w.getName().contains("_skylands")) {
				boolean allowskylands = false;
				if (sender instanceof Player) {
					allowskylands = ((Player) sender).isOp();
				} else {
					allowskylands = true;
				}
				if (allowskylands) {
					sender.sendMessage(ChatColor.GRAY + "End");
				}
			} else {
				sender.sendMessage(ChatColor.YELLOW + w.getName());
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SFTeleportTask.teleporters.containsKey(player.getName())) {
				player.sendMessage(ChatColor.GRAY
						+ "Teleport already in progress, use /abort to cancel.");
				return true;
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
					SFLocation lastLoc = plugin.getDatabase()
							.find(SFLocation.class).where()
							.ieq("worldName", world.getName())
							.ieq("playerName", player.getName()).findUnique();
					Location loc = world.getSpawnLocation();
					if (lastLoc != null) {
						loc = new Location(plugin.getServer().getWorld(
								lastLoc.getWorldName()), lastLoc.getX(),
								lastLoc.getY(), lastLoc.getZ(),
								lastLoc.getYaw(), lastLoc.getPitch());
					}
					int taskId = plugin
							.getServer()
							.getScheduler()
							.scheduleAsyncDelayedTask(
									plugin,
									new SFTeleportTask(player, player, null,
											loc, false, null, "Teleporting to "
													+ world.getName()));
					SFTeleportTask.teleporters.put(player.getName(), taskId);
					return true;

				} else if (world == player.getWorld()) {
					sender.sendMessage(ChatColor.RED
							+ "You are in this world already! Use /spawn");
					return true;
				}
				ListWorlds(sender, args[0]);
				return true;
			}
			ListWorlds(sender, "");
			return true;
		}
		return false;
	}
}
