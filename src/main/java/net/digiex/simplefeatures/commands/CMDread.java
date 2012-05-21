package net.digiex.simplefeatures.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTranslation;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDread implements CommandExecutor {

	private final SFPlugin plugin;

	public CMDread(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String langid = "en_US";
		SFTranslation t = SFTranslation.getInstance();
		SFPlayer sfp = null;
		if (sender instanceof Player) {
			sfp = SFPlayer.getSFPlayer((Player) sender);
			langid = sfp.getLanguage();
		}
		int page = 1;
		if (args.length > 0) {
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				sender.sendMessage(ChatColor.RED
						+ t.translateKeyFormat("read.invalidnumber", langid,
								args[0]));
				return false;
			}
		}
		List<SFMail> msgs;
		msgs = plugin.getDatabase().find(SFMail.class).where()
				.ieq("toPlayer", sender.getName()).orderBy("timestamp DESC")
				.findList();
		if (sfp != null) {
			if (sfp.hasClientMod() && sfp.getClientModVersion() > 0.2) {
				sfp.showMailboxGui(msgs);
				return true;
			}
		}
		if (msgs.isEmpty()) {
			sender.sendMessage(ChatColor.RED
					+ t.translateKey("read.emptymailbox", langid));
			return true;
		} else {
			final int start = (page - 1) * 5;
			final int pages = msgs.size() / 5 + (msgs.size() % 5 > 0 ? 1 : 0);

			sender.sendMessage(ChatColor.GREEN
					+ t.translateKey("general.page", langid) + " " + page + "/"
					+ pages);
			for (int i = start; i < msgs.size() && i < start + 5; i++) {
				OfflinePlayer op = plugin.getServer().getOfflinePlayer(
						msgs.get(i).getFromPlayer());
				String dispName = ChatColor.YELLOW + op.getName()
						+ ChatColor.WHITE;
				if (op.isOp()) {
					dispName = (ChatColor.AQUA + op.getName() + ChatColor.WHITE);
				} else {
					dispName = (ChatColor.GREEN + op.getName() + ChatColor.WHITE);
				}
				sender.sendMessage(ChatColor.GRAY
						+ ((new SimpleDateFormat("dd.MM")).format(new Date(msgs
								.get(i).getTimestamp()))) + " " + dispName
						+ ": " + msgs.get(i).getMessage());
			}
			sender.sendMessage(ChatColor.AQUA
					+ t.translateKey("read.usecleartoempty", langid));
			return true;
		}
	}
}
