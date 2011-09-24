package net.digiex.simplefeatures.commands;

import java.util.List;

import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CMDlastmsgs implements CommandExecutor {
	SFPlugin plugin;

	public CMDlastmsgs(SFPlugin parent) {
		this.plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		List<SFMail> msgs;
		if (args.length > 0) {
			msgs = plugin.getDatabase().find(SFMail.class).where()
					.ieq("toPlayer", sender.getName())
					.ieq("fromPlayer", args[0]).setMaxRows(5)
					.orderBy("timestamp DESC").findList();
		} else {
			msgs = plugin.getDatabase().find(SFMail.class).where()
					.ieq("toPlayer", sender.getName()).setMaxRows(5)
					.orderBy("timestamp DESC").findList();
		}
		if (msgs.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "Nothing found!");
			return true;
		} else {
			for (SFMail msg : msgs) {
				sender.sendMessage(ChatColor.YELLOW + msg.getFromPlayer()
						+ ChatColor.WHITE + ": " + msg.getMessage());
			}
			return true;
		}
	}
}
