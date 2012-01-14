package net.digiex.simplefeatures.listeners;

import java.util.logging.Level;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.entity.Player;
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

			if (((String) json.get("id")).equalsIgnoreCase("login")) {
				double version = (Double) json.get("version");
				SFPlugin.clientAddons.put(player.getName(), version);
				SFPlugin.log(Level.INFO, player.getName()
						+ " has the SFClientMod version " + version);
			}

		}
	}

}
