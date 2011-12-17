package net.digiex.simplefeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import net.digiex.simplefeatures.commands.CMDabort;
import net.digiex.simplefeatures.commands.CMDadmin;
import net.digiex.simplefeatures.commands.CMDcleanup;
import net.digiex.simplefeatures.commands.CMDclear;
import net.digiex.simplefeatures.commands.CMDentities;
import net.digiex.simplefeatures.commands.CMDhome;
import net.digiex.simplefeatures.commands.CMDlastseen;
import net.digiex.simplefeatures.commands.CMDlisthomes;
import net.digiex.simplefeatures.commands.CMDme;
import net.digiex.simplefeatures.commands.CMDmsg;
import net.digiex.simplefeatures.commands.CMDrandom;
import net.digiex.simplefeatures.commands.CMDread;
import net.digiex.simplefeatures.commands.CMDreply;
import net.digiex.simplefeatures.commands.CMDsend;
import net.digiex.simplefeatures.commands.CMDsendall;
import net.digiex.simplefeatures.commands.CMDsethome;
import net.digiex.simplefeatures.commands.CMDsetspawn;
import net.digiex.simplefeatures.commands.CMDspawn;
import net.digiex.simplefeatures.commands.CMDtp;
import net.digiex.simplefeatures.commands.CMDtpa;
import net.digiex.simplefeatures.commands.CMDtpahere;
import net.digiex.simplefeatures.commands.CMDtphere;
import net.digiex.simplefeatures.commands.CMDwho;
import net.digiex.simplefeatures.commands.CMDworld;
import net.digiex.simplefeatures.listeners.BListener;
import net.digiex.simplefeatures.listeners.EListener;
import net.digiex.simplefeatures.listeners.PListener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.wimbli.WorldBorder.WorldBorder;

import de.diddiz.LogBlockQuestioner.LogBlockQuestioner;

public class SFPlugin extends JavaPlugin {

	public static LogBlockQuestioner questioner;
	static final Logger log = Logger.getLogger("Minecraft");
	public static String pluginName = "SimpleFeatures";
	public static WorldBorder worldBorderPlugin;

	public static OfflinePlayer getOfflinePlayer(CommandSender sender,
			String name, SFPlugin plugin) {
		if (name != null) {
			List<OfflinePlayer> players = matchOfflinePlayer(name, plugin);
			if (players.isEmpty()) {
				sender.sendMessage("I don't know who '" + name + "' is!");
				return null;
			} else {
				return players.get(0);
			}
		} else {
			if (!(sender instanceof Player)) {
				return null;
			} else {
				return (OfflinePlayer) sender;
			}
		}
	}

	public static Player getPlayer(CommandSender sender, String name) {
		if (name != null) {
			List<Player> players = sender.getServer().matchPlayer(name);

			if (players.isEmpty()) {
				sender.sendMessage("I don't know who '" + name + "' is!");
				return null;
			} else {
				return players.get(0);
			}
		} else {
			if (!(sender instanceof Player)) {
				return null;
			} else {
				return (Player) sender;
			}
		}
	}

	public static boolean isInSpawnProtect(Location loc, SFPlugin plugin) {
		final Vector player = loc.toVector();
		final Vector spawn = loc.getWorld().getSpawnLocation().toVector();
		final double safe = plugin.getConfig().getDouble(
				"worlds." + loc.getWorld().getName() + ".spawnprotect", 50);
		if (spawn.distance(player) < safe) {
			return true;
		}
		return false;
	}

	public static String itemStackToString(ItemStack[] itemStacks) {
		String invString = "";
		for (ItemStack itemStack : itemStacks) {
			if (itemStack != null) {
				invString = invString + ";" + itemStack.getTypeId() + ":"
						+ itemStack.getAmount() + ":"
						+ itemStack.getDurability();

				if (itemStack.getData() == null) {
					invString = invString + ":null";
				} else {
					invString = invString + ":" + itemStack.getData().getData();
				}

			} else {
				invString = invString + ";" + "null";
			}
		}
		return invString;
	}

	public static void log(Level level, String msg) {
		log.log(level, "[" + pluginName + "] " + msg);
	}

	// 2 = loaded with errors int shares =
	// 0;

	public static List<OfflinePlayer> matchOfflinePlayer(String partialName,
			SFPlugin plugin) {
		Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
		for (OfflinePlayer op : plugin.getServer().getOfflinePlayers()) {
			if (op.isWhitelisted()) {
				players.add(op);
			}
		}

		List<OfflinePlayer> matchedOfflinePlayers = new ArrayList<OfflinePlayer>();
		List<String> found = new ArrayList<String>();
		for (OfflinePlayer player : players) {
			if (!found.contains(player.getName())) {
				found.add(player.getName());
				if (partialName.equalsIgnoreCase(player.getName())) {
					// Exact match
					matchedOfflinePlayers.clear();
					matchedOfflinePlayers.add(player);
					break;
				}
				if (player.getName().toLowerCase()
						.indexOf(partialName.toLowerCase()) != -1) {
					// Partial match
					matchedOfflinePlayers.add(player);
				}
			}
		}

		return matchedOfflinePlayers;
	}

	public static String recompileMessage(String[] args, int start, int end) {
		if (start > args.length) {
			throw new IndexOutOfBoundsException();
		}

		String result = args[start];

		for (int i = start + 1; i <= end; i++) {
			result += " " + args[i];
		}

		return result;
	}

	public static ItemStack[] stringToItemStack(String invString) {
		if (invString == null) {
			return null;
		}
		String[] firstSplit = invString.split("\\;");
		ItemStack[] itemStack = new ItemStack[firstSplit.length - 1];

		for (int i = 0; i < firstSplit.length - 1; i++) {
			if (!firstSplit[(i + 1)].equals("null")) {
				String[] secondSplit = firstSplit[(i + 1)].split("\\:");
				itemStack[i] = new ItemStack(Integer.valueOf(secondSplit[0])
						.intValue(),
						Integer.valueOf(secondSplit[1]).intValue(), Short
								.valueOf(secondSplit[2]).shortValue());

				if (!secondSplit[3].equals("null")) {
					itemStack[i].setData(new MaterialData(Integer.valueOf(
							secondSplit[0]).intValue(), Byte.valueOf(
							secondSplit[3]).byteValue()));
				}
			}

		}

		return itemStack;
	}

	protected int AutoSaveTaskID;

	public HashMap<String, PermissionAttachment> permissionAttachements = new HashMap<String, PermissionAttachment>();

	boolean permissionsEnabled = true; // 0 = unloaded, 1 = loaded successfully,

	// public boolean deleteSFInventory(GameMode gameMode, String playerName) {
	// SFInventory inv = getSFInventory(gameMode, playerName);
	// if (inv != null) {
	// getDatabase().delete(inv);
	// return true;
	// }
	// return false;
	// }
	private final ArrayList<UUID> SFWorlds = new ArrayList<UUID>();

	@SuppressWarnings("unchecked")
	private void createDefaultConfig() {
		// Worlds
		FileConfiguration c = getConfig();
		for (World w : getServer().getWorlds()) {
			String ks = "worlds." + w.getName() + ".";
			if (SFWorlds.contains(w.getUID())) {
				if (!c.isSet(ks + "environment")) {
					c.set(ks + "environment", w.getEnvironment().toString());
				}
				if (!c.isSet(ks + "seed")) {
					c.set(ks + "seed", w.getSeed());
				}
				if (!c.isSet(ks + "pvp")) {
					c.set(ks + "pvp", w.getPVP());
				}
				if (!c.isSet(ks + "monsters")) {
					c.set(ks + "monsters", w.getAllowMonsters());
				}
				if (!c.isSet(ks + "animals")) {
					c.set(ks + "animals", w.getAllowAnimals());
				}
			}
			if (!c.isSet(ks + "itemdrops")) {
				c.set(ks + "itemdrops", true);
			}
			if (!c.isSet(ks + "explosions")) {
				c.set(ks + "explosions", false);
			}
			if (!c.isSet(ks + "spawnprotect")) {
				c.set(ks + "spawnprotect", 50);
			}
			if (!c.isSet(ks + "gamemode")) {
				c.set(ks + "gamemode", getServer().getDefaultGameMode()
						.getValue());
			}
		}
		// Admins
		if (!c.isSet("admins")) {
			List<String> admlist = new ArrayList<String>();
			for (OfflinePlayer op : getServer().getOperators()) {
				admlist.add(op.getName());
			}
			c.set("admins", admlist);
		}
		saveConfig();
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(SFHome.class);
		list.add(SFInventory.class);
		list.add(SFMail.class);
		list.add(SFLocation.class);
		return list;
	}

	public Environment getEnvFromString(String env) {
		if (env.equalsIgnoreCase("HELL") || env.equalsIgnoreCase("NETHER")) {
			return Environment.NETHER;
		}

		if (env.equalsIgnoreCase("SKYLANDS") || env.equalsIgnoreCase("SKYLAND")
				|| env.equalsIgnoreCase("STARWARS")
				|| env.equalsIgnoreCase("THE_END")) {
			return Environment.THE_END;
		}

		if (env.equalsIgnoreCase("NORMAL") || env.equalsIgnoreCase("WORLD")) {
			return Environment.NORMAL;
		}
		return Environment.NORMAL;
	}

	@Override
	public void onDisable() {
		log(Level.INFO, "Plugin disabled.");
		getServer().getScheduler().cancelTask(AutoSaveTaskID);
	}

	@Override
	public void onEnable() {
		setFilter();
		PluginManager pm = getServer().getPluginManager();
		questioner = (LogBlockQuestioner) pm.getPlugin("LogBlockQuestioner");
		worldBorderPlugin = (WorldBorder) pm.getPlugin("WorldBorder");
		// Worlds

		// Basic Counter to count how many Worlds we are loading.
		int count = 0;
		// Grab all the Worlds from the Config.
		Set<String> worldKeys = getConfig().getConfigurationSection("worlds")
				.getKeys(false);

		// Check that the list is not null.
		if (worldKeys != null) {
			for (String worldKey : worldKeys) {
				if (getServer().getWorld(worldKey) == null) {
					// Grab the initial values from the config file.
					String environment = getConfig().getString(
							"worlds." + worldKey + ".environment", "NORMAL"); // Grab
																				// the
																				// Environment
																				// as
					// a String.
					// World newworld = getServer().createWorld(worldKey,
					// getEnvFromString(environment));
					WorldCreator wc = new WorldCreator(worldKey);
					wc.environment(getEnvFromString(environment));
					long seed = getConfig().getLong(
							"worlds." + worldKey + ".seed", 0);
					if (seed != 0) {
						wc.seed(seed);
					}
					World newworld = getServer().createWorld(wc);

					// Increment the world count
					newworld.setPVP(getConfig().getBoolean(
							"worlds." + worldKey + ".pvp", false));
					newworld.setSpawnFlags(
							getConfig().getBoolean(
									"worlds." + worldKey + ".monsters", false),
							getConfig().getBoolean(
									"worlds." + worldKey + ".animals", false));
					SFWorlds.add(newworld.getUID());
					log(Level.INFO,
							ChatColor.GRAY + "World " + newworld.getName()
									+ " loaded, environment "
									+ newworld.getEnvironment().toString()
									+ ", pvp: " + newworld.getPVP()
									+ ", Animals:" + newworld.getAllowAnimals()
									+ ", Monsters: "
									+ newworld.getAllowMonsters() + ", seed: "
									+ newworld.getSeed());
					count++;
				}
			}
		}

		// Simple Output to the Console to show how many Worlds were loaded.
		log(Level.INFO, ChatColor.YELLOW + "" + count + " world(s) loaded.");
		PListener playerListener = new PListener(this);
		// WListener worldListener = new WListener(this);
		BListener blockListener = new BListener(this);
		EListener entityListener = new EListener(this);
		// Listeners
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener,
				Event.Priority.Highest, this);
		// pm.registerEvent(Event.Type.WORLD_SAVE, worldListener,
		// Priority.Monitor, this); TODO: Save stuff when world saves.
		// pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,
		// Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_PRELOGIN, playerListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_GAME_MODE_CHANGE, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.ITEM_SPAWN, entityListener,
				Priority.Highest, this);
		getCommand("home").setExecutor(new CMDhome(this));
		getCommand("sethome").setExecutor(new CMDsethome(this));
		getCommand("setspawn").setExecutor(new CMDsetspawn(this));
		getCommand("listhomes").setExecutor(new CMDlisthomes(this));
		getCommand("spawn").setExecutor(new CMDspawn(this));
		getCommand("tpa").setExecutor(new CMDtpa(this));
		getCommand("tpahere").setExecutor(new CMDtpahere(this));
		getCommand("tp").setExecutor(new CMDtp(this));
		getCommand("tphere").setExecutor(new CMDtphere(this));
		getCommand("world").setExecutor(new CMDworld(this));
		getCommand("who").setExecutor(new CMDwho(this));
		getCommand("msg").setExecutor(new CMDmsg(this));
		getCommand("reply").setExecutor(new CMDreply(this));
		getCommand("me").setExecutor(new CMDme(this));
		getCommand("entities").setExecutor(new CMDentities(this));
		getCommand("abort").setExecutor(new CMDabort(this));
		getCommand("read").setExecutor(new CMDread(this));
		getCommand("send").setExecutor(new CMDsend(this));
		getCommand("sendall").setExecutor(new CMDsendall(this));
		getCommand("clear").setExecutor(new CMDclear(this));
		getCommand("cleanup").setExecutor(new CMDcleanup(this));
		getCommand("random").setExecutor(new CMDrandom(this));
		getCommand("admin").setExecutor(new CMDadmin(this));
		getCommand("lastseen").setExecutor(new CMDlastseen(this));
		setupDatabase();
		int interval = getConfig().getInt("autosave.interval", 300);
		log(Level.INFO, ChatColor.AQUA
				+ "Players and worlds will be saved every " + interval
				+ " seconds.");
		AutoSaveTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(
				this, new AutoSaveThread(this), interval * 20, interval * 20);
		saveConfig();
		createDefaultConfig();
	}

	public void saveSFInventory(SFInventory inv) {
		getDatabase().save(inv);
	}

	public void setFilter() {
		log.setFilter(new Filter() {

			@Override
			public boolean isLoggable(LogRecord record) {
				return (record.getMessage() == null)
						|| (!record.getMessage().contains("overloaded?"))
						|| (record.getLevel() != Level.WARNING);
			}
		});
	}

	private void setupDatabase() {
		try {
			getDatabase().find(SFHome.class).findRowCount();
			getDatabase().find(SFInventory.class).findRowCount();
			getDatabase().find(SFMail.class).findRowCount();
			getDatabase().find(SFLocation.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("Installing database for "
					+ getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}
}
