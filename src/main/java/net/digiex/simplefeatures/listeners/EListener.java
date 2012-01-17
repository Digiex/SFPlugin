package net.digiex.simplefeatures.listeners;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ItemSpawnEvent;
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
				String mainWorld = l.getWorld().getName()
						.replace("_the_end", "");
				l.setWorld(plugin.getServer().getWorld(mainWorld));
				l.setY(200);
				if (SFPlugin.worldBorderPlugin != null) {
					if (SFPlugin.worldBorderPlugin.GetWorldBorder(mainWorld) != null) {
						if (!SFPlugin.worldBorderPlugin.GetWorldBorder(
								mainWorld).insideBorder(l)) {
							return;
						}
					}
				}
				if (l.getWorld() != null) {
					ent.teleport(l);
				} else {
					return;
				}
			} else {
				ent.setFallDistance(0);
				Location tpLoc = null;
				int i = 0;
				while (tpLoc == null) {
					i++;
					if (i > 20) {
						tpLoc = ent.getWorld().getSpawnLocation();
					} else {
						Block hB = ent.getWorld().getHighestBlockAt(
								ent.getLocation().getBlockX() + i,
								ent.getLocation().getBlockZ() + i);

						if (hB.getY() > 1) {
							tpLoc = hB.getLocation();
						}
					}
				}
				if (SFPlugin.worldBorderPlugin != null) {
					if (SFPlugin.worldBorderPlugin.GetWorldBorder(tpLoc
							.getWorld().getName()) != null) {
						if (!SFPlugin.worldBorderPlugin.GetWorldBorder(
								tpLoc.getWorld().getName()).insideBorder(tpLoc)) {
							return;
						}
					}
				}
				ent.teleport(tpLoc);
			}
			e.setCancelled(true);

		}

	}

	@Override
	public void onEntityExplode(EntityExplodeEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (plugin.getConfig().getBoolean(
				"worlds." + e.getLocation().getWorld().getName()
						+ ".explosions", false)) {
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

	@Override
	public void onItemSpawn(ItemSpawnEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (plugin.getConfig()
				.getBoolean(
						"worlds." + e.getLocation().getWorld().getName()
								+ ".itemdrops", true)) {
			return;
		}
		e.setCancelled(true);
	}
}