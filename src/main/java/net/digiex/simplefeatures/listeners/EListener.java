package net.digiex.simplefeatures.listeners;

import java.util.List;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class EListener implements Listener {

	private final SFPlugin plugin;

	public EListener(SFPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Entity ent = e.getEntity();
		if (plugin.getConfig().getBoolean("features.voidtp", true)) {
			if (e.getCause() == DamageCause.VOID) {
				if (ent.getLocation().getBlock().getBiome() == Biome.SKY) {
					Location l = ent.getLocation();
					World mainWorld = Bukkit.getWorld("0");
					l.setWorld(mainWorld);
					l.setY(200);
					if (SFPlugin.worldBorderPlugin != null) {
						if (SFPlugin.worldBorderPlugin.GetWorldBorder(mainWorld
								.getName()) != null) {
							if (!SFPlugin.worldBorderPlugin.GetWorldBorder(
									mainWorld.getName()).insideBorder(l)) {
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
					try {
						tpLoc = Util.getSafeDestination(ent.getLocation());
					} catch (Exception e1) {
						tpLoc = ent.getWorld().getSpawnLocation();
					}

					if (SFPlugin.worldBorderPlugin != null) {
						if (SFPlugin.worldBorderPlugin.GetWorldBorder(tpLoc
								.getWorld().getName()) != null) {
							if (!SFPlugin.worldBorderPlugin.GetWorldBorder(
									tpLoc.getWorld().getName()).insideBorder(
									tpLoc)) {
								return;
							}
						}
					}
					ent.teleport(tpLoc);
				}
				e.setCancelled(true);

			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemSpawn(ItemSpawnEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (!plugin.getConfig()
				.getBoolean(
						"worlds." + e.getLocation().getWorld().getName()
								+ ".itemdrops", true)) {
			e.setCancelled(true);
			return;
		}
		List<Integer> disallowedItemsList = plugin.getConfig().getIntegerList(
				"advanced.disalloweditems");
		if (disallowedItemsList != null) {
			if (disallowedItemsList.contains(e.getEntity().getItemStack()
					.getTypeId())) {
				e.setCancelled(true);
				return;
			}
		}
	}
}