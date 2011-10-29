package net.digiex.simplefeatures.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import net.digiex.simplefeatures.SFHome;
import net.digiex.simplefeatures.SFInventory;
import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDsendall implements CommandExecutor {

	private SFPlugin parent;

	public CMDsendall(SFPlugin parent) {
		this.parent = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String string,
			String[] args) {
		if (!sender.hasPermission(new Permission("sf.sendall",
				PermissionDefault.OP))) {
			sender.sendMessage(ChatColor.RED + "Don't do that again or "
					+ ChatColor.AQUA + "sharks" + ChatColor.RED
					+ " will eat you! Seriously, no permission to do that.");
		}
		if (args.length < 1) {
			return false;
		}
		String message = SFPlugin.recompileMessage(args, 0, args.length - 1);
		int i = 0;
		List<String> sent = new ArrayList<String>();
		
		List<SFInventory> invs;
		invs = parent.getDatabase().find(SFInventory.class)
				.findList();
		for(SFInventory inv : invs){
			if (!sent.contains(inv.getPlayerName())) {
				SFMail save = new SFMail();
				save.newMail(sender.getName(), inv.getPlayerName(), message);
				parent.getDatabase().save(save);
				Player p = parent.getServer().getPlayer(inv.getPlayerName());
				if (p != null) {
					p.sendMessage(ChatColor.AQUA
							+ "You have new mail! Type /read to read it!");
				}
				sent.add(inv.getPlayerName());
				i++;
			}
		}
		sender.sendMessage("Message sent for " + i + " players.");
		return true;
	}

}
