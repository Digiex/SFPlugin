package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDhome implements CommandExecutor{
	SFPlugin plugin;
	public CMDhome(SFPlugin parent){
		this.plugin = parent;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			SFPlayer sfplayer = new SFPlayer((Player) sender);
			sfplayer.teleportToHome();
			sender.sendMessage(ChatColor.GRAY+"Home sweet home!");
			return true;

		}
		return false;
	}

}
