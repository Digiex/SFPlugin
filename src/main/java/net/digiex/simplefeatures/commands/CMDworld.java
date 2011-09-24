package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDworld implements CommandExecutor {
	SFPlugin plugin;

	public CMDworld(SFPlugin parent) {
		this.plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length > 0) {
				World world = null;
				for (World w : plugin.getServer().getWorlds()) {
					if (w.getName().equalsIgnoreCase(args[0])) {
						world = w;
					} else if (w.getName().toLowerCase()
							.contains(args[0].toLowerCase())) {
						world = w;
					}

				}
				if (world != null) {
					if (world.getName().contains("_nether")) {
						if (!((Player) sender).isOp()) {
							sender.sendMessage(ChatColor.RED
									+ "Only ops can warp to the nether!");
							return true;
						} else {
							sender.sendMessage("Wait! You need to use nether portals!!! Oh you're an OP... Sorry, my mistake.");
						}
					}
					((Player) sender).teleport(world.getSpawnLocation());
					sender.sendMessage(ChatColor.GRAY + "Teleporting to "
							+ world.getName());
					return true;

				}
			}
			ListWorlds(sender, args[0]);
			return true;
		}
		return false;
	}

	private void ListWorlds(CommandSender sender, String tried) {
		sender.sendMessage(ChatColor.RED + "World \"" + tried
				+ "\" was not found. Check Spelling.");
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
					sender.sendMessage(ChatColor.GRAY + w.getName());
				}
			} else {
				sender.sendMessage(ChatColor.YELLOW + w.getName());
			}
		}
	}
}
