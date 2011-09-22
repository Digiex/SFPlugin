package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.Home;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDsethome implements CommandExecutor {
	SFPlugin plugin;

	public CMDsethome(SFPlugin parent) {
		this.plugin = parent;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String name = null;
		if(args.length > 0){
			name = args[0];
		}
		Player player = SFPlugin.getPlayer(sender, name);
		if (player == null) {
			return true;
		} else if ((player != sender) && (!sender.isOp())) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to set other players homes");
			return true;
		} else if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "I don't know where you are!");
			return true;
		}
		Home home = plugin.getDatabase().find(Home.class).where()
				.ieq("worldName", player.getLocation().getWorld().getName()).ieq("playerName", player.getName())
				.findUnique();
		if (home == null) {
			home = new Home();
			home.setPlayer(player);
		}
		home.setLocation(((Player) sender).getLocation());
		plugin.getDatabase().save(home);
		return true;
	}

}
