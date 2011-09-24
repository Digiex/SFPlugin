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
				World world = plugin.getServer().getWorld(args[0]);
				if (world != null) {
					if (world.getName().contains("_nether")) {
						if (!((Player) sender).isOp()) {
							sender.sendMessage(ChatColor.RED
									+ "Only ops can warp to the nether!");
							return false;
						} else {
							sender.sendMessage("Wait! You need to use nether portals!!! Oh you're an OP... Sorry, my mistake.");
						}
					}
					((Player) sender).teleport(world.getSpawnLocation());
					sender.sendMessage(ChatColor.GRAY + "Teleporting to "
							+ world.getName());
					return true;

				} else {
					sender.sendMessage(ChatColor.RED + "World \"" + args[0]
							+ "\" was not found. Check Spelling.");
					sender.sendMessage(ChatColor.GREEN + "Available worlds:");
					for (World w : plugin.getServer().getWorlds()) {
						sender.sendMessage(ChatColor.YELLOW + w.getName());
					}
				}
			}
		}
		return false;
	}

}
