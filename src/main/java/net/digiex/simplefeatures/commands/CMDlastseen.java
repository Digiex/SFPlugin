package net.digiex.simplefeatures.commands;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDlastseen implements CommandExecutor {

	private static int dateDiff(int type, Calendar fromDate, Calendar toDate,
			boolean future) {
		int diff = 0;
		long savedDate = fromDate.getTimeInMillis();
		while ((future && !fromDate.after(toDate))
				|| (!future && !fromDate.before(toDate))) {
			savedDate = fromDate.getTimeInMillis();
			fromDate.add(type, future ? 1 : -1);
			diff++;
		}
		diff--;
		fromDate.setTimeInMillis(savedDate);
		return diff;
	}

	public static String formatDateDiff(Calendar fromDate, Calendar toDate) {
		boolean future = false;
		if (toDate.equals(fromDate)) {
			return ("right now");
		}
		if (toDate.after(fromDate)) {
			future = true;
		}

		StringBuilder sb = new StringBuilder();
		int[] types = new int[] { Calendar.YEAR, Calendar.MONTH,
				Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
				Calendar.SECOND };
		String[] names = new String[] { "year", ("years"), ("month"),
				("months"), ("day"), ("days"), ("hour"), ("hours"), ("minute"),
				("minutes"), ("second"), ("seconds") };
		for (int i = 0; i < types.length; i++) {
			int diff = dateDiff(types[i], fromDate, toDate, future);
			if (diff > 0) {
				sb.append(" ").append(diff).append(" ")
						.append(names[i * 2 + (diff > 1 ? 1 : 0)]);
			}
		}
		if (sb.length() == 0) {
			return "right now";
		} else {
			sb.append(" ago");
		}
		return sb.toString();
	}

	public static String formatDateDiffSinceTimestamp(long fromTimestamp) {
		Calendar fromDate = Calendar.getInstance();
		fromDate.setTimeInMillis(fromTimestamp);
		return formatDateDiff(fromDate, Calendar.getInstance())
				+ " ("
				+ ((new SimpleDateFormat("dd.MM.YYYY")).format(new Date(
						fromTimestamp))) + ")";
	}

	SFPlugin plugin;

	public CMDlastseen(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length < 1) {
			return false;
		}

		if (!sender.hasPermission(new Permission("sfp.lastseen",
				PermissionDefault.TRUE))) {
			sender.sendMessage(ChatColor.RED
					+ "You do not have permission to send private messages");
			return true;
		}
		OfflinePlayer target = SFPlugin.getOfflinePlayer(sender, args[0],
				plugin);

		if (target != null) {
			String dispName = target.getName();
			if (target.isOnline()) {
				dispName = plugin.getServer().getPlayer(target.getName())
						.getDisplayName();
			} else {
				if (target.isOp()) {
					dispName = (ChatColor.AQUA + target.getName() + ChatColor.WHITE);
				} else {
					dispName = (ChatColor.GREEN + target.getName() + ChatColor.WHITE);
				}
			}

			sender.sendMessage(ChatColor.YELLOW + dispName + ":");
			sender.sendMessage(ChatColor.YELLOW
					+ "Status: "
					+ (target.isOnline() ? ChatColor.GREEN + "online"
							: ChatColor.RED + "offline"));
			sender.sendMessage(ChatColor.YELLOW + "Last seen "
					+ formatDateDiffSinceTimestamp(target.getLastPlayed()));
			sender.sendMessage(ChatColor.YELLOW + "Joined "
					+ formatDateDiffSinceTimestamp(target.getFirstPlayed()));
		} else {
			sender.sendMessage(ChatColor.RED
					+ "No player with that name found.");
		}
		return true;
	}

}
