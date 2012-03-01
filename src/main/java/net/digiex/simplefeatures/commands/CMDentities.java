package net.digiex.simplefeatures.commands;

import java.util.HashMap;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CMDentities implements CommandExecutor {
	SFPlugin plugin;

	public CMDentities(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender.isOp()) {
			World w;
			if (args.length < 1) {
				if (sender instanceof Player) {
					w = ((Player) sender).getWorld();
				} else {
					return false;
				}
			} else {
				w = plugin.getServer().getWorld(args[0]);
			}
			if (w != null) {
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("clear")) {
						sender.sendMessage("Clearing has been disabled since it causes too much trouble for players");
					}
				}
				sender.sendMessage("Entities for " + w.getName());
				HashMap<String, Integer> entities = new HashMap<String, Integer>();
				for (Entity e : w.getEntities()) {
					String name = "UNKNOWN";
					if (e.getType() != null) {
						name = e.getType().toString();
					} else if (e.getClass().getCanonicalName() != null) {
						name = e.getClass().getCanonicalName();
					}
					int count = 0;
					if (entities.get(name) != null) {
						count = entities.get(name);
					}
					entities.put(name, count + 1);
				}
				sender.sendMessage(ChatColor.YELLOW + "All entities: "
						+ ChatColor.AQUA + w.getEntities().size());
				for (String name : entities.keySet()) {
					sender.sendMessage(ChatColor.YELLOW
							+ name.toLowerCase().replace("_", " ") + ": "
							+ ChatColor.AQUA + entities.get(name));
				}
				return true;
			}
		}
		return false;
	}
}
