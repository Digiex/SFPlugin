package net.digiex.simplefeatures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

	private Location tempHomeLocation = null;

	public SFPlayer(Player player) {
		this.player = player;
		plugin = ((SFPlugin) player.getServer().getPluginManager()
				.getPlugin("SimpleFeatures"));
	}

	public double getClientModVersion() {
		if (!hasClientMod()) {
			return 0;
		}
		return SFPlugin.clientAddons.get(player.getName());
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

	public String getLanguage() {
		String lang = SFPlugin.playerLangs.get(player.getName());
		if (lang != null) {
			return lang;
		}
		return "en_US";
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

	public Location getTempHomeLocation() {
		return tempHomeLocation;
	}

	public boolean hasClientMod() {
		return SFPlugin.clientAddons.containsKey(player.getName());
	}

	public boolean isAdmin() {
		List<?> admins = plugin.getConfig().getList("admins");
		if (admins != null) {
			if (admins.contains(player.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isTeleporting() {
		// return SFTeleportTask.teleporters.containsKey(player.getName());
		return false;
	}

	public boolean loadInventory() {
		return loadInventory(player.getGameMode());
	}

	public boolean loadInventory(GameMode gamemode) {
		com.avaje.ebean.EbeanServer db = plugin.getDatabase();
		boolean retval = true;
		try {
			SFInventory inv = db.find(SFInventory.class).where()
					.eq("gameMode", gamemode.getValue())
					.ieq("playerName", player.getName()).findUnique();
			if (inv != null) {
				ItemStack[] contents = SFPlugin.stringToItemStack(inv
						.getInventory());
				if (contents != null) {
					player.getInventory().setContents(contents);
				}
				ItemStack[] armor = SFPlugin.stringToItemStack(inv.getArmor());
				if (armor != null) {
					player.getInventory().setArmorContents(armor);
				}
				if (!(inv.getHealth() > 0)) {
					player.setHealth(20);
					player.setFoodLevel(20);
				} else {
					player.setHealth(inv.getHealth());
					player.setFoodLevel(inv.getFood());
				}
				player.setExhaustion(inv.getExhaustion());
				player.setFireTicks(inv.getFireTicks());
				player.setRemainingAir(inv.getRemainingAir());
				player.setSaturation(inv.getSaturation());
				player.setTotalExperience(inv.getTotalExperience());
				player.setLevel(inv.getLevel());
				player.setExp(inv.getExp());
			}

		} catch (NullPointerException ex) {
			SFPlugin.log(Level.INFO, "Some inventory contents were null for "
					+ player.getName());
			// ex.printStackTrace();
		}
		return retval;
	}

	public boolean saveInventory() {
		com.avaje.ebean.EbeanServer db = plugin.getDatabase();
		boolean retval = true;
		if (!(player.getHealth() > 0)) {
			player.getInventory().clear();
			player.setHealth(20);
			player.setFoodLevel(20);
		}
		db.beginTransaction();
		boolean isUpdate = false;
		try {
			SFInventory inv = db.find(SFInventory.class).where()
					.eq("gameMode", player.getGameMode().getValue())
					.ieq("playerName", player.getName()).findUnique();
			if (inv == null) {
				inv = new SFInventory();
			} else {
				isUpdate = true;
			}
			inv.setGameMode(player.getGameMode().getValue());
			inv.setPlayerName(player.getName());
			inv.setInventory(SFPlugin.itemStackToString(player.getInventory()
					.getContents()));
			inv.setArmor(SFPlugin.itemStackToString(player.getInventory()
					.getArmorContents()));
			inv.setHealth(player.getHealth());
			inv.setFood(player.getFoodLevel());
			inv.setExhaustion(player.getExhaustion());
			inv.setFireTicks(player.getFireTicks());
			inv.setRemainingAir(player.getRemainingAir());
			inv.setSaturation(player.getSaturation());
			inv.setTotalExperience(player.getTotalExperience());
			inv.setLevel(player.getLevel());
			inv.setExp(player.getExp());
			if (isUpdate) {
				db.update(inv, invUpdateProps);
			}
			db.save(inv);
			db.commitTransaction();
		} catch (Exception ex) {
			player.kickPlayer(ChatColor.RED
					+ translateString("general.servererror") + " "
					+ ex.getMessage());
			ex.printStackTrace();
			retval = false;
		} finally {
			db.endTransaction();
		}
		return retval;
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

	public void setTempHomeLocation(Location tempHomeLocation) {
		this.tempHomeLocation = tempHomeLocation;
	}

	@SuppressWarnings("unchecked")
	public void showMailboxGui(List<SFMail> maillist) {
		JSONObject msg = new JSONObject();
		msg.put("id", "mailbox");
		List<JSONObject> mails = new ArrayList<JSONObject>();

		for (SFMail mail : maillist) {
			JSONObject mailobj = new JSONObject();
			mailobj.put("id", mail.getId());
			mailobj.put("from", mail.getFromPlayer());
			mailobj.put("message", mail.getMessage());
			mailobj.put("timestamp", mail.getTimestamp());
			mails.add(mailobj);
		}
		msg.put("mails", mails);
		player.sendPluginMessage(plugin, "simplefeatures", msg.toJSONString()
				.getBytes());
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

	@Deprecated
	public void teleport(Player who, Player askSubject, Location where,
			boolean ask, final String question, String infoMsg) {
		/*
		 * if (ask) { Prompt prompt = new BooleanPrompt() {
		 * 
		 * @Override protected Prompt acceptValidatedInput( ConversationContext
		 * context, boolean input) {
		 * 
		 * System.out.println(input);
		 * 
		 * return Prompt.END_OF_CONVERSATION; }
		 * 
		 * @Override public String getPromptText(ConversationContext context) {
		 * // TODO Auto-generated method stub return question; }
		 * 
		 * }; Conversation convo = new Conversation(plugin, askSubject, prompt);
		 * askSubject.beginConversation(convo); }
		 */
		/*
		 * int taskId = plugin .getServer() .getScheduler()
		 * .scheduleAsyncDelayedTask( plugin, new SFTeleportTask(who, player,
		 * askSubject, where, ask, question, infoMsg));
		 * SFTeleportTask.teleporters.put(player.getName(), taskId);
		 */
		if (!ask) {
			who.sendMessage(infoMsg);
			who.teleport(where);
		} else {
			who.sendMessage("Sorry, cannot teleport you. Please nag jessenic to fix this!");

		}
	}

	public String translateString(String node) {
		return SFTranslation.getInstance().translateKey(node, getLanguage());
	}

	public String translateStringFormat(String node, Object... args) {
		return SFTranslation.getInstance().translateKeyFormat(node,
				getLanguage(), args);
	}

	public void updateNameColour() {
		if (!player.isOp()) {
			plugin.permissionAttachements.get(player.getName()).setPermission(
					"bukkit.command.plugins", false);
			plugin.permissionAttachements.get(player.getName()).setPermission(
					"bukkit.command.version", false);
		} else {

			plugin.permissionAttachements.get(player.getName()).setPermission(
					"bukkit.command.plugins", true);
			plugin.permissionAttachements.get(player.getName()).setPermission(
					"bukkit.command.version", true);
		}
		String pname;
		if (player.isOp()) {
			pname = ChatColor.AQUA + player.getName();
		} else {
			pname = ChatColor.GREEN + player.getName();
		}
		player.setDisplayName(pname + ChatColor.WHITE);
		if (pname.length() < 17) {
			player.setPlayerListName(pname);
		}
	}
}
