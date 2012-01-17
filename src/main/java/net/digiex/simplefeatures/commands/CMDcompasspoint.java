package net.digiex.simplefeatures.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.digiex.simplefeatures.SFCompassPoint;
import net.digiex.simplefeatures.SFPlayer;
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
			SFPlayer sfp = new SFPlayer(p);
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("list")) {
					List<SFCompassPoint> points = plugin.getDatabase()
							.find(SFCompassPoint.class).where()
							.ieq("playerName", p.getName()).findList();
					if (points.isEmpty()) {
						sender.sendMessage(ChatColor.RED
								+ sfp.translateString("compasspoints.empty"));
					} else {
						String result = "";
						for (SFCompassPoint cp : points) {
							if (result.length() > 0) {
								result += ", ";
							}
							result += cp.getPointName();
						}
						sender.sendMessage(ChatColor.YELLOW
								+ sfp.translateString("compasspoints.all")
								+ " " + ChatColor.AQUA + result);
					}
					return true;
				} else if (args[0].equalsIgnoreCase("add")) {
					if (p.getWorld().getEnvironment() == Environment.NETHER
							|| p.getWorld().getEnvironment() == Environment.THE_END) {
						sender.sendMessage(ChatColor.YELLOW
								+ sfp.translateString("compasspoints.notinnether"));
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
										+ sfp.translateString("compasspoints.newcreated"));

								point = new SFCompassPoint();
								point.setPlayerName(p.getName());
							} else {

								p.sendMessage(ChatColor.YELLOW
										+ sfp.translateString("compasspoints.updated"));

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
						SFCompassPoint point = db
								.find(SFCompassPoint.class)
								.where()
								.ieq("worldName",
										p.getLocation().getWorld().getName())
								.ieq("playerName", p.getName())
								.ieq("pointName", args[1]).findUnique();

						if (point == null) {
							p.sendMessage(ChatColor.RED
									+ sfp.translateStringFormat(
											"compasspoints.notfound", args[1]));
							return true;
						} else {
							p.sendMessage(ChatColor.YELLOW
									+ sfp.translateString("compasspoints.removed"));
							db.delete(point);
						}
						return true;
					}
				}
			}
			sender.sendMessage(ChatColor.YELLOW
					+ sfp.translateString("compasspoints.toggle"));
		}
		return false;
	}

}
