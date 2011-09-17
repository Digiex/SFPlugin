package net.digiex.simplefeatures;

import org.bukkit.entity.Player;

public class TeleportConfirmTask implements Runnable{
	private final Player fromPlayer;
	private final Player toPlayer;
	private String question;
	private final boolean tpahere;
	public TeleportConfirmTask(Player fromPlayer,Player toPlayer, boolean tpahere) {
		this.fromPlayer = fromPlayer;
		this.toPlayer = toPlayer;
		this.tpahere = tpahere;
		question = fromPlayer.getDisplayName()+" wants ";
		if(tpahere){
			question=question+"to teleport you to him/her.";
		}else{

			question=question+"to teleport to you.";
		}

		question=question+" Do you want to accept?";
	}

	@Override
	public void run() {
		if (SFPlugin.questioner.ask(fromPlayer, question, "yes", "no") == "yes") {
			if(this.tpahere){
				fromPlayer.teleport(toPlayer);
			}else{
				toPlayer.teleport(fromPlayer);
			}
			toPlayer.sendMessage("Teleport request accepted.");
		} else {
			toPlayer.sendMessage("Teleport request rejected.");
		}
	}
}