package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDsendall implements CommandExecutor {

	private final SFPlugin parent;

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
			return true;
		}
		if (args.length < 1) {
			return false;
		}
		String message = SFPlugin.recompileMessage(args, 0, args.length - 1);
		int i = 0;

		for (OfflinePlayer op : parent.getServer().getOfflinePlayers()) {
			if (op.isWhitelisted()) {
				SFMail save = new SFMail();
				save.newMail(sender.getName(), op.getName(), message);
				parent.getDatabase().save(save);
				Player p = op.getPlayer();
				if (p != null) {
					p.sendMessage(ChatColor.AQUA
							+ "You have new mail! Type /read to read it!");
				}
				i++;
			}
		}
		sender.sendMessage("Message sent for " + i + " players.");
		return true;
	}

}
