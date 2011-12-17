package net.digiex.simplefeatures.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.digiex.simplefeatures.SFCompassPoint;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDcompasspoint implements CommandExecutor {
	SFPlugin plugin;
	// Properties the EbeanServer must be told to update. It
	// doesn't appear to be smart enough to figure these out on its own.
	private static final Set<String> updateProps;
	static {
		updateProps = new HashSet<String>();
		updateProps.add("x");
		updateProps.add("y");
		updateProps.add("z");
		updateProps.add("yaw");
		updateProps.add("pitch");
		updateProps.add("world_name");
		updateProps.add("point_name");
	}

	public CMDcompasspoint(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("list")) {
					List<SFCompassPoint> points = plugin.getDatabase()
							.find(SFCompassPoint.class).where()
							.ieq("playerName", p.getName()).findList();
					if (points.isEmpty()) {
						sender.sendMessage("You have no compass points.");
					} else {
						String result = "";
						for (SFCompassPoint cp : points) {
							if (result.length() > 0) {
								result += ", ";
							}
							result += cp.getWorldName();
						}
						sender.sendMessage(ChatColor.YELLOW
								+ "All compass points: " + ChatColor.AQUA
								+ result);
					}
					return true;
				} else if (args[0].equalsIgnoreCase("add")) {
					if (p.getWorld().getEnvironment() == Environment.NETHER
							&& p.getWorld().getEnvironment() == Environment.THE_END) {
						sender.sendMessage(ChatColor.YELLOW
								+ "Compasses do not work in the nether nor in the end");
						return true;
					}
					if (args.length > 1) {
						com.avaje.ebean.EbeanServer db = plugin.getDatabase();
						db.beginTransaction();

						try {
							SFCompassPoint point = db
									.find(SFCompassPoint.class)
									.where()
									.ieq("worldName",
											p.getLocation().getWorld()
													.getName())
									.ieq("playerName", p.getName())
									.ieq("pointName", args[1]).findUnique();
							boolean isUpdate = false;

							if (point == null) {
								p.sendMessage(ChatColor.YELLOW
										+ "A new compass point for this world created!");

								point = new SFCompassPoint();
								point.setPlayerName(p.getName());
							} else {

								p.sendMessage(ChatColor.YELLOW
										+ "Compass point updated!");

								isUpdate = true;
							}
							point.setWorldName(p.getWorld().getName());
							point.setPointName(args[1]);
							point.setX(p.getLocation().getX());
							point.setY(p.getLocation().getY());
							point.setZ(p.getLocation().getZ());
							point.setYaw(p.getLocation().getYaw());
							point.setPitch(p.getLocation().getPitch());

							if (isUpdate) {
								db.update(point, updateProps);
							}
							db.save(point);
							db.commitTransaction();
						} finally {
							db.endTransaction();
						}
						return true;
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length > 1) {

						com.avaje.ebean.EbeanServer db = plugin.getDatabase();
						db.beginTransaction();

						try {
							SFCompassPoint point = db
									.find(SFCompassPoint.class)
									.where()
									.ieq("worldName",
											p.getLocation().getWorld()
													.getName())
									.ieq("playerName", p.getName())
									.ieq("pointName", args[1]).findUnique();

							if (point == null) {
								p.sendMessage(ChatColor.RED
										+ "No compass point named " + args[1]
										+ " exists!");
								return true;
							} else {

								p.sendMessage(ChatColor.YELLOW
										+ "Compass point removed!");
								db.delete(point);
							}
							db.save(point);
							db.commitTransaction();
						} finally {
							db.endTransaction();
						}
						return true;
					}
				}
			}
		}
		sender.sendMessage(ChatColor.YELLOW
				+ "Left/right click with a compass to toggle active compass points");
		return false;
	}

}
