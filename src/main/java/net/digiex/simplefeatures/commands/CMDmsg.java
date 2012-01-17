package net.digiex.simplefeatures.commands;

import java.util.HashMap;
import java.util.Map;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTranslation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDmsg implements CommandExecutor {
	SFPlugin plugin;
	private final Map<Player, CommandSender> lastMessages = new HashMap<Player, CommandSender>();

	public CMDmsg(SFPlugin parent) {
		plugin = parent;
	}

	public CommandSender getLastSender(Player player) {
		return lastMessages.get(player);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
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
		Player target = SFPlugin.getPlayer(sender, pname);

		if (target != null) {
			String message = SFPlugin
					.recompileMessage(args, 1, args.length - 1);
			String name = "console";

			if (sender instanceof Player) {
				name = ((Player) sender).getDisplayName();
			}

			target.sendMessage(String.format("[%s]->[%s]: %s", name,
					t.translateKey("general.you", langid), message));
			sender.sendMessage(String.format("[%s]->[%s]: %s",
					t.translateKey("general.you", langid),
					target.getDisplayName(), message));

			lastMessages.put(target, sender);
		}

		return true;
	}

}
