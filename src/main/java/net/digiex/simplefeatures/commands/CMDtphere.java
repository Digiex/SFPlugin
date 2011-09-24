package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDtphere implements CommandExecutor {

    SFPlugin plugin;

    public CMDtphere(SFPlugin parent) {
        this.plugin = parent;
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			Player from = null;
			Player to = p;
			if(p.isOp()){
				if(args.length == 1){
					from = SFPlugin.getPlayer(sender, args[0]);
				}
				if(from != null && to != null){
					from.teleport(to);
					sender.sendMessage("Teleported ;)");
					return true;
				}
			}
		}
		return false;
	}
}
