package net.digiex.simplefeatures;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.digiex.simplefeatures.commands.CMDhome;
import net.digiex.simplefeatures.commands.CMDsethome;
import net.digiex.simplefeatures.commands.CMDsetspawn;
import net.digiex.simplefeatures.commands.CMDspawn;
import net.digiex.simplefeatures.commands.CMDtpa;
import net.digiex.simplefeatures.commands.CMDtpahere;
import net.digiex.simplefeatures.commands.CMDworld;
import net.digiex.simplefeatures.listeners.BListener;
import net.digiex.simplefeatures.listeners.PListener;
import net.digiex.simplefeatures.listeners.WListener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;

import de.diddiz.LogBlockQuestioner.LogBlockQuestioner;

public class SFPlugin extends JavaPlugin{

	public static Configuration playerconfig;
	static LogBlockQuestioner questioner;
	public Configuration config;
	static final Logger log = Logger.getLogger("Minecraft");
	public static String pluginName = "SimpleFeatures";
	public static boolean isInSpawnProtect(Location loc){
		final Vector player = loc.toVector();
		final Vector spawn = loc.getWorld().getSpawnLocation().toVector();
		final double safe = 50;
		if (spawn.distance(player) < safe)
		{
			return true;
		}
		return false;
		//TODO: Check if this works
	}
	public static void log(Level level, String msg){
		log.log(level, "["+pluginName+"] "+msg);
	}
	boolean permissionsEnabled = true;    // 0 = unloaded, 1 = loaded successfully, 2 = loaded with errors    int shares = 0;
	public Environment getEnvFromString(String env) {
		// Don't reference the enum directly as there aren't that many, and we can be more forgiving to users this way
		if (env.equalsIgnoreCase("HELL") || env.equalsIgnoreCase("NETHER")) {
			env = "NETHER";
		}

		if (env.equalsIgnoreCase("SKYLANDS") || env.equalsIgnoreCase("SKYLAND") || env.equalsIgnoreCase("STARWARS")) {
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
	public String itemStackToString(ItemStack[] itemStacks)
	{
		String invString = "";
		for (ItemStack itemStack : itemStacks) {
			if (itemStack != null) {
				invString = invString + ";" + itemStack.getTypeId() + ":" + itemStack.getAmount() + ":" + itemStack.getDurability();

				if (itemStack.getData() == null) {
					invString = invString + ":null";
				} else {
					invString = invString + ":" + itemStack.getData().getData();
				}

			}
			else
			{
				invString = invString + ";" + "null";
			}
		}
		return invString;
	}
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}
	@Override
	public void onEnable() {
		questioner = (LogBlockQuestioner)this.getServer().getPluginManager().getPlugin("LogBlockQuestioner");
		PluginManager pm = getServer().getPluginManager();
		// TODO Auto-generated method stub
		if(!this.getDataFolder().exists()){
			this.getDataFolder().mkdir();
		}
		File pcfgfile = new File(this.getDataFolder(),"players.yml");
		if(!pcfgfile.exists()){
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
		//Worlds

		// Basic Counter to count how many Worlds we are loading.
		int count = 0;
		// Grab all the Worlds from the Config.
		List<String> worldKeys = config.getKeys("worlds");


		// Check that the list is not null.
		if (worldKeys != null) {
			for (String worldKey : worldKeys) {
				// Grab the initial values from the config file.
				String environment = config.getString("worlds." + worldKey + ".environment", "NORMAL"); // Grab the Environment as a String.
				World newworld = getServer().createWorld(worldKey, getEnvFromString(environment));
				// Increment the world count
				newworld.setPVP(config.getBoolean("worlds." + worldKey + ".pvp", false));
				newworld.setSpawnFlags(config.getBoolean("worlds." + worldKey + ".monsters", false), config.getBoolean("worlds." + worldKey + ".animals", false));
				log(Level.INFO,"World "+newworld.getName()+" loaded, environment "+newworld.getEnvironment().toString()+", pvp: "+newworld.getPVP()+", Animals:"+newworld.getAllowAnimals()+", Monsters: "+newworld.getAllowMonsters());
				count++;
			}
		}

		// Simple Output to the Console to show how many Worlds were loaded.
		log(Level.INFO,count + " world(s) loaded.");
		PListener playerListener = new PListener(this);
		WListener worldListener = new WListener(this);
		BListener blockListener = new BListener(this);
		//Listeners
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.WORLD_SAVE, worldListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.WORLD_SAVE, worldListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
		getCommand("home").setExecutor(new CMDhome(this));
		getCommand("sethome").setExecutor(new CMDsethome(this));
		getCommand("setspawn").setExecutor(new CMDsetspawn(this));
		getCommand("spawn").setExecutor(new CMDspawn(this));
		getCommand("tpa").setExecutor(new CMDtpa(this));
		getCommand("tpahere").setExecutor(new CMDtpahere(this));
		getCommand("world").setExecutor(new CMDworld(this));
	}

	public ItemStack[] stringToItemStack(String invString)
	{
		String[] firstSplit = invString.split("\\;");
		ItemStack[] itemStack = new ItemStack[firstSplit.length - 1];

		for (int i = 0; i < firstSplit.length - 1; i++) {
			if (!firstSplit[(i + 1)].equals("null")) {
				String[] secondSplit = firstSplit[(i + 1)].split("\\:");
				itemStack[i] = new ItemStack(Integer.valueOf(secondSplit[0]).intValue(), Integer.valueOf(secondSplit[1]).intValue(), Short.valueOf(secondSplit[2]).shortValue());

				if (!secondSplit[3].equals("null")) {
					itemStack[i].setData(new MaterialData(Integer.valueOf(secondSplit[0]).intValue(), Byte.valueOf(secondSplit[3]).byteValue()));
				}
			}

		}

		return itemStack;
	}


}

