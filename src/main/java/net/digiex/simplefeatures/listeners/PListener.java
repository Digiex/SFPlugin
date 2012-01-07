package net.digiex.simplefeatures.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import net.digiex.simplefeatures.SFCompassPoint;
import net.digiex.simplefeatures.SFHome;
import net.digiex.simplefeatures.SFInventory;
import net.digiex.simplefeatures.SFLocation;
import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitWorker;

import com.wimbli.WorldBorder.BorderData;

public class PListener extends PlayerListener {

	private class AskSetHomeTask implements Runnable {
		private final Player player;
		private final Location homeLoc;

		public AskSetHomeTask(Player player, Location homeLoc) {
			this.player = player;
			this.homeLoc = homeLoc;
		}

		@Override
		public void run() {
			String answer = SFPlugin.questioner.ask(player, ChatColor.YELLOW
					+ "Do you want to set your home to this bed?", "set",
					"cancel");
			if (answer == "set") {

				com.avaje.ebean.EbeanServer db = plugin.getDatabase();
				db.beginTransaction();

				try {
					SFHome home = db
							.find(SFHome.class)
							.where()
							.ieq("worldName",
									player.getLocation().getWorld().getName())
							.ieq("playerName", player.getName()).findUnique();
					boolean isUpdate = false;

					if (home == null) {
						player.sendMessage(ChatColor.YELLOW
								+ "Home for this world created!");

						home = new SFHome();
						home.setPlayer(player);
					} else {

						player.sendMessage(ChatColor.YELLOW
								+ "Home for this world updated!");

						isUpdate = true;
					}

					home.setLocation(homeLoc);
					player.setCompassTarget(homeLoc);
					if (isUpdate) {
						db.update(home, homeUpdateProps);
					}
					db.save(home);
					db.commitTransaction();
				} finally {
					db.endTransaction();
				}
			} else {
				player.sendMessage(ChatColor.GRAY
						+ "Setting home here cancelled.");
			}
			homeTasks.remove(player.getName());
		}
	}

	SFPlugin plugin;

	public BukkitScheduler tasks;

	public static HashMap<String, Integer> homeTasks = new HashMap<String, Integer>();

	private static final Set<String> homeUpdateProps;
	static {
		homeUpdateProps = new HashSet<String>();
		homeUpdateProps.add("x");
		homeUpdateProps.add("y");
		homeUpdateProps.add("z");
		homeUpdateProps.add("yaw");
		homeUpdateProps.add("pitch");
		homeUpdateProps.add("world_name");
	}
	private static final Set<String> invUpdateProps;
	static {
		invUpdateProps = new HashSet<String>();
		invUpdateProps.add("player_name");
		invUpdateProps.add("inventory");
		invUpdateProps.add("armor");
		invUpdateProps.add("health");
		invUpdateProps.add("food");
		invUpdateProps.add("game_mode");
		invUpdateProps.add("experience");
		invUpdateProps.add("exhaustion");
		invUpdateProps.add("fire_ticks");
		invUpdateProps.add("level");
		invUpdateProps.add("remaining_air");
		invUpdateProps.add("saturation");
		invUpdateProps.add("total_experience");
	}

	public static void updatePlayerNameColour(Player p, SFPlugin plugin) {
		if (!p.isOp()) {
			plugin.permissionAttachements.get(p.getName()).setPermission(
					"bukkit.command.plugins", false);
			plugin.permissionAttachements.get(p.getName()).setPermission(
					"bukkit.command.version", false);
		} else {

			plugin.permissionAttachements.get(p.getName()).setPermission(
					"bukkit.command.plugins", true);
			plugin.permissionAttachements.get(p.getName()).setPermission(
					"bukkit.command.version", true);
		}
		if (p.isOp()) {
			p.setDisplayName(ChatColor.AQUA + p.getName() + ChatColor.WHITE);
		} else {
			p.setDisplayName(ChatColor.GREEN + p.getName() + ChatColor.WHITE);
		}
		String plistname = p.getDisplayName();
		if (plistname.length() < 17) {
			p.setPlayerListName(plistname);
		}
	}

	private final HashMap<String, Integer> activeCompassPoints = new HashMap<String, Integer>();

	public PListener(SFPlugin parent) {
		plugin = parent;
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
		com.avaje.ebean.EbeanServer db = plugin.getDatabase();
		db.beginTransaction();
		boolean isUpdate = false;
		try {
			SFInventory inv = db.find(SFInventory.class).where()
					.eq("gameMode", e.getPlayer().getGameMode().getValue())
					.ieq("playerName", e.getPlayer().getName()).findUnique();
			if (inv == null) {
				inv = new SFInventory();
			} else {
				isUpdate = true;
			}
			inv.setGameMode(e.getPlayer().getGameMode().getValue());
			inv.setPlayerName(e.getPlayer().getName());
			inv.setInventory(SFPlugin.itemStackToString(e.getPlayer()
					.getInventory().getContents()));
			inv.setArmor(SFPlugin.itemStackToString(e.getPlayer()
					.getInventory().getArmorContents()));
			inv.setHealth(e.getPlayer().getHealth());
			inv.setFood(e.getPlayer().getFoodLevel());
			inv.setExp(e.getPlayer().getExp());
			inv.setExhaustion(e.getPlayer().getExhaustion());
			inv.setFireTicks(e.getPlayer().getFireTicks());
			inv.setLevel(e.getPlayer().getLevel());
			inv.setRemainingAir(e.getPlayer().getRemainingAir());
			inv.setSaturation(e.getPlayer().getSaturation());
			inv.setTotalExperience(e.getPlayer().getTotalExperience());
			if (isUpdate) {
				db.update(inv, invUpdateProps);
			}
			db.save(inv);
			db.commitTransaction();
		} catch (Exception ex) {
			e.getPlayer().kickPlayer(
					ChatColor.RED + "Server error, contact admin: "
							+ ex.getMessage());
			ex.printStackTrace();
			e.setCancelled(true);
		} finally {
			db.endTransaction();
		}
		e.getPlayer().getInventory().clear();
		try {
			SFInventory inv = db.find(SFInventory.class).where()
					.eq("gameMode", e.getNewGameMode().getValue())
					.ieq("playerName", e.getPlayer().getName()).findUnique();
			if (inv != null) {
				ItemStack[] contents = SFPlugin.stringToItemStack(inv
						.getInventory());
				if (contents != null) {
					e.getPlayer().getInventory().setContents(contents);
				}
				ItemStack[] armor = SFPlugin.stringToItemStack(inv.getArmor());
				if (armor != null) {
					e.getPlayer().getInventory().setArmorContents(armor);
				}
				if (!(inv.getHealth() > 0)) {
					e.getPlayer().setHealth(20);
					e.getPlayer().setFoodLevel(20);
				} else {
					e.getPlayer().setHealth(inv.getHealth());
					e.getPlayer().setFoodLevel(inv.getFood());
				}
				e.getPlayer().setExp(inv.getExp());
				e.getPlayer().setExhaustion(inv.getExhaustion());
				e.getPlayer().setFireTicks(inv.getFireTicks());
				e.getPlayer().setLevel(inv.getLevel());
				e.getPlayer().setRemainingAir(inv.getRemainingAir());
				e.getPlayer().setSaturation(inv.getSaturation());
				e.getPlayer().setTotalExperience(inv.getTotalExperience());
			}

		} catch (NullPointerException ex) {
			SFPlugin.log(Level.INFO, "Some inventory contents were null for "
					+ e.getPlayer().getName());
			// ex.printStackTrace();
		}
		e.getPlayer().saveData();
	}

	@Override
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
						"You have no compass points. Type /cp for more help");
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
						ChatColor.YELLOW + "Your compass points now to "
								+ ChatColor.AQUA + cp.getPointName());
				event.setCancelled(true);
				return;
			}
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.isCancelled() && !event.hasBlock()) {
				return;
			}
			if (event.getClickedBlock().getType() == Material.BED_BLOCK) {
				Player player = event.getPlayer();
				if (player.getWorld().getName().contains("_nether")
						|| player.getWorld().getName().contains("_the_end")) {
					return;
				}
				if (homeTasks.containsKey(player.getName())) {
					plugin.getServer().getScheduler()
							.cancelTask(homeTasks.get(player.getName()));
				}
				int taskId = plugin
						.getServer()
						.getScheduler()
						.scheduleAsyncDelayedTask(
								plugin,
								new AskSetHomeTask(player, player.getLocation()));
				homeTasks.put(player.getName(), taskId);
			}
		}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent e) {

		PermissionAttachment attachment = e.getPlayer().addAttachment(plugin);
		plugin.permissionAttachements.put(e.getPlayer().getName(), attachment);
		updatePlayerNameColour(e.getPlayer(), plugin);
		// updateCompass(e.getPlayer(), e.getPlayer().getWorld());
		setGameMode(e.getPlayer(), e.getPlayer().getWorld());
		List<SFMail> msgs;
		msgs = plugin.getDatabase().find(SFMail.class).where()
				.ieq("toPlayer", e.getPlayer().getName()).findList();
		if (!msgs.isEmpty()) {
			e.getPlayer().sendMessage(
					ChatColor.AQUA + "You have " + msgs.size()
							+ " new mail! Type /read to view.");
		}
	}

	@Override
	public void onPlayerKick(PlayerKickEvent e) {
		System.out
				.println(e.getPlayer().getName() + " lost connection: kicked");
		if (!e.getPlayer().isWhitelisted()) {
			e.setLeaveMessage(null);
		}
		try {
			for (BukkitWorker worker : plugin.getServer().getScheduler()
					.getActiveWorkers()) {
				if (worker.getOwner() instanceof SFPlugin) {
					if (SFTeleportTask.teleporters.get(e.getPlayer().getName())
							.equals(worker.getTaskId())) {

						plugin.getServer().getScheduler()
								.cancelTask(worker.getTaskId());

					}
				}
			}
		} catch (Exception ex) {
			SFPlugin.log(Level.INFO, "Tried to cancel a teleport of "
					+ e.getPlayer().getName()
					+ " but it had been cancelled before (" + ex.getMessage()
					+ ")");
		}
		if (plugin.permissionAttachements.containsKey(e.getPlayer().getName())) {
			e.getPlayer().removeAttachment(
					plugin.permissionAttachements.get(e.getPlayer().getName()));
			plugin.permissionAttachements.remove(e.getPlayer().getName());
		}
		if (SFPlugin.clientAddons.contains((e.getPlayer().getName()))) {
			SFPlugin.clientAddons.remove(e.getPlayer().getName());
		}
	}

	@Override
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
												+ "You seem to want to go somewhere, but sadly it's outside of the border.");
						e.setCancelled(true);
						return;
					}
				}
			}
			Teleported(e.getFrom().getWorld(), e.getTo().getWorld(),
					e.getPlayer(), e);
		}
	}

	@Override
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

	@Override
	public void onPlayerQuit(PlayerQuitEvent e) {
		try {
			for (BukkitWorker worker : plugin.getServer().getScheduler()
					.getActiveWorkers()) {
				if (worker.getOwner() instanceof SFPlugin) {
					if (SFTeleportTask.teleporters.get(e.getPlayer().getName())
							.equals(worker.getTaskId())) {

						plugin.getServer().getScheduler()
								.cancelTask(worker.getTaskId());

					}
				}
			}
		} catch (Exception ex) {
			SFPlugin.log(Level.INFO, "Tried to cancel a teleport of "
					+ e.getPlayer().getName()
					+ " but it had been cancelled before (" + ex.getMessage()
					+ ")");
		}
		if (plugin.permissionAttachements.containsKey(e.getPlayer().getName())) {
			e.getPlayer().removeAttachment(
					plugin.permissionAttachements.get(e.getPlayer().getName()));
			plugin.permissionAttachements.remove(e.getPlayer().getName());
		}
		if (SFPlugin.clientAddons.contains((e.getPlayer().getName()))) {
			SFPlugin.clientAddons.remove(e.getPlayer().getName());
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		String wname = event.getPlayer().getLocation().getWorld().getName();
		if (wname.contains("_nether") || wname.contains("_the_end")) {
			wname = "Survival";
		}
		SFHome home = plugin.getDatabase().find(SFHome.class).where()
				.ieq("worldName", wname)
				.ieq("playerName", event.getPlayer().getName()).findUnique();
		if (home != null) {
			event.setRespawnLocation(home.getLocation());
		} else {
			event.setRespawnLocation(plugin.getServer().getWorld(wname)
					.getSpawnLocation());
		}
		Teleported(event.getPlayer().getWorld(), event.getRespawnLocation()
				.getWorld(), event.getPlayer(), event);
	}

	@Override
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
												+ "You seem to want to go somewhere, but sadly it's outside of the border.");
						e.setCancelled(true);
						return;
					}
				}
			}
			e.getPlayer().setNoDamageTicks(200);
			// if (e.getPlayer().getVehicle() != null) {
			// if (e.getFrom().distance(e.getTo()) > 30) {
			// if (e.getPlayer().getVehicle().eject()) {
			// e.getPlayer()
			// .sendMessage(
			// "Sorry, you cannot take your "
			// + e.getPlayer().getVehicle()
			// .getClass()
			// .getSimpleName()
			// + " with you.");
			// }
			// }
			// }
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
				com.avaje.ebean.EbeanServer db = plugin.getDatabase();
				db.beginTransaction();

				try {
					SFLocation lastLoc = db.find(SFLocation.class).where()
							.ieq("worldName", from.getName())
							.ieq("playerName", player.getName()).findUnique();
					boolean isUpdate = false;

					if (lastLoc == null) {
						lastLoc = new SFLocation();
						lastLoc.setPlayerName(player.getName());
					} else {
						isUpdate = true;
					}
					Location loc = player.getLocation();
					lastLoc.setX(loc.getX());
					lastLoc.setY(loc.getY());
					lastLoc.setZ(loc.getZ());
					lastLoc.setYaw(loc.getYaw());
					lastLoc.setPitch(loc.getPitch());
					lastLoc.setWorldName(loc.getWorld().getName());
					if (isUpdate) {
						db.update(lastLoc, homeUpdateProps);
					}
					db.save(lastLoc);
					db.commitTransaction();
				} finally {
					db.endTransaction();
				}
			}
			setGameMode(player, to);
			// updateCompass(player, to);
		}
	}

	public void updateCompass(Player p, World toWorld) {
		SFHome home = plugin.getDatabase().find(SFHome.class).where()
				.ieq("worldName", toWorld.getName())
				.ieq("playerName", p.getName()).findUnique();
		if (home != null) {
			p.setCompassTarget(home.getLocation());
		} else {
			p.setCompassTarget(toWorld.getSpawnLocation());
		}
	}
}