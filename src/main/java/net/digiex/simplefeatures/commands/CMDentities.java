package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

public class CMDentities implements CommandExecutor {
	SFPlugin plugin;

	public CMDentities(SFPlugin parent) {
		this.plugin = parent;
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
				sender.sendMessage("Entities for " + w.getName());
				int expOrbs = 0;
				int creatures = 0;
				for (Entity e : w.getEntities()) {
					if (e instanceof ExperienceOrb) {
						expOrbs++;
					}
					if (e instanceof Creature) {
						creatures++;
					}
				}
				sender.sendMessage("All entities: " + w.getEntities().size());
				if (expOrbs > 0) {
					sender.sendMessage("Experience orbs: " + expOrbs);
				}
				if (creatures > 0) {
					sender.sendMessage("Creatures: " + expOrbs);
				}
			}
		}
		return false;
	}
}
