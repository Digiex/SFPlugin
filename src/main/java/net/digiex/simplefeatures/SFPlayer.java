package net.digiex.simplefeatures;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class SFPlayer {
	Player player;
	SFPlugin plugin;
	// Properties the EbeanServer must be told to update. It
	// doesn't appear to be smart enough to figure these out on its own.
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

	public SFPlayer(Player player) {
		this.player = player;
		plugin = ((SFPlugin) player.getServer().getPluginManager()
				.getPlugin("SimpleFeatures"));
	}

	public SFHome getHome(World world) {
		String homename = world.getName();
		if (player.getWorld().getName().contains("_nether")
				|| player.getWorld().getName().contains("_the_end")) {
			homename = plugin.getServer().getWorlds().get(0).getName();
		}
		return plugin.getDatabase().find(SFHome.class).where()
				.ieq("worldName", homename).ieq("playerName", player.getName())
				.findUnique();

	}

	public Location getHomeLoc(World world) {
		SFHome home = getHome(world);
		if (home != null) {
			return home.getLocation();
		}
		return null;
	}

	public Location getLastLocation(World world) {
		SFLocation lastLoc = plugin.getDatabase().find(SFLocation.class)
				.where().ieq("worldName", world.getName())
				.ieq("playerName", player.getName()).findUnique();
		Location loc = world.getSpawnLocation();
		if (lastLoc != null) {
			loc = new Location(plugin.getServer().getWorld(
					lastLoc.getWorldName()), lastLoc.getX(), lastLoc.getY(),
					lastLoc.getZ(), lastLoc.getYaw(), lastLoc.getPitch());
		}
		return loc;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isAdmin() {
		List<Object> admins = plugin.getConfig().getList("admins");
		if (admins != null) {
			if (admins.contains(player.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isTeleporting() {
		return SFTeleportTask.teleporters.containsKey(player.getName());
	}

	public void setHome(Location loc) {
		if (loc.getWorld().getName().contains("_nether")
				|| loc.getWorld().getName().contains("_the_end")) {
			player.sendMessage(ChatColor.RED
					+ "You can not set a home in "
					+ loc.getWorld().getEnvironment().toString().toLowerCase()
							.replace("_", " ") + "!");
			return;
		}
		com.avaje.ebean.EbeanServer db = plugin.getDatabase();
		db.beginTransaction();

		try {
			SFHome home = db.find(SFHome.class).where()
					.ieq("worldName", loc.getWorld().getName())
					.ieq("playerName", player.getName()).findUnique();
			boolean isUpdate = false;
			if (home == null) {
				player.sendMessage(ChatColor.YELLOW + "Home for "
						+ loc.getWorld().getName() + " created!");

				home = new SFHome();
				home.setPlayer(player);
			} else {
				player.sendMessage(ChatColor.YELLOW + "Home for "
						+ loc.getWorld().getName() + " updated!");

				isUpdate = true;
			}
			home.setLocation(loc);
			if (isUpdate) {
				db.update(home, homeUpdateProps);
			}
			db.save(home);
			db.commitTransaction();
		} finally {
			db.endTransaction();
		}
	}

	public void setLastLocation(Location loc) {
		com.avaje.ebean.EbeanServer db = plugin.getDatabase();
		db.beginTransaction();

		try {
			SFLocation lastLoc = db.find(SFLocation.class).where()
					.ieq("worldName", loc.getWorld().getName())
					.ieq("playerName", player.getName()).findUnique();
			boolean isUpdate = false;

			if (lastLoc == null) {
				lastLoc = new SFLocation();
				lastLoc.setPlayerName(player.getName());
			} else {
				isUpdate = true;
			}
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

	@SuppressWarnings("unchecked")
	public void showYesNoGui(String line1, String line2, String button1text,
			String button2text, String button1command, String button2command) {
		JSONObject msg = new JSONObject();
		msg.put("id", "yesno");
		msg.put("l1", line1);
		msg.put("l2", line2);
		msg.put("b1", button1text);
		msg.put("b2", button2text);
		msg.put("b1c", button1command);
		msg.put("b2c", button2command);
		player.sendPluginMessage(plugin, "simplefeatures", msg.toJSONString()
				.getBytes());
	}

	public void teleport(Location to) {
		teleport(player, null, to, false, null, "Teleporting!");
	}

	public void teleport(Location to, String infoMsg) {
		teleport(player, null, to, false, null, infoMsg);
	}

	public void teleport(Player who, Player askSubject, Location where,
			boolean ask, String question, String infoMsg) {
		int taskId = plugin
				.getServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(
						plugin,
						new SFTeleportTask(who, player, askSubject, where, ask,
								question, infoMsg));
		SFTeleportTask.teleporters.put(player.getName(), taskId);
	}
}
