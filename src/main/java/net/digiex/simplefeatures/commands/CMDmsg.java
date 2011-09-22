package net.digiex.simplefeatures.commands;

import java.util.HashMap;
import java.util.Map;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CMDmsg implements CommandExecutor {
	SFPlugin plugin;
	private Map<Player, CommandSender> lastMessages = new HashMap<Player, CommandSender>();

	public CMDmsg(SFPlugin parent) {
		this.plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length < 2) {
			return false;
		}

		if (!sender.hasPermission(new Permission("sfp.msg",PermissionDefault.TRUE))) {
			sender.sendMessage(ChatColor.RED
					+ "You do not have permission to send private messages");
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
			String name = "Anonymous";

			if (sender instanceof Player) {
				name = ((Player) sender).getDisplayName();
			}

			target.sendMessage(String.format("[%s]->[you]: %s", name, message));
			sender.sendMessage(String.format("[you]->[%s]: %s",
					target.getDisplayName(), message));

			lastMessages.put(target, sender);
		}

		return true;
	}

	public CommandSender getLastSender(Player player) {
		return lastMessages.get(player);
	}

}
