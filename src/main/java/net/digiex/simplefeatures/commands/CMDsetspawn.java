package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDsetspawn implements CommandExecutor{
	SFPlugin plugin;
	public CMDsetspawn(SFPlugin parent){
		this.plugin = parent;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Location loc = ((Player) sender).getLocation();
			loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			sender.sendMessage(ChatColor.GRAY+"Spawn set!");
			return true;
		}
		return false;
	}

}
