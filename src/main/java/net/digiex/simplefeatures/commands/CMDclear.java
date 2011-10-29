package net.digiex.simplefeatures.commands;

import java.util.List;

import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CMDclear  implements CommandExecutor {


	private SFPlugin plugin;

	public CMDclear(SFPlugin parent) {
		this.plugin = parent;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		List<SFMail> msgs;
			msgs = plugin.getDatabase().find(SFMail.class).where()
					.ieq("toPlayer", sender.getName()).findList();
		if (msgs.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "Nothing to clear!");
			return true;
		} else {
			int i = 0;
			for (SFMail msg : msgs) {
				plugin.getDatabase().delete(msg);
				i++;
			}
			sender.sendMessage(ChatColor.YELLOW+"Successfully cleared "+i+" messages.");
			return true;
		}
	}


}
