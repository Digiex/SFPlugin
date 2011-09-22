package net.digiex.simplefeatures.listeners;

import java.util.logging.Level;

import net.digiex.simplefeatures.SFHome;
import net.digiex.simplefeatures.SFInventory;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class PListener extends PlayerListener {

	SFPlugin plugin;
	public BukkitScheduler tasks;

	public PListener(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (event.getClickedBlock().getType() == Material.BED_BLOCK) {
			Player player = event.getPlayer();
			SFHome home = plugin
					.getDatabase()
					.find(SFHome.class)
					.where()
					.ieq("worldName", player.getLocation().getWorld().getName())
					.ieq("playerName", player.getName()).findUnique();
			if (home == null) {
				home = new SFHome();
				home.setPlayer(player);
			}
			home.setLocation(player.getLocation());
			plugin.getDatabase().save(home);
			player.sendMessage(ChatColor.YELLOW
					+ "Your home for this world is now set to this bed!");
		}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent e) {
		setGameMode(e.getPlayer(), e.getPlayer().getWorld());
		if (e.getPlayer().isOp()) {
			e.getPlayer().setDisplayName(
					ChatColor.AQUA + e.getPlayer().getName() + ChatColor.WHITE);
		} else {
			e.getPlayer()
					.setDisplayName(
							ChatColor.GREEN + e.getPlayer().getName()
									+ ChatColor.WHITE);
		}
	}

	@Override
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
		if (e.isCancelled()) {
			return;
		}
		SFPlugin.log(Level.INFO, e.getPlayer().getName()
				+ "'s gamemode changed to " + e.getNewGameMode().toString());
		SFInventory inv = new SFInventory();
		inv.setGameMode(e.getPlayer().getGameMode());
		inv.setPlayerName(e.getPlayer().getName());
		inv.setInventory(SFPlugin.itemStackToString(e.getPlayer()
				.getInventory().getContents()));
		inv.setArmor(SFPlugin.itemStackToString(e.getPlayer().getInventory()
				.getArmorContents()));
		inv.setHealth(e.getPlayer().getHealth());
		inv.setFood(e.getPlayer().getFoodLevel());
		plugin.updateSFInventory(inv);

		inv = plugin
				.getSFInventory(e.getNewGameMode(), e.getPlayer().getName());
		e.getPlayer().getInventory().clear();
		ItemStack[] contents = SFPlugin.stringToItemStack(inv.getInventory());
		if (contents != null) {
			e.getPlayer().getInventory().setContents(contents);
		}
		ItemStack[] armor = SFPlugin.stringToItemStack(inv.getArmor());
		if (armor != null) {
			e.getPlayer().getInventory().setArmorContents(armor);
		}
		e.getPlayer().setHealth(inv.getHealth());
		e.getPlayer().setFoodLevel(inv.getFood());

	}

	@Override
	public void onPlayerPortal(PlayerPortalEvent e) {
		if (!(e.isCancelled()) && e.getTo() != null) {
			Teleported(e.getFrom().getWorld(), e.getTo().getWorld(),
					e.getPlayer());
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {

		SFHome home = plugin
				.getDatabase()
				.find(SFHome.class)
				.where()
				.ieq("worldName",
						event.getPlayer().getLocation().getWorld().getName())
				.ieq("playerName", event.getPlayer().getName()).findUnique();
		if (home != null) {
			event.setRespawnLocation(home.getLocation());
		}

	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (!(e.isCancelled()) && e.getTo() != null) {
			Teleported(e.getFrom().getWorld(), e.getTo().getWorld(),
					e.getPlayer());
			if (!plugin.gods.containsKey(e.getPlayer().getName())) {
				GodTask task = new GodTask(plugin, e.getPlayer());
				int id = plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(plugin, task, 200);
				task.setId(id);
				plugin.gods.put(e.getPlayer().getName(), true);
			}
		}
	}

	public class GodTask implements Runnable {

		private SFPlugin plugin;
		private Player player;
		private int id;

		public GodTask(SFPlugin plugin, Player player) {
			this.plugin = plugin;
			this.player = player;
		}

		@Override
		public void run() {
			plugin.gods.remove(player.getName());
			plugin.getServer().getScheduler().cancelTask(id);
		}

		public Player getPlayer() {
			return this.player;
		}

		public int getId() {
			return this.id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

	public void setGameMode(Player player, World world) {
		int gamemode = plugin.config.getInt("worlds." + world.getName()
				+ ".gamemode", 5);
		if (gamemode == 1) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				player.setGameMode(GameMode.CREATIVE);
				SFPlugin.log(Level.INFO, "Gamemode set to creative for "
						+ player.getName());
			}
		} else if (gamemode == 0) {
			if (player.getGameMode() != GameMode.SURVIVAL) {
				player.setGameMode(org.bukkit.GameMode.SURVIVAL);

				SFPlugin.log(Level.INFO, "Gamemode set to survival for "
						+ player.getName());
			}
		} else {
			if (player.getGameMode() != plugin.getServer().getDefaultGameMode()) {
				player.setGameMode(plugin.getServer().getDefaultGameMode());
				SFPlugin.log(Level.INFO, "Gamemode set to default ("
						+ plugin.getServer().getDefaultGameMode().toString()
						+ ") for " + player.getName());
			}
		}
	}

	public void Teleported(World from, World to, Player player) {
		if (from != to) {
			setGameMode(player, to);
			SFPlugin.log(Level.INFO, player.getName() + " teleported from "
					+ from.getName() + " to " + to.getName());
		}
	}
}