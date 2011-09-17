package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDspawn implements CommandExecutor{
	SFPlugin plugin;
	public CMDspawn(SFPlugin parent){
		this.plugin = parent;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			SFPlayer sfplayer = new SFPlayer((Player) sender);
			sfplayer.teleportToSpawn();
			sender.sendMessage(ChatColor.GRAY+"Spawn!");
			return true;
		}
		return false;
	}

}
