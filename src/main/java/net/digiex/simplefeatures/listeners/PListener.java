package net.digiex.simplefeatures.listeners;

import java.util.List;
import java.util.logging.Level;

import net.digiex.simplefeatures.SFHome;
import net.digiex.simplefeatures.SFInventory;
import net.digiex.simplefeatures.SFMail;
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
			plugin.getServer()
					.getScheduler()
					.scheduleAsyncDelayedTask(plugin,
							new AskSetHomeTask(player));
		}
	}

	private class AskSetHomeTask implements Runnable {
		private Player player;

		public AskSetHomeTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {

			String answer = SFPlugin.questioner.ask(this.player,
					ChatColor.YELLOW
							+ "Do you want to set your home to this bed?",
					"yes", "no");
			if (answer == "yes") {
				SFHome home = plugin
						.getDatabase()
						.find(SFHome.class)
						.where()
						.ieq("worldName",
								player.getLocation().getWorld().getName())
						.ieq("playerName", player.getName()).findUnique();
				if (home == null) {
					home = new SFHome();
					home.setPlayer(player);
				}
				home.setLocation(player.getLocation());
				plugin.getDatabase().save(home);
				player.sendMessage(ChatColor.YELLOW
						+ "Your home for this world is now set to this bed!");
			} else {
				player.sendMessage(ChatColor.GRAY
						+ "Setting home here cancelled.");
			}
		}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!e.getPlayer().isWhitelisted()) {
			e.setJoinMessage(ChatColor.YELLOW + e.getPlayer().getDisplayName()
					+ " tried to join, but is not on whitelist!");
			e.getPlayer().kickPlayer(
					ChatColor.RED + "Not on whitelist, " + ChatColor.WHITE
							+ " see " + ChatColor.AQUA
							+ "http://digiex.net/minecraft");
			return;
		}
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
		String plistname = e.getPlayer().getDisplayName();
		if (plistname.length() < 17) {
			e.getPlayer().setPlayerListName(plistname);
		}
		List<SFMail> msgs;
			msgs = plugin.getDatabase().find(SFMail.class).where()
					.ieq("toPlayer", e.getPlayer().getName()).findList();
		if (!msgs.isEmpty()) {
		e.getPlayer().sendMessage(ChatColor.AQUA+"You have "+msgs.size()+" new mail! Type /read to view.");
		}
	}

	@Override
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
		if (e.isCancelled()) {
			return;
		}
		SFPlugin.log(Level.INFO, e.getPlayer().getName()
				+ "'s gamemode changed to " + e.getNewGameMode().toString());
		if (!(e.getPlayer().getHealth() > 0)) {

			e.getPlayer().getInventory().clear();
			e.getPlayer().setHealth(20);
			e.getPlayer().setFoodLevel(20);
		}

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

		e.getPlayer().getInventory().clear();
		try {
			if (!(inv.getHealth() > 0)) {
				e.getPlayer().setHealth(20);
				e.getPlayer().setFoodLevel(20);
			} else {
				inv = plugin.getSFInventory(e.getNewGameMode(), e.getPlayer()
						.getName());
				ItemStack[] contents = SFPlugin.stringToItemStack(inv
						.getInventory());
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
		} catch (NullPointerException ex) {
			SFPlugin.log(Level.INFO, "Some inventory contents were null for "
					+ e.getPlayer().getName() + ". Stacktrace for debugging:");
			ex.printStackTrace();
		}
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
		Teleported(event.getPlayer().getLocation().getWorld(), event
				.getRespawnLocation().getWorld(), event.getPlayer());
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (!(e.isCancelled()) && e.getTo() != null) {
			Teleported(e.getFrom().getWorld(), e.getTo().getWorld(),
					e.getPlayer());
			if (!plugin.isGod(e.getPlayer().getName())) {
				plugin.setGodOn(e.getPlayer().getName(), 200);
			}
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