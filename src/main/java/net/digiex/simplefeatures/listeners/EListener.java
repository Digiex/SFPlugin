package net.digiex.simplefeatures.listeners;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class EListener extends EntityListener {

	private final SFPlugin plugin;

	public EListener(SFPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onEntityExplode(EntityExplodeEvent e) {
		e.setCancelled(true);
		if (e.getEntity() instanceof org.bukkit.entity.TNTPrimed) {
			e.getLocation()
					.getWorld()
					.dropItemNaturally(e.getLocation(),
							new ItemStack(Material.TNT, 1));
		}
	}
}