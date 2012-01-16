package net.digiex.simplefeatures.listeners;

import java.util.List;
import java.util.logging.Level;

import net.digiex.simplefeatures.SFMail;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ClientModListener implements PluginMessageListener {
	SFPlugin plugin;

	public ClientModListener(SFPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player,
			byte[] message) {
		if (channel.equalsIgnoreCase("simplefeatures")) {
			JSONObject json = (JSONObject) JSONValue.parse(new String(message));
			String id = ((String) json.get("id"));
			if (id.equalsIgnoreCase("login")) {
				double version = (Double) json.get("version");
				String lang = (String) json.get("lang");
				if (lang == null) {
					lang = "en_US";
				}
				SFPlugin.clientAddons.put(player.getName(), version);
				SFPlugin.playerLangs.put(player.getName(), lang);
				SFPlugin.log(Level.INFO, player.getName()
						+ " has the SFClientMod version " + version
						+ ", using language " + lang);
			} else if (id.equalsIgnoreCase("sendmail")) {
				if (!player.hasPermission(new Permission("sfp.msg",
						PermissionDefault.TRUE))) {
					player.sendMessage(ChatColor.RED
							+ "You do not have permission to send private messages");
					return;
				}
				String pname = ((String) json.get("to"));
				OfflinePlayer target = SFPlugin.getOfflinePlayer(player, pname,
						plugin);

				if (target != null) {
					player.sendMessage(String.format("Mail sent to %s: %s",
							target.getName(), json.get("mail")));
					SFMail save = new SFMail();
					save.newMail(player.getName(), target.getName(),
							((String) json.get("mail")));
					plugin.getDatabase().save(save);
					Player p = plugin.getServer().getPlayer(target.getName());
					if (p != null) {
						p.sendMessage(ChatColor.AQUA
								+ "You have new mail! Type /read to read it!");
					}
					return;
				}
			} else if (id.equalsIgnoreCase("clearmailbox")) {
				List<SFMail> msgs;
				msgs = plugin.getDatabase().find(SFMail.class).where()
						.ieq("toPlayer", player.getName()).findList();
				if (msgs.isEmpty()) {
					player.sendMessage(ChatColor.RED + "Nothing to clear!");
					return;
				} else {
					int i = 0;
					for (SFMail msg : msgs) {
						plugin.getDatabase().delete(msg);
						i++;
					}
					player.sendMessage(ChatColor.YELLOW
							+ "Successfully cleared " + i + " messages.");
					return;
				}
			} else if (id.equalsIgnoreCase("deletemail")) {
				SFMail msg = plugin.getDatabase().find(SFMail.class).where()
						.ieq("toPlayer", player.getName())
						.ieq("id", json.get("mailid").toString()).findUnique();
				if (msg == null) {
					player.sendMessage(ChatColor.RED + "Nothing to delete!");
					return;
				} else {
					player.sendMessage(ChatColor.YELLOW
							+ "Deleting 1 mail from " + msg.getFromPlayer());
					plugin.getDatabase().delete(msg);
					return;
				}
			}

			else {
				SFPlugin.log(Level.INFO, "Unknown SFPacket! Contents: "
						+ new String(message));
			}

		}
	}

}
