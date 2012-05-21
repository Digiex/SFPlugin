package net.digiex.simplefeatures.commands;

import java.util.List;

import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDclear implements CommandExecutor {

	private final SFPlugin plugin;

	public CMDclear(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SFPlayer sfp = null;
		if (sender instanceof Player) {
			sfp = SFPlayer.getSFPlayer((Player) sender);
		}
		List<SFMail> msgs;
		msgs = plugin.getDatabase().find(SFMail.class).where()
				.ieq("toPlayer", sender.getName()).findList();
		if (msgs.isEmpty()) {
			if (sfp != null) {
				sender.sendMessage(ChatColor.RED
						+ sfp.translateString("clear.nothingtoclear"));
			} else {
				sender.sendMessage(ChatColor.RED + "Nothing to clear!");
			}
			return true;
		} else {
			int i = 0;
			for (SFMail msg : msgs) {
				plugin.getDatabase().delete(msg);
				i++;
			}
			if (sfp != null) {
				sender.sendMessage(ChatColor.YELLOW
						+ sfp.translateStringFormat("clear.success", i));
			} else {
				sender.sendMessage(ChatColor.YELLOW + "Successfully cleared "
						+ i + " messages.");
			}
			return true;
		}
	}

}
