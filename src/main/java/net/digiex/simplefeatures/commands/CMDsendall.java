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

public class CMDsendall implements CommandExecutor {

	private final SFPlugin parent;

	public CMDsendall(SFPlugin parent) {
		this.parent = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String string,
			String[] args) {
		String langid = "en_US";
		if (sender instanceof Player) {
			langid = SFPlayer.getSFPlayer((Player) sender).getLanguage();
		}
		SFTranslation t = SFTranslation.getInstance();
		if (!sender.hasPermission(new Permission("sfp.sendall",
				PermissionDefault.OP))) {
			sender.sendMessage(ChatColor.RED
					+ t.translateKey("general.nopermission", langid));
			return true;
		}
		if (args.length < 1) {
			return false;
		}
		String message = SFPlugin.recompileMessage(args, 0, args.length - 1);
		int i = 0;

		for (OfflinePlayer op : parent.getServer().getOfflinePlayers()) {
			SFMail save = new SFMail();
			save.newMail(sender.getName(), op.getName(), message);
			parent.getDatabase().save(save);
			Player p = op.getPlayer();
			if (p != null) {
				p.sendMessage(ChatColor.AQUA
						+ SFPlayer.getSFPlayer(p).translateString(
								"mail.newmailnotify"));
			}
			i++;
		}
		sender.sendMessage(t.translateKeyFormat("mailall.sent", langid, i));
		return true;
	}

}
