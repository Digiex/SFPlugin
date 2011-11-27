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

public class CMDsend implements CommandExecutor {

	private SFPlugin parent;

	public CMDsend(SFPlugin parent) {
		this.parent = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String string,
			String[] args) {
		if (args.length < 2) {
			return false;
		}

		if (!sender.hasPermission(new Permission("sfp.msg",
				PermissionDefault.TRUE))) {
			sender.sendMessage(ChatColor.RED
					+ "You do not have permission to send private messages");
			return true;
		}
		String pname = null;
		if (args.length > 0) {
			pname = args[0];
		}
		OfflinePlayer target = SFPlugin.getOfflinePlayer(sender, pname, parent);

		if (target != null) {
			String message = SFPlugin
					.recompileMessage(args, 1, args.length - 1);
			sender.sendMessage(String.format("Mail sent to %s: %s",
					target.getName(), message));
			SFMail save = new SFMail();
			save.newMail(sender.getName(), target.getName(), message);
			parent.getDatabase().save(save);
			Player p = parent.getServer().getPlayer(target.getName());
			if (p != null) {
				p.sendMessage(ChatColor.AQUA
						+ "You have new mail! Type /read to read it!");
			}
			return true;
		}
		return false;
	}

}
