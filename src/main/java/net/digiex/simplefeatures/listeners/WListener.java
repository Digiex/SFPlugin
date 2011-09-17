package net.digiex.simplefeatures.listeners;

import java.util.logging.Level;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldSaveEvent;

public class WListener extends WorldListener{
	SFPlugin plugin;
	public WListener(SFPlugin parent){
		plugin = parent;
	}
	@Override
	public void onWorldSave(WorldSaveEvent event) {
		SFPlugin.log(Level.INFO, "Saving inventories...");
	}
}
