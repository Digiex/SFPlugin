package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTranslation;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wimbli.WorldBorder.BorderData;

public class CMDworld implements CommandExecutor {

	SFPlugin plugin;

	public CMDworld(SFPlugin parent) {
		plugin = parent;
	}

	private void ListWorlds(CommandSender sender, String tried, String langid) {
		SFTranslation t = SFTranslation.getInstance();
		if (tried.length() > 0) {
			sender.sendMessage(ChatColor.RED
					+ t.translateKeyFormat("world.notfound", langid, tried));
		}
		sender.sendMessage(ChatColor.GREEN
				+ t.translateKey("world.available", langid));
		for (World w : plugin.getServer().getWorlds()) {
			if (w.getName().contains("_nether")) {
				boolean allownether = false;
				if (sender instanceof Player) {
					allownether = ((Player) sender).isOp();
				} else {
					allownether = true;
				}
				if (allownether) {
					sender.sendMessage(ChatColor.GRAY + "Nether");
				}
			} else if (w.getName().contains("_the_end")) {
				boolean allowskylands = false;
				if (sender instanceof Player) {
					allowskylands = ((Player) sender).isOp();
				} else {
					allowskylands = true;
				}
				if (allowskylands) {
					sender.sendMessage(ChatColor.GRAY + "End");
				}
			} else {
				sender.sendMessage(ChatColor.YELLOW + w.getName());
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			SFPlayer sfp = SFPlayer.getSFPlayer(player);
			if (sfp.isTeleporting()) {
				player.sendMessage(ChatColor.GRAY
						+ sfp.translateString("teleport.inprogress"));
				return true;
			}
			if (args.length > 0) {
				World world = null;
				for (World w : plugin.getServer().getWorlds()) {
					String wname = w.getName();
					if (w.getName().contains("_nether")) {
						wname = "Nether";
					}
					if (w.getName().contains("_the_end")) {
						wname = "End";
					}
					if (wname.equalsIgnoreCase(args[0])) {
						world = w;
					} else if (wname.toLowerCase().indexOf(
							args[0].toLowerCase()) != -1) {
						world = w;
					}
				}
				if (world != null) {
					if (world.getName().contains("_nether")) {
						if (!((Player) sender).isOp()) {
							sender.sendMessage(ChatColor.RED
									+ "Use a Nether Portal in Survival to enter Nether");
							return true;
						} else {
							sender.sendMessage("Wait! You need to use nether portals!!! Oh you're an OP... Sorry, my mistake.");
						}
					}
					if (world.getName().contains("_the_end")) {
						if (!((Player) sender).isOp()) {
							sender.sendMessage(ChatColor.RED
									+ "Use a Enderportal in Survival to enter The End");
							return true;
						} else {
							sender.sendMessage("Wait! You need to use Enderportals!!! Oh you're an OP... Sorry, my mistake.");
						}
					}
					Location loc = sfp.getLastLocation(world);
					if (loc == null) {
						loc = world.getSpawnLocation();
					}
					if (SFPlugin.worldBorderPlugin != null) {
						BorderData bData = SFPlugin.worldBorderPlugin
								.GetWorldBorder(loc.getWorld().getName());
						if (bData != null) {
							if (!bData.insideBorder(loc)) {
								player.sendMessage(ChatColor.RED
										+ sfp.translateString("teleport.outsideofborder"));
								return true;
							}
						}
					}
					sfp.teleport(
							loc,
							sfp.translateStringFormat("world.tpingto",
									world.getName()));
					return true;

				} else if (world == player.getWorld()) {
					sender.sendMessage(ChatColor.RED
							+ sfp.translateString("world.inthisworld"));
					return true;
				}
				ListWorlds(sender, args[0], sfp.getLanguage());
				return true;
			}
			ListWorlds(sender, "", sfp.getLanguage());
			return true;
		}
		return false;
	}
}
