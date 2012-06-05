package net.digiex.simplefeatures.listeners;

import java.util.List;
import java.util.logging.Level;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
	private final SFPlugin plugin;

	public InventoryListener(SFPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getResult() == Result.DENY) {
			return;
		}
		List<Integer> disallowedItemsList = plugin.getConfig().getIntegerList(
				"advanced.disalloweditems");
		if (disallowedItemsList != null) {
			ItemStack currentItem = e.getCurrentItem();
			if (currentItem != null) {
				if (disallowedItemsList.contains(currentItem.getTypeId())) {
					SFPlugin.log(Level.WARNING,
							e.getWhoClicked().getName() + " tried to take "
									+ currentItem.getAmount() + "x disallowed "
									+ currentItem.getType().toString()
									+ " with id " + currentItem.getTypeId()
									+ ":" + currentItem.getDurability());
					Bukkit.getPlayer(e.getWhoClicked().getName()).sendMessage(
							ChatColor.RED + "Sorry, that item is disallowed.");
					e.setResult(Result.DENY);
					e.setCancelled(true);
					e.setCurrentItem(null);
					return;
				}
			}
		}
	}
}
