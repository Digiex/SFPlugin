package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTranslation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDreply implements CommandExecutor {
	SFPlugin plugin;

	public CMDreply(SFPlugin parent) {
		plugin = parent;
	}

	private CommandSender getTarget(Player player) {
		PluginCommand command = plugin.getCommand("msg");

		if ((command != null) && (command.getExecutor() instanceof CMDmsg)) {
			return ((CMDmsg) command.getExecutor()).getLastSender(player);
		} else {
			return null;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player) || (args.length < 1)) {
			return false;
		}
		String langid = "en_US";
		if (sender instanceof Player) {
			langid = SFPlayer.getSFPlayer((Player) sender).getLanguage();
		}
		SFTranslation t = SFTranslation.getInstance();
		if (!sender.hasPermission(new Permission("sfp.msg",
				PermissionDefault.TRUE))) {
			sender.sendMessage(ChatColor.RED
					+ t.translateKey("general.nopermission", langid));
			return true;
		}

		Player player = (Player) sender;
		CommandSender target = getTarget(player);

		if (target == null) {
			sender.sendMessage(ChatColor.RED
					+ t.translateKey("reply.nobodytoreply", langid));
		} else {
			String message = SFPlugin
					.recompileMessage(args, 0, args.length - 1);
			String name = "console";

			if (target instanceof Player) {
				name = ((Player) target).getDisplayName();
			}

			target.sendMessage(String.format("[%s]->[%s]: %s",
					player.getDisplayName(),
					t.translateKey("general.you", langid), message));
			sender.sendMessage(String.format("[%s]->[%s]: %s",
					t.translateKey("general.you", langid), name, message));
		}

		return true;
	}

}
