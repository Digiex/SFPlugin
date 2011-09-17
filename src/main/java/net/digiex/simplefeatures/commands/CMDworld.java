package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDworld implements CommandExecutor{
	SFPlugin plugin;
	public CMDworld(SFPlugin parent){
		this.plugin = parent;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			if(args.length > 0){
				World world = plugin.getServer().getWorld(args[0]);
				if(world != null){
					((Player) sender).teleport(world.getSpawnLocation());
					sender.sendMessage(ChatColor.GRAY+"Teleporting to "+world.getName());
					return true;
				}
			}
		}
		return false;
	}

}
