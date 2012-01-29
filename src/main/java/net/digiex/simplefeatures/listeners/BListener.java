package net.digiex.simplefeatures.listeners;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BListener implements Listener {

	SFPlugin plugin;

	public BListener(SFPlugin parent) {
		plugin = parent;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		SFPlayer sfp = new SFPlayer(p);
		if (!p.isOp()) {
			boolean inspawn = SFPlugin.isInSpawnProtect(event.getBlock()
					.getLocation(), plugin);
			if (inspawn) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED
						+ sfp.translateString("general.inspawnprotect"));
			}
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				if (!event.isCancelled()) {
					if (event.getBlock().getY() < Double.valueOf(7)
							&& event.getBlock().getType()
									.equals(Material.BEDROCK)) {
						event.setCancelled(true);
						p.sendMessage(ChatColor.RED
								+ sfp.translateString("general.bedrockblocked.removal"));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		SFPlayer sfp = new SFPlayer(p);
		if (!p.isOp()) {
			boolean inspawn = SFPlugin.isInSpawnProtect(event.getBlock()
					.getLocation(), plugin);
			if (inspawn) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED
						+ sfp.translateString("general.inspawnprotect"));
			}
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				if (!event.isCancelled()) {
					if (event.getBlock().getY() < Double.valueOf(7)
							&& event.getBlock().getType()
									.equals(Material.BEDROCK)) {
						event.setCancelled(true);
						p.sendMessage(ChatColor.RED
								+ sfp.translateString("general.bedrockblocked.placement"));
					}
				}
			}
		}
	}
}
