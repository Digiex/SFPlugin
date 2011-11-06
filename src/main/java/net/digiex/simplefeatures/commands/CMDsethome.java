package net.digiex.simplefeatures.commands;

import java.util.HashSet;
import java.util.Set;

import net.digiex.simplefeatures.SFHome;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDsethome implements CommandExecutor {
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
	}

	public CMDsethome(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.hasPermission(new Permission("sf.sethome",
					PermissionDefault.OP))) {
				player.sendMessage(ChatColor.RED + "Use a bed to set a home!");
				return true;
			}
			if (player.getWorld().getName().contains("_nether")
					|| player.getWorld().getName().contains("_skylands")) {
				player.sendMessage(ChatColor.RED
						+ "You can not set a home in this dimension!");
				return true;
			}
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

				home.setLocation(player.getLocation());
				player.setCompassTarget(player.getLocation());

				if (isUpdate) {
					db.update(home, updateProps);
				}
				db.save(home);
				db.commitTransaction();
			} finally {
				db.endTransaction();
			}
			return true;
		}
		return false;
	}

}
