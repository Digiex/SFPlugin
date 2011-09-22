package net.digiex.simplefeatures;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

import javax.persistence.PersistenceException;

import net.digiex.simplefeatures.commands.CMDhome;
import net.digiex.simplefeatures.commands.CMDlisthomes;
import net.digiex.simplefeatures.commands.CMDmsg;
import net.digiex.simplefeatures.commands.CMDreply;
import net.digiex.simplefeatures.commands.CMDsethome;
import net.digiex.simplefeatures.commands.CMDsetspawn;
import net.digiex.simplefeatures.commands.CMDspawn;
import net.digiex.simplefeatures.commands.CMDtpa;
import net.digiex.simplefeatures.commands.CMDtpahere;
import net.digiex.simplefeatures.commands.CMDwho;
import net.digiex.simplefeatures.commands.CMDworld;
import net.digiex.simplefeatures.listeners.BListener;
import net.digiex.simplefeatures.listeners.PListener;
import net.digiex.simplefeatures.listeners.EListener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;

import de.diddiz.LogBlockQuestioner.LogBlockQuestioner;

public class SFPlugin extends JavaPlugin {

	public static Configuration playerconfig;
	static LogBlockQuestioner questioner;
	public Configuration config;
	static final Logger log = Logger.getLogger("Minecraft");
	public static String pluginName = "SimpleFeatures";

	public HashMap<String, TeleportConfirmTask> teleporters = new HashMap<String, TeleportConfirmTask>();
	public HashMap<String, Boolean> gods = new HashMap<String, Boolean>();

	public static boolean isInSpawnProtect(Location loc) {
		final Vector player = loc.toVector();
		final Vector spawn = loc.getWorld().getSpawnLocation().toVector();
		final double safe = 50;
		if (spawn.distance(player) < safe) {
			return true;
		}
		return false;
	}

	public static void log(Level level, String msg) {
		log.log(level, "[" + pluginName + "] " + msg);
	}

	boolean permissionsEnabled = true; // 0 = unloaded, 1 = loaded successfully,
										// 2 = loaded with errors int shares =
										// 0;

	public Environment getEnvFromString(String env) {
		// Don't reference the enum directly as there aren't that many, and we
		// can be more forgiving to users this way
		if (env.equalsIgnoreCase("HELL") || env.equalsIgnoreCase("NETHER")) {
			env = "NETHER";
		}

		if (env.equalsIgnoreCase("SKYLANDS") || env.equalsIgnoreCase("SKYLAND")
				|| env.equalsIgnoreCase("STARWARS")) {
			env = "SKYLANDS";
		}

		if (env.equalsIgnoreCase("NORMAL") || env.equalsIgnoreCase("WORLD")) {
			env = "NORMAL";
		}

		try {
			return Environment.valueOf(env);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable() {
		questioner = (LogBlockQuestioner) this.getServer().getPluginManager()
				.getPlugin("LogBlockQuestioner");
		PluginManager pm = getServer().getPluginManager();
		// TODO Auto-generated method stub
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		File pcfgfile = new File(this.getDataFolder(), "players.yml");
		if (!pcfgfile.exists()) {
			try {
				pcfgfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		playerconfig = new Configuration(pcfgfile);
		playerconfig.load();
		config = this.getConfiguration();
		config.load();
		config.setHeader("#Feature configuration");
		config.save();
		// Worlds

		// Basic Counter to count how many Worlds we are loading.
		int count = 0;
		// Grab all the Worlds from the Config.
		List<String> worldKeys = config.getKeys("worlds");

		// Check that the list is not null.
		if (worldKeys != null) {
			for (String worldKey : worldKeys) {
				// Grab the initial values from the config file.
				String environment = config.getString("worlds." + worldKey
						+ ".environment", "NORMAL"); // Grab the Environment as
														// a String.
				World newworld = getServer().createWorld(worldKey,
						getEnvFromString(environment));
				// Increment the world count
				newworld.setPVP(config.getBoolean(
						"worlds." + worldKey + ".pvp", false));
				newworld.setSpawnFlags(config.getBoolean("worlds." + worldKey
						+ ".monsters", false), config.getBoolean("worlds."
						+ worldKey + ".animals", false));
				log(Level.INFO,
						"World " + newworld.getName() + " loaded, environment "
								+ newworld.getEnvironment().toString()
								+ ", pvp: " + newworld.getPVP() + ", Animals:"
								+ newworld.getAllowAnimals() + ", Monsters: "
								+ newworld.getAllowMonsters());
				count++;
			}
		}

		// Simple Output to the Console to show how many Worlds were loaded.
		log(Level.INFO, count + " world(s) loaded.");
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
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener,
				Priority.Highest, this);
		getCommand("home").setExecutor(new CMDhome(this));
		getCommand("sethome").setExecutor(new CMDsethome(this));
		getCommand("setspawn").setExecutor(new CMDsetspawn(this));
		getCommand("listhomes").setExecutor(new CMDlisthomes(this));
		getCommand("spawn").setExecutor(new CMDspawn(this));
		getCommand("tpa").setExecutor(new CMDtpa(this));
		getCommand("tpahere").setExecutor(new CMDtpahere(this));
		getCommand("world").setExecutor(new CMDworld(this));
		getCommand("who").setExecutor(new CMDwho(this));
		getCommand("msg").setExecutor(new CMDmsg(this));
		getCommand("reply").setExecutor(new CMDreply(this));
		setupDatabase();
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(SFHome.class);
		list.add(SFInventory.class);
		return list;
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

	private void setupDatabase() {
		try {
			getDatabase().find(SFHome.class).findRowCount();
			getDatabase().find(SFInventory.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("Installing database for "
					+ getDescription().getName() + " due to first time usage");
			installDDL();
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

	public static String itemStackToString(ItemStack[] itemStacks) {
		String invString = "";
		for (ItemStack itemStack : itemStacks) {
			if (itemStack != null) {
				invString = invString + ";" + itemStack.getTypeId() + ":"
						+ itemStack.getAmount() + ":"
						+ itemStack.getDurability();

				if (itemStack.getData() == null)
					invString = invString + ":null";
				else {
					invString = invString + ":" + itemStack.getData().getData();
				}

			} else {
				invString = invString + ";" + "null";
			}
		}
		return invString;
	}

	public static ItemStack[] stringToItemStack(String invString) {
		if(invString == null){
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

	public void saveSFInventory(SFInventory inv) {
		getDatabase().save(inv);
	}

	public boolean deleteSFInventory(GameMode gameMode, String playerName) {
		SFInventory inv = getSFInventory(gameMode, playerName);
		if (inv != null) {
			getDatabase().delete(inv);
			return true;
		}
		return false;
	}

	public SFInventory getSFInventory(GameMode gameMode, String playerName) {
		int gm = gameMode.getValue();
		SFInventory inv = (SFInventory) getDatabase().find(SFInventory.class)
				.where().eq("gameMode", gm).ieq("playerName", playerName)
				.findUnique();
		return inv;
	}

	public void updateSFInventory(SFInventory inv) {
		deleteSFInventory(inv.getGamemode(),
				inv.getPlayerName());
		saveSFInventory(inv);
	}
}
