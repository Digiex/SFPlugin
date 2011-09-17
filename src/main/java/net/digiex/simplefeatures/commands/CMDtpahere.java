package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.TeleportConfirmTask;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDtpahere implements CommandExecutor{
	SFPlugin plugin;
	public CMDtpahere(SFPlugin parent){
		this.plugin = parent;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			if(args.length > 0){
				Player to = plugin.getServer().getPlayer(args[0]);
				if(to != null){
					plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new TeleportConfirmTask((Player)sender, to, true));
					sender.sendMessage(ChatColor.GRAY+"Requesting!");
					return true;
				}
			}
		}
		return false;
	}

}
