package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTranslation;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDsend implements CommandExecutor {

	private final SFPlugin parent;

	public CMDsend(SFPlugin parent) {
		this.parent = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String string,
			String[] args) {
		if (args.length < 2) {
			return false;
		}

		String langid = "en_US";
		if (sender instanceof Player) {
			langid = new SFPlayer((Player) sender).getLanguage();
		}
		SFTranslation t = SFTranslation.getInstance();
		if (!sender.hasPermission(new Permission("sfp.msg",
				PermissionDefault.TRUE))) {
			sender.sendMessage(ChatColor.RED
					+ t.translateKey("general.nopermission", langid));
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
			sender.sendMessage(t.translateKeyFormat("mail.sent", langid,
					target.getName(), message));
			SFMail save = new SFMail();
			save.newMail(sender.getName(), target.getName(), message);
			parent.getDatabase().save(save);
			Player p = parent.getServer().getPlayer(target.getName());
			if (p != null) {
				p.sendMessage(ChatColor.AQUA
						+ new SFPlayer(p).translateString("mail.newmailnotify"));
			}
			return true;
		}
		return false;
	}

}
