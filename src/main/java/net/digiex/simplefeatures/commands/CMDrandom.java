package net.digiex.simplefeatures.commands;

import java.util.Random;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wimbli.WorldBorder.BorderData;

public class CMDrandom implements CommandExecutor {

	private final SFPlugin parent;

	public CMDrandom(SFPlugin parent) {
		this.parent = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			p.sendMessage(ChatColor.YELLOW
					+ "Calculating the meaning of life...");
			final Location l = new Location(p.getWorld(),
					randGen(p.getWorld()), 128, randGen(p.getWorld()));
			if (SFPlugin.worldBorderPlugin != null) {
				BorderData bData = SFPlugin.worldBorderPlugin.GetWorldBorder(l
						.getWorld().getName());
				if (bData != null) {
					if (!bData.insideBorder(l)) {
						p.sendMessage(ChatColor.RED
								+ "You seem to want to go somewhere, but sadly it's outside of the border.");
						return true;
					}
				}
			}
			Chunk ch = p.getWorld().getChunkAt(l);
			String where = "place";
			if (!ch.isLoaded()) {
				if (!ch.load()) {
					p.sendMessage("Failed to load chunk!");
					return true;
				}
			}
			try {
				Block hB = p.getWorld().getHighestBlockAt(l);
				if (hB.getBiome() != null) {
					if (hB.getBiome().toString() != null) {
						where = hB.getBiome().toString().toLowerCase()
								.replace("_", " ");
					}
				}
				int taskId = parent
						.getServer()
						.getScheduler()
						.scheduleAsyncDelayedTask(
								parent,
								new SFTeleportTask(p, p, null,
										hB.getLocation(), false, null,
										"Teleporting you to some random "
												+ where));
				SFTeleportTask.teleporters.put(p.getName(), taskId);
			} catch (Exception ex) {
				p.sendMessage(ChatColor.AQUA
						+ "The gods have spoken, no teleport this time!");
				ch.unload(true);
			}
			return true;
		}
		return false;
	}

	private int randGen(World w) {
		int aStart = parent.getConfig().getInt(
				"worlds." + w.getName() + ".randmin", 1000);
		int aEnd = parent.getConfig().getInt(
				"worlds." + w.getName() + ".randmax", 2000);
		Random aRandom = new Random();
		// get the range, casting to long to avoid overflow problems
		long range = (long) aEnd - (long) aStart + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * aRandom.nextDouble());
		int randomNumber = (int) (fraction + aStart);
		boolean randBool = (new Random()).nextBoolean();
		if (randBool) {
			randomNumber = -randomNumber;
		}
		return randomNumber;
	}
}
