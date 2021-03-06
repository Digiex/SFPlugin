package net.digiex.simplefeatures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import net.digiex.simplefeatures.commands.CMDabort;
import net.digiex.simplefeatures.commands.CMDadmin;
import net.digiex.simplefeatures.commands.CMDclear;
import net.digiex.simplefeatures.commands.CMDcompasspoint;
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
import net.digiex.simplefeatures.commands.CMDsf;
import net.digiex.simplefeatures.commands.CMDspawn;
import net.digiex.simplefeatures.commands.CMDtp;
import net.digiex.simplefeatures.commands.CMDtpa;
import net.digiex.simplefeatures.commands.CMDtpahere;
import net.digiex.simplefeatures.commands.CMDtphere;
import net.digiex.simplefeatures.commands.CMDwho;
import net.digiex.simplefeatures.commands.CMDworld;
import net.digiex.simplefeatures.commands.CMDxp;
import net.digiex.simplefeatures.listeners.BListener;
import net.digiex.simplefeatures.listeners.ClientModListener;
import net.digiex.simplefeatures.listeners.EListener;
import net.digiex.simplefeatures.listeners.InventoryListener;
import net.digiex.simplefeatures.listeners.PListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.wimbli.WorldBorder.WorldBorder;

public class SFPlugin extends JavaPlugin {

	public class unknownCMDexecutor implements CommandExecutor {

		@Override
		public boolean onCommand(CommandSender sender, Command command,
				String label, String[] args) {
			String langid = "en_US";
			if (sender instanceof Player) {
				langid = SFPlayer.getSFPlayer((Player) sender).getLanguage();
			}
			sender.sendMessage(ChatColor.RED
					+ SFTranslation.getInstance().translateKey(
							"general.commanddisabled", langid));
			return true;
		}

	}

	public static String pluginName = "SimpleFeatures";
	public BListener blockListener;
	public EListener entityListener;
	public PListener playerListener;
	public static WorldBorder worldBorderPlugin;

	public static void broadcastLocalizedFormattedMessage(String node,
			Object... args) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			SFPlayer sfp = SFPlayer.getSFPlayer(p);
			p.sendMessage(sfp.translateStringFormat(node, args));
		}
	}

	public static void broadcastLocalizedMessage(String node) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			SFPlayer sfp = SFPlayer.getSFPlayer(p);
			p.sendMessage(sfp.translateString(node));
		}
	}

	public static OfflinePlayer getOfflinePlayer(CommandSender sender,
			String name, SFPlugin plugin) {
		String langid = "en_US";
		if (sender instanceof Player) {
			langid = SFPlayer.getSFPlayer((Player) sender).getLanguage();
		}
		if (name != null) {
			List<OfflinePlayer> players = matchOfflinePlayer(name, plugin);
			if (players.isEmpty()) {
				sender.sendMessage(SFTranslation.getInstance()
						.translateKeyFormat("general.unknownplayer", langid,
								name));
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
		String langid = "en_US";
		if (sender instanceof Player) {
			langid = SFPlayer.getSFPlayer((Player) sender).getLanguage();
		}
		if (name != null) {
			List<Player> players = sender.getServer().matchPlayer(name);

			if (players.isEmpty()) {
				sender.sendMessage(SFTranslation.getInstance()
						.translateKeyFormat("general.unknownplayer", langid,
								name));
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

	// 2 = loaded with errors int shares =
	// 0;

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
		Bukkit.getPluginManager().getPlugin("SimpleFeatures").getLogger()
				.log(level, msg);
	}

	public static List<OfflinePlayer> matchOfflinePlayer(String partialName,
			SFPlugin plugin) {
		Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
		for (OfflinePlayer op : plugin.getServer().getOfflinePlayers()) {
			players.add(op);
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
	public static HashMap<String, Double> clientAddons = new HashMap<String, Double>();

	public static HashMap<String, String> playerLangs = new HashMap<String, String>();

	YamlConfiguration permsConfig;
	private InventoryListener inventoryListener;

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
				if (!c.isSet(ks + "type")) {
					c.set(ks + "type", w.getWorldType().toString());
				}
				if (!c.isSet(ks + "structures")) {
					c.set(ks + "structures", w.canGenerateStructures());
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
		if (!c.isSet("whitelist.enabled")) {
			c.set("whitelist.enabled", false);
		}
		if (!c.isSet("whitelist.kickmsg")) {
			c.set("whitelist.kickmsg", ChatColor.RED + "Not on whitelist, "
					+ ChatColor.WHITE + " please ask an " + ChatColor.AQUA
					+ "admin" + ChatColor.WHITE + " to whitelist you.");
		}
		if (!c.isSet("commands.disabled")) {
			List<String> cmdlist = new ArrayList<String>();
			cmdlist.add("entities");
			cmdlist.add("random");
			c.set("commands.disabled", cmdlist);
		}
		if (!c.isSet("autosave.interval")) {
			c.set("autosave.interval", 300);
		}
		if (!c.isSet("features.spawnprotect")) {
			c.set("features.spawnprotect", true);
		}
		if (!c.isSet("features.bedrockprotect")) {
			c.set("features.bedrockprotect", true);
		}
		if (!c.isSet("features.worldborderintegration")) {
			c.set("features.worldborderintegration", true);
		}
		if (!c.isSet("features.voidtp")) {
			c.set("features.voidtp", true);
		}
		if (!c.isSet("minimap.cavemapping")) {
			c.set("minimap.cavemapping", false);
		}
		if (!c.isSet("minimap.entitiesradar.player")) {
			c.set("minimap.entitiesradar.player", false);
		}
		if (!c.isSet("minimap.entitiesradar.animal")) {
			c.set("minimap.entitiesradar.animal", false);
		}
		if (!c.isSet("minimap.entitiesradar.mob")) {
			c.set("minimap.entitiesradar.mob", false);
		}
		if (!c.isSet("minimap.entitiesradar.slime")) {
			c.set("minimap.entitiesradar.slime", false);
		}
		if (!c.isSet("minimap.entitiesradar.squid")) {
			c.set("minimap.entitiesradar.squid", false);
		}
		if (!c.isSet("minimap.entitiesradar.other")) {
			c.set("minimap.entitiesradar.other", false);
		}
		saveConfig();
		if (permsConfig == null) {
			permsConfig = new YamlConfiguration();
		}
		try {
			permsConfig.load(new File(getDataFolder(), "permissions.yml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		permsConfig
				.options()
				.header("This file is not used yet, but it will be for handling permissions");
		try {
			permsConfig.save(new File(getDataFolder(), "permissions.yml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(SFHome.class);
		list.add(SFInventory.class);
		list.add(SFMail.class);
		list.add(SFLocation.class);
		list.add(SFCompassPoint.class);
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

	public WorldType getWorldTypeFromString(String type) {
		WorldType type1 = WorldType.getByName(type);
		if (type1 != null) {
			return type1;
		}
		if (type.equalsIgnoreCase("flat")) {
			return WorldType.FLAT;
		}
		if (type.equalsIgnoreCase("superflat")) {
			return WorldType.FLAT;
		}
		if (type.equalsIgnoreCase("VERSION_1_1")) {
			return WorldType.VERSION_1_1;
		}
		return WorldType.NORMAL;
	}

	@Override
	public void onDisable() {
		log(Level.INFO, "Plugin disabled.");
		getServer().getScheduler().cancelTask(AutoSaveTaskID);
	}

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		if (getConfig().getBoolean("features.worldborderintegration", true)) {
			worldBorderPlugin = (WorldBorder) pm.getPlugin("WorldBorder");
		}
		// Worlds

		// Basic Counter to count how many Worlds we are loading.
		int count = 0;
		if (getConfig().getConfigurationSection("worlds") != null) {
			// Grab all the Worlds from the Config.
			Set<String> worldKeys = getConfig().getConfigurationSection(
					"worlds").getKeys(false);

			// Check that the list is not null.
			if (worldKeys != null) {
				for (String worldKey : worldKeys) {
					if (getServer().getWorld(worldKey) == null) {
						// Grab the initial values from the config file.
						String environment = getConfig()
								.getString(
										"worlds." + worldKey + ".environment",
										"NORMAL"); // Grab
													// the
													// Environment
													// as
						// a String.
						// World newworld = getServer().createWorld(worldKey,
						// getEnvFromString(environment));
						WorldCreator wc = new WorldCreator(worldKey);
						wc.environment(getEnvFromString(environment));
						wc.type(getWorldTypeFromString(getConfig().getString(
								"worlds." + worldKey + ".type", "NORMAL")));
						long seed = getConfig().getLong(
								"worlds." + worldKey + ".seed", 0);
						if (seed != 0) {
							wc.seed(seed);
						}
						wc.generateStructures(getConfig().getBoolean(
								"worlds." + worldKey + ".structures", true));
						World newworld = getServer().createWorld(wc);

						// Increment the world count
						newworld.setPVP(getConfig().getBoolean(
								"worlds." + worldKey + ".pvp", false));
						newworld.setSpawnFlags(
								getConfig().getBoolean(
										"worlds." + worldKey + ".monsters",
										false),
								getConfig().getBoolean(
										"worlds." + worldKey + ".animals",
										false));
						SFWorlds.add(newworld.getUID());
						log(Level.INFO, "World " + newworld.getName()
								+ " loaded, type "
								+ newworld.getWorldType().toString()
								+ ", environment "
								+ newworld.getEnvironment().toString()
								+ ", pvp: " + newworld.getPVP() + ", Animals:"
								+ newworld.getAllowAnimals() + ", Monsters: "
								+ newworld.getAllowMonsters() + ", seed: "
								+ newworld.getSeed());
						count++;
					}
				}
			}
		}

		// Simple Output to the Console to show how many Worlds were loaded.
		log(Level.INFO, count + " world(s) loaded.");
		playerListener = new PListener(this);
		if (getConfig().getBoolean("features.bedrockprotect", true)
				|| getConfig().getBoolean("features.spawnprotect", true)) {
			blockListener = new BListener(this);
		}
		entityListener = new EListener(this);
		if (getConfig().getIntegerList("advanced.disalloweditems") != null) {
			inventoryListener = new InventoryListener(this);
		}

		saveConfig();
		createDefaultConfig();

		setCMDexecutor("home", new CMDhome(this));
		setCMDexecutor("sethome", new CMDsethome(this));
		setCMDexecutor("setspawn", new CMDsetspawn(this));
		setCMDexecutor("listhomes", new CMDlisthomes(this));
		setCMDexecutor("spawn", new CMDspawn(this));
		setCMDexecutor("tpa", new CMDtpa(this));
		setCMDexecutor("tpahere", new CMDtpahere(this));
		setCMDexecutor("tp", new CMDtp(this));
		setCMDexecutor("tphere", new CMDtphere(this));
		setCMDexecutor("world", new CMDworld(this));
		setCMDexecutor("who", new CMDwho(this));
		setCMDexecutor("msg", new CMDmsg(this));
		setCMDexecutor("reply", new CMDreply(this));
		setCMDexecutor("me", new CMDme(this));
		setCMDexecutor("entities", new CMDentities(this));
		setCMDexecutor("abort", new CMDabort(this));
		setCMDexecutor("read", new CMDread(this));
		setCMDexecutor("send", new CMDsend(this));
		setCMDexecutor("sendall", new CMDsendall(this));
		setCMDexecutor("clear", new CMDclear(this));
		setCMDexecutor("random", new CMDrandom(this));
		setCMDexecutor("admin", new CMDadmin(this));
		setCMDexecutor("lastseen", new CMDlastseen(this));
		setCMDexecutor("compasspoint", new CMDcompasspoint(this));
		setCMDexecutor("xp", new CMDxp(this));
		setCMDexecutor("sf", new CMDsf(this));
		setupDatabase();
		int interval = getConfig().getInt("autosave.interval", 300);
		log(Level.INFO, ChatColor.AQUA
				+ "Players and worlds will be saved every " + interval
				+ " seconds.");
		AutoSaveTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(
				this, new AutoSaveThread(this), interval * 20, interval * 20);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this,
				"simplefeatures");
		Bukkit.getMessenger().registerIncomingPluginChannel(this,
				"simplefeatures", new ClientModListener(this));
	}

	public void saveSFInventory(SFInventory inv) {
		getDatabase().save(inv);
	}

	private void setCMDexecutor(String cmd, CommandExecutor exc) {
		List<?> disabled = getConfig().getList("commands.disabled");
		if (disabled != null) {
			if (!disabled.contains(cmd)) {
				getCommand(cmd).setExecutor(exc);
			} else {
				getCommand(cmd).setExecutor(new unknownCMDexecutor());
			}
		} else {
			getCommand(cmd).setExecutor(exc);
		}
	}

	private void setupDatabase() {
		try {
			getDatabase().find(SFHome.class).findRowCount();
			getDatabase().find(SFInventory.class).findRowCount();
			getDatabase().find(SFMail.class).findRowCount();
			getDatabase().find(SFLocation.class).findRowCount();
			getDatabase().find(SFCompassPoint.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("Installing database for "
					+ getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}
}
