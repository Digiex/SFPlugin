package net.digiex.simplefeatures.commands;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.wimbli.WorldBorder.BorderData;

public class CMDtpahere implements CommandExecutor {

	SFPlugin plugin;

	public CMDtpahere(SFPlugin parent) {
		plugin = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			if (args.length > 0) {
				Player to = SFPlugin.getPlayer(sender, args[0]);
				SFPlayer sfp = SFPlayer.getSFPlayer(to);
				if (to != null) {
					if (sfp.isTeleporting()) {
						player.sendMessage(ChatColor.GRAY
								+ sfp.translateStringFormat(
										"teleport.playertping",
										to.getDisplayName()));
						return true;
					}
					if (player.getName().equals(to.getName())) {
						player.sendMessage(ChatColor.GRAY
								+ sfp.translateString("teleport.cannottptoself"));
						return true;
					}
					if (SFPlugin.worldBorderPlugin != null) {
						BorderData bData = SFPlugin.worldBorderPlugin
								.GetWorldBorder(to.getWorld().getName());
						if (bData != null) {
							if (!bData.insideBorder(to.getLocation())) {
								player.sendMessage(ChatColor.RED
										+ sfp.translateString("teleport.outsideofborder"));
								return true;
							}
						}
					}
					player.sendMessage(ChatColor.GRAY
							+ sfp.translateString("teleport.requesting"));
					SFPlayer sfto = SFPlayer.getSFPlayer(to);
					sfto.requestTeleport(sfp, true);
					Prompt prompt = new BooleanPrompt() {
						@Override
						protected Prompt acceptValidatedInput(
								ConversationContext context, boolean input) {
							Conversable to = context.getForWhom();
							final SFPlayer target = SFPlayer.getSFPlayer(
									(Player) to).getTeleportRequest();
							if (target == null) {
								to.sendRawMessage("It appeas that your friend has left already");
							}
							if (input) {
								to.sendRawMessage("Ok, you will arrive to your friend soon :)");
								target.getPlayer()
										.sendMessage(
												((Player) to).getDisplayName()
														+ " accepted your teleport request");
								try {
									SFPlayer.getSFPlayer((Player) to)
											.getTeleport()
											.teleport(target.getPlayer(),
													TeleportCause.COMMAND);
								} catch (Exception e) {
									to.sendRawMessage("I could not teleport you to your friend because "
											+ e.getMessage());
									target.getPlayer()
											.sendMessage(
													((Player) to)
															.getDisplayName()
															+ " could not be teleported because "
															+ e.getMessage());
								}
							} else {
								to.sendRawMessage("Alright then, maybe next time!");
								target.getPlayer()
										.sendMessage(
												((Player) to).getDisplayName()
														+ " rejected your teleport request");
								SFPlayer.getSFPlayer((Player) to)
										.requestTeleport(null, false);
							}
							return Prompt.END_OF_CONVERSATION;
						}

						@Override
						public String getPromptText(ConversationContext context) {
							return "Can you teleport to "
									+ player.getDisplayName() + "? yes/no";
						}
					};
					Conversation convo = new Conversation(plugin, to, prompt);
					to.beginConversation(convo);
					return true;
				}
			}
		}
		return false;
	}
}