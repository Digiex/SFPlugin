package net.digiex.simplefeatures.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class CMDhelp implements CommandExecutor {
	SFPlugin plugin;
	private static final String DESCRIPTION = "description";
	private static final String PERMISSION = "permission";
	private static final String PERMISSIONS = "permissions";
	public boolean showpermless = true;
	public final Yaml yaml = new Yaml(new SafeConstructor());

	public CMDhelp(SFPlugin parent) {
		this.plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		int page = 1;
		@SuppressWarnings("unused")
		String match = "";
		try {
			if (args.length > 0) {
				match = args[0].toLowerCase();
				page = Integer.parseInt(args[args.length - 1]);
				if (args.length == 1) {
					match = "";
				}
			}

		} catch (Exception ex) {
			if (args.length == 1) {
				match = args[0].toLowerCase();
			}
		}

		List<String> lines;
		// TODO: Add help this way at some point but make it work without
		// permissions also
		// try {
		// lines = getHelpLines(sender, match);
		// } catch (Exception e) {
		// sender.sendMessage("Help failed: " + e.getMessage());
		// e.printStackTrace();
		// return true;
		// }
		lines = new ArrayList<String>();
		lines.add(ChatColor.RED + "home:" + ChatColor.YELLOW
				+ " Teleport to home");
		lines.add(ChatColor.RED + "who:" + ChatColor.YELLOW
				+ " Show player list");
		lines.add(ChatColor.RED + "world:" + ChatColor.YELLOW
				+ " Teleport across worlds");
		lines.add(ChatColor.RED + "spawn:" + ChatColor.YELLOW
				+ " Teleport to the world spawn");
		lines.add(ChatColor.RED + "tpa:" + ChatColor.YELLOW
				+ " Teleport to player");
		lines.add(ChatColor.RED + "tpahere:" + ChatColor.YELLOW
				+ " Teleport a player here");
		lines.add(ChatColor.RED + "msg, m:" + ChatColor.YELLOW
				+ " Send a private message");
		lines.add(ChatColor.RED + "reply, r:" + ChatColor.YELLOW
				+ " reply to a message");
		lines.add(ChatColor.RED + "lastmsgs:" + ChatColor.YELLOW
				+ " Show last messages");
		lines.add(ChatColor.RED + "me:" + ChatColor.YELLOW
				+ " Express yourself");
		lines.add(ChatColor.RED + "listhomes:" + ChatColor.YELLOW
				+ " List your homes");
		if (lines.isEmpty()) {
			sender.sendMessage("No help found");
			return true;
		}

		final int start = (page - 1) * 9;
		final int pages = lines.size() / 9 + (lines.size() % 9 > 0 ? 1 : 0);

		sender.sendMessage(ChatColor.GREEN + "Page " + page + " of " + pages);
		for (int i = start; i < lines.size() && i < start + 9; i++) {
			sender.sendMessage(lines.get(i));
		}

		return true;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private List<String> getHelpLines(final CommandSender player,
			final String match) throws Exception {
		final List<String> retval = new ArrayList<String>();

		boolean reported = false;
		String pluginName = "";
		for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
			try {
				final PluginDescriptionFile desc = p.getDescription();
				final HashMap<String, HashMap<String, Object>> cmds = (HashMap<String, HashMap<String, Object>>) desc
						.getCommands();
				pluginName = p.getDescription().getName().toLowerCase();
				for (Entry<String, HashMap<String, Object>> k : cmds.entrySet()) {
					try {
						if ((!match.equalsIgnoreCase(""))
								&& (!k.getKey().toLowerCase().contains(match))
								&& (!(k.getValue().get(DESCRIPTION) instanceof String && ((String) k
										.getValue().get(DESCRIPTION))
										.toLowerCase().contains(match)))
								&& (!pluginName.contains(match))) {
							continue;
						}

						final HashMap<String, Object> value = k.getValue();
						if (value.containsKey(PERMISSION)
								&& value.get(PERMISSION) instanceof String
								&& !(value.get(PERMISSION).equals(""))) {
							if (player.hasPermission((String) value
									.get(PERMISSION))) {
								retval.add(ChatColor.RED + k.getKey()
										+ ChatColor.YELLOW + ": "
										+ value.get(DESCRIPTION));
							}
						} else if (value.containsKey(PERMISSION)
								&& value.get(PERMISSION) instanceof List
								&& !((List<Object>) value.get(PERMISSION))
										.isEmpty()) {
							boolean enabled = false;
							for (Object o : (List<Object>) value
									.get(PERMISSION)) {
								if (o instanceof String
										&& player.hasPermission((String) o)) {
									enabled = true;
									break;
								}
							}
							if (enabled) {
								retval.add(ChatColor.RED + k.getKey()
										+ ChatColor.YELLOW + ": "
										+ value.get(DESCRIPTION));
							}
						} else if (value.containsKey(PERMISSIONS)
								&& value.get(PERMISSIONS) instanceof String
								&& !(value.get(PERMISSIONS).equals(""))) {
							if (player.hasPermission((String) value
									.get(PERMISSION))) {
								retval.add(ChatColor.RED + k.getKey()
										+ ChatColor.YELLOW + ": "
										+ value.get(DESCRIPTION));
							}
						} else if (value.containsKey(PERMISSIONS)
								&& value.get(PERMISSIONS) instanceof List
								&& !((List<Object>) value.get(PERMISSIONS))
										.isEmpty()) {
							boolean enabled = false;
							for (Object o : (List<Object>) value
									.get(PERMISSIONS)) {
								if (o instanceof String
										&& player.hasPermission((String) o)) {
									enabled = true;
									break;
								}
							}
							if (enabled) {
								retval.add(ChatColor.RED + k.getKey()
										+ ChatColor.YELLOW + ": "
										+ value.get(DESCRIPTION));
							}
						} else if (player
								.hasPermission(new Permission("sf.help."
										+ pluginName, PermissionDefault.TRUE))) {
							retval.add(ChatColor.RED + k.getKey()
									+ ChatColor.YELLOW + ": "
									+ value.get(DESCRIPTION));
						} else {
							if (showpermless || player.isOp()) {
								retval.add(ChatColor.RED + k.getKey()
										+ ChatColor.YELLOW + ": "
										+ value.get(DESCRIPTION)
										+ " (permless)");
							}
						}
					} catch (NullPointerException ex) {
						continue;
					}
				}
			} catch (NullPointerException ex) {
				continue;
			} catch (Exception ex) {
				if (!reported) {
					SFPlugin.log(Level.WARNING, "Help failed for " + pluginName);
					ex.printStackTrace();
				}
				reported = true;
				continue;
			}
		}
		return retval;
	}

}
