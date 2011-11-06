package net.digiex.simplefeatures.listeners;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class EListener extends EntityListener {

	private final SFPlugin plugin;

	public EListener(SFPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Entity ent = e.getEntity();
		if (e.getCause() == DamageCause.VOID) {
			if (ent.getLocation().getBlock().getBiome() == Biome.SKY) {
				Location l = ent.getLocation();
				l.setWorld(plugin.getServer().getWorld("Survival"));
				l.setY(200);
				ent.teleport(l);
			} else {
				ent.teleport(ent
						.getWorld()
						.getHighestBlockAt(ent.getLocation().getBlockX() + 1,
								ent.getLocation().getBlockZ() + 1)
						.getLocation());
			}
			e.setCancelled(true);

		}

	}

	@Override
	public void onEntityExplode(EntityExplodeEvent e) {
		if (e.isCancelled()) {
			return;
		}
		e.setCancelled(true);
		if (e.getEntity() instanceof org.bukkit.entity.TNTPrimed) {
			e.getLocation()
					.getWorld()
					.dropItemNaturally(e.getLocation(),
							new ItemStack(Material.TNT, 1));
		}
	}
}