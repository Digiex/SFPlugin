package net.digiex.simplefeatures.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import net.digiex.simplefeatures.SFCompassPoint;
import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.Util;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitScheduler;

import com.wimbli.WorldBorder.BorderData;

public class PListener implements Listener {

	SFPlugin plugin;

	public BukkitScheduler tasks;

	private final HashMap<String, Integer> activeCompassPoints = new HashMap<String, Integer>();

	public PListener(SFPlugin parent) {
		plugin = parent;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
		if (e.isCancelled()) {
			return;
		}
		SFPlayer sfp = SFPlayer.getSFPlayer(e.getPlayer());
		SFPlugin.log(Level.INFO, e.getPlayer().getName()
				+ "'s gamemode changed to " + e.getNewGameMode().toString());
		if (!sfp.saveInventory()) {
			e.setCancelled(true);
			return;
		}
		e.getPlayer().getInventory().clear();
		sfp.loadInventory(e.getNewGameMode());
		e.getPlayer().saveData();
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_AIR
				&& event.getAction() != Action.RIGHT_CLICK_AIR
				&& event.getAction() != Action.RIGHT_CLICK_BLOCK
				&& event.getAction() != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		if (event.hasItem() && event.getMaterial() == Material.COMPASS) {
			if (event.getPlayer().getWorld().getEnvironment() == Environment.NETHER
					|| event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
				return;
			}
			List<SFCompassPoint> points = plugin.getDatabase()
					.find(SFCompassPoint.class).where()
					.ieq("playerName", event.getPlayer().getName())
					.ieq("worldName", event.getPlayer().getWorld().getName())
					.findList();
			if (points.isEmpty()) {
				event.getPlayer().sendMessage(
						SFPlayer.getSFPlayer(event.getPlayer())
								.translateString("compasspoints.nopoints"));
				return;
			} else {
				Integer point = activeCompassPoints.get(event.getPlayer()
						.getName());
				if (point == null) {
					point = 0;
				} else {
					activeCompassPoints.remove(event.getPlayer().getName());
				}
				if (event.getAction() == Action.LEFT_CLICK_AIR
						|| event.getAction() == Action.LEFT_CLICK_BLOCK) {
					point--;
				} else if (event.getAction() == Action.RIGHT_CLICK_AIR
						|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					point++;
				}
				if (point < 0) {
					point = points.size() - 1;
				} else if (point >= points.size()) {
					point = 0;
				}
				SFCompassPoint cp = points.get(point);
				activeCompassPoints.put(event.getPlayer().getName(), point);
				event.getPlayer().setCompassTarget(
						new Location(event.getPlayer().getWorld(), cp.getX(),
								cp.getY(), cp.getZ(), cp.getYaw(), cp
										.getPitch()));
				event.getPlayer().sendMessage(
						ChatColor.YELLOW
								+ SFPlayer.getSFPlayer(event.getPlayer())
										.translateStringFormat(
												"compasspoints.pointchanged",
												ChatColor.AQUA
														+ cp.getPointName()
														+ ChatColor.WHITE));
				event.setCancelled(true);
				return;
			}
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.isCancelled() && !event.hasBlock()) {
				return;
			}
			if (event.getClickedBlock().getType() == Material.BED_BLOCK) {
				Player player = event.getPlayer();
				if (player.getWorld().getEnvironment() == Environment.NETHER
						|| player.getWorld().getEnvironment() == Environment.THE_END) {
					return;
				}
				player.sendMessage(ChatColor.YELLOW
						+ "Type /sethome if you want to set your home to this bed.");
				(SFPlayer.getSFPlayer(player)).setTempHomeLocation(player
						.getLocation());

			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		StringBuilder minimap = new StringBuilder();
		if (plugin.getConfig().getBoolean("minimap.cavemapping", false)) {
			minimap.append("\2471");
		}
		if (plugin.getConfig()
				.getBoolean("minimap.entitiesradar.player", false)) {
			minimap.append("\2472");
		}
		if (plugin.getConfig()
				.getBoolean("minimap.entitiesradar.animal", false)) {
			minimap.append("\2473");
		}
		if (plugin.getConfig().getBoolean("minimap.entitiesradar.mob", false)) {
			minimap.append("\2474");
		}
		if (plugin.getConfig().getBoolean("minimap.entitiesradar.slime", false)) {
			minimap.append("\2475");
		}
		if (plugin.getConfig().getBoolean("minimap.entitiesradar.squid", false)) {
			minimap.append("\2476");
		}
		if (plugin.getConfig().getBoolean("minimap.entitiesradar.other", false)) {
			minimap.append("\2477");
		}
		if (minimap.length() > 0) {
			e.getPlayer().sendRawMessage(
					"\2470\2470" + minimap.toString() + "\247e\247f");
		}
		SFPlayer sfp = SFPlayer.getSFPlayer(e.getPlayer());
		PermissionAttachment attachment = e.getPlayer().addAttachment(plugin);
		plugin.permissionAttachements.put(e.getPlayer().getName(), attachment);
		sfp.updateNameColour();
		// updateCompass(e.getPlayer(), e.getPlayer().getWorld());
		setGameMode(e.getPlayer(), e.getPlayer().getWorld());
		List<SFMail> msgs;
		msgs = plugin.getDatabase().find(SFMail.class).where()
				.ieq("toPlayer", e.getPlayer().getName()).findList();
		if (!msgs.isEmpty()) {
			e.getPlayer().sendMessage(
					ChatColor.AQUA
							+ SFPlayer.getSFPlayer(e.getPlayer())
									.translateStringFormat(
											"mail.newmailnotifynum",
											msgs.size()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent e) {
		System.out
				.println(e.getPlayer().getName() + " lost connection: kicked");
		if (e.getPlayer().isOp()) {
			e.getPlayer().setOp(false);
		}
		if (!e.getPlayer().isWhitelisted()) {
			e.setLeaveMessage(null);
		}
		if (plugin.permissionAttachements.containsKey(e.getPlayer().getName())) {
			e.getPlayer().removeAttachment(
					plugin.permissionAttachements.get(e.getPlayer().getName()));
			plugin.permissionAttachements.remove(e.getPlayer().getName());
		}
		if (SFPlugin.clientAddons.containsKey((e.getPlayer().getName()))) {
			SFPlugin.clientAddons.remove(e.getPlayer().getName());
		}
		SFPlayer.removeSFPlayerObject(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPortal(PlayerPortalEvent e) {
		if (!(e.isCancelled()) && e.getTo() != null) {
			if (SFPlugin.worldBorderPlugin != null) {
				BorderData bData = SFPlugin.worldBorderPlugin.GetWorldBorder(e
						.getTo().getWorld().getName());
				if (bData != null) {
					if (!bData.insideBorder(e.getTo())) {
						e.getPlayer()
								.sendMessage(
										ChatColor.RED
												+ SFPlayer
														.getSFPlayer(
																e.getPlayer())
														.translateString(
																"teleport.outsideofborder"));
						e.setCancelled(true);
						return;
					}
				}
			}
			Teleported(e.getFrom().getWorld(), e.getTo().getWorld(),
					e.getPlayer(), e);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(PlayerPreLoginEvent e) {
		if (plugin.getConfig().getBoolean("whitelist.enabled", false)) {
			if (!plugin.getServer().getOfflinePlayer(e.getName())
					.isWhitelisted()) {
				plugin.getServer().broadcastMessage(
						ChatColor.YELLOW + e.getName()
								+ " tried to join, but is not on whitelist!");
				e.disallow(
						Result.KICK_WHITELIST,
						plugin.getConfig().getString(
								"whitelist.kickmsg",
								ChatColor.RED + "Not on whitelist, "
										+ ChatColor.WHITE + " please ask an "
										+ ChatColor.AQUA + "admin"
										+ ChatColor.WHITE
										+ " to whitelist you."));
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (e.getPlayer().isOp()) {
			e.getPlayer().setOp(false);
		}
		if (plugin.permissionAttachements.containsKey(e.getPlayer().getName())) {
			e.getPlayer().removeAttachment(
					plugin.permissionAttachements.get(e.getPlayer().getName()));
			plugin.permissionAttachements.remove(e.getPlayer().getName());
		}
		if (SFPlugin.clientAddons.containsKey((e.getPlayer().getName()))) {
			SFPlugin.clientAddons.remove(e.getPlayer().getName());
		}
		SFPlayer.removeSFPlayerObject(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		SFPlayer sfp = SFPlayer.getSFPlayer(event.getPlayer());
		Location homeLoc = sfp.getHomeLoc(event.getPlayer().getWorld());
		if (homeLoc != null) {
			try {
				event.setRespawnLocation(Util.getSafeDestination(homeLoc));
			} catch (Exception e) {
				event.getPlayer().sendMessage(
						ChatColor.RED + "Could not respawn at home: "
								+ e.getMessage());
			}
		} // Let vanilla handle setting the location if no home
		Teleported(event.getPlayer().getWorld(), event.getRespawnLocation()
				.getWorld(), event.getPlayer(), event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (!(e.isCancelled()) && e.getTo() != null) {
			if (SFPlugin.worldBorderPlugin != null) {
				BorderData bData = SFPlugin.worldBorderPlugin.GetWorldBorder(e
						.getTo().getWorld().getName());
				if (bData != null) {
					if (!bData.insideBorder(e.getTo())) {
						e.getPlayer()
								.sendMessage(
										ChatColor.RED
												+ SFPlayer
														.getSFPlayer(
																e.getPlayer())
														.translateString(
																"teleport.outsideofborder"));
						e.setCancelled(true);
						return;
					}
				}
			}
			Teleported(e.getFrom().getWorld(), e.getTo().getWorld(),
					e.getPlayer(), e);
			if (e.getFrom().getWorld() != e.getTo().getWorld()) {
				SFPlugin.log(Level.INFO, e.getPlayer().getName()
						+ " teleported from " + e.getFrom().toString() + " to "
						+ e.getTo().toString());
			}
		}
	}

	public void setGameMode(Player player, World world) {
		int gamemode = plugin.getConfig().getInt(
				"worlds." + world.getName() + ".gamemode", 5);
		if (gamemode == 1) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				player.setGameMode(GameMode.CREATIVE);
			}
		} else if (gamemode == 0) {
			if (player.getGameMode() != GameMode.SURVIVAL) {
				player.setGameMode(org.bukkit.GameMode.SURVIVAL);

			}
		} else {
			if (player.getGameMode() != plugin.getServer().getDefaultGameMode()) {
				player.setGameMode(plugin.getServer().getDefaultGameMode());
			}
		}
	}

	public void Teleported(World from, World to, Player player, Event caller) {
		if (from != to) {
			if (player.getHealth() > 0
					&& !(caller instanceof PlayerRespawnEvent)
					&& player.getLocation().getY() > 1) {
				SFPlayer sfp = SFPlayer.getSFPlayer(player);
				sfp.setLastLocation(player.getLocation());
			}
			setGameMode(player, to);
			// updateCompass(player, to);
		}
	}
}