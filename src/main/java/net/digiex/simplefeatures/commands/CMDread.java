package net.digiex.simplefeatures.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CMDread implements CommandExecutor {

	private SFPlugin plugin;

	public CMDread(SFPlugin parent) {
		this.plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		int page = 1;
		if (args.length > 0) {
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				sender.sendMessage("Are you sure that " + args[0]
						+ " is a number?");
				return false;
			}
		}
		List<SFMail> msgs;
		msgs = plugin.getDatabase().find(SFMail.class).where()
				.ieq("toPlayer", sender.getName()).orderBy("timestamp DESC")
				.findList();

		if (msgs.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "Nothing found!");
			return true;
		} else {
			final int start = (page - 1) * 9;
			final int pages = msgs.size() / 9 + (msgs.size() % 9 > 0 ? 1 : 0);

			sender.sendMessage(ChatColor.GREEN + "Page " + page + " of "
					+ pages);
			for (int i = start; i < msgs.size() && i < start + 9; i++) {
				sender.sendMessage(ChatColor.GRAY
						+ ((new SimpleDateFormat("dd.MM")).format(new Date(msgs
								.get(i).getTimestamp()))) + " "
						+ ChatColor.YELLOW + msgs.get(i).getFromPlayer()
						+ ChatColor.WHITE + ": " + msgs.get(i).getMessage());
			}
			return true;
		}
	}

}
