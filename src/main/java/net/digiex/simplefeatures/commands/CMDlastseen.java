package net.digiex.simplefeatures.commands;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTranslation;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
		return formatDateDiff(fromDate, toDate, "en_US");
	}

	public static String formatDateDiff(Calendar fromDate, Calendar toDate,
			String langid) {
		SFTranslation t = SFTranslation.getInstance();
		boolean future = false;
		if (toDate.equals(fromDate)) {
			return (t.translateKey("time.rightnow", langid));
		}
		if (toDate.after(fromDate)) {
			future = true;
		}

		StringBuilder sb = new StringBuilder();
		int[] types = new int[] { Calendar.YEAR, Calendar.MONTH,
				Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
				Calendar.SECOND };
		String[] names = new String[] { t.translateKey("time.year", langid),
				t.translateKey("time.years", langid),
				t.translateKey("time.month", langid),
				t.translateKey("time.months", langid),
				t.translateKey("time.day", langid),
				t.translateKey("time.days", langid),
				t.translateKey("time.hour", langid),
				t.translateKey("time.hours", langid),
				t.translateKey("time.minute", langid),
				t.translateKey("time.minutes", langid),
				t.translateKey("time.second", langid),
				t.translateKey("time.seconds", langid) };
		for (int i = 0; i < types.length; i++) {
			int diff = dateDiff(types[i], fromDate, toDate, future);
			if (diff > 0) {
				sb.append(" ").append(diff).append(" ")
						.append(names[i * 2 + (diff > 1 ? 1 : 0)]);
			}
		}
		if (sb.length() == 0) {
			return t.translateKey("time.rightnow", langid);
		} else {
			sb.append(" " + t.translateKey("time.ago", langid));
		}
		return sb.toString();
	}

	public static String formatDateDiffSinceTimestamp(long fromTimestamp) {
		return formatDateDiffSinceTimestamp(fromTimestamp, "en_US");
	}

	public static String formatDateDiffSinceTimestamp(long fromTimestamp,
			String langid) {
		Calendar fromDate = Calendar.getInstance();
		fromDate.setTimeInMillis(fromTimestamp);
		return formatDateDiff(fromDate, Calendar.getInstance(), langid)
				+ " ("
				+ ((new SimpleDateFormat("dd.MM.yyyy")).format(new Date(
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
		String langid = "en_US";
		SFTranslation t = SFTranslation.getInstance();
		if (sender instanceof Player) {
			langid = SFPlayer.getSFPlayer((Player) sender).getLanguage();
		}

		if (!sender.hasPermission(new Permission("sfp.lastseen",
				PermissionDefault.TRUE))) {
			sender.sendMessage(ChatColor.RED
					+ t.translateKey("general.nopermission", langid));
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
					+ t.translateKey("lastseen.status", langid)
					+ " "
					+ (target.isOnline() ? ChatColor.GREEN
							+ t.translateKey("player.online", langid)
							: ChatColor.RED
									+ t.translateKey("player.offline", langid)));
			if (!target.isOnline()) {
				sender.sendMessage(ChatColor.YELLOW
						+ t.translateKey("lastseen.lastseen", langid)
						+ " "
						+ formatDateDiffSinceTimestamp(target.getLastPlayed(),
								langid));
			}
			sender.sendMessage(ChatColor.YELLOW
					+ t.translateKey("lastseen.joined", langid)
					+ " "
					+ formatDateDiffSinceTimestamp(target.getFirstPlayed(),
							langid));
		} else {
			sender.sendMessage(ChatColor.RED
					+ t.translateKey("lastseen.playernotfound", langid));
		}
		return true;
	}

}
