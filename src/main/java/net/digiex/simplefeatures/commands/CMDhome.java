package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.Home;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDhome implements CommandExecutor {
	SFPlugin plugin;

	public CMDhome(SFPlugin parent) {
		this.plugin = parent;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player player = SFPlugin.getPlayer(sender, args[0]);
		if (player == null) {
			return true;
		} else if ((player != sender) && (!sender.isOp())) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to go to other players homes");
			return true;
		} else if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "I don't know how to move you!");
			return true;
		}
		Home home = plugin.getDatabase().find(Home.class).where()
				.ieq("worldName", player.getLocation().getWorld().getName()).ieq("playerName", player.getName())
				.findUnique();
		if (home == null) {
			if(player.getBedSpawnLocation() != null && player.getBedSpawnLocation().getWorld() == ((Player) sender).getWorld()){
				((Player) sender).teleport(player.getBedSpawnLocation());
			}else{
				sender.sendMessage(ChatColor.RED + "I don't know where that is!");
			}
		} else {
			((Player) sender).teleport(home.getLocation());
		}
		return true;
	}

}
