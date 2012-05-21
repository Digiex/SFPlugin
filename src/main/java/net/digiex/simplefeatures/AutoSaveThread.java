package net.digiex.simplefeatures;

import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class AutoSaveThread implements Runnable {
	public static SFPlugin plugin;

	public AutoSaveThread(SFPlugin instance) {
		plugin = instance;
	}

	@Override
	public void run() {
		int players = 0;
		int worlds = 0;
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.saveData();
			SFPlayer.getSFPlayer(p).saveInventory();
			players++;

		}
		for (World w : plugin.getServer().getWorlds()) {
			w.save();
			worlds++;
		}
		SFPlugin.log(Level.INFO, players + " players and " + worlds
				+ " worlds saved.");
	}
}
