package net.digiex.simplefeatures.questioner;

import java.util.Vector;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SFQuestionerPlayerListener implements Listener {
	private final Vector<Question> questions;
	private final SFPlugin plugin;

	public SFQuestionerPlayerListener(Vector<Question> questions,
			SFPlugin parent) {
		this.questions = questions;
		plugin = parent;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled() && !questions.isEmpty()) {
			final int playerHash = event.getPlayer().getName().hashCode();
			for (final Question question : questions) {
				if (question.isPlayerQuestioned(playerHash)
						&& question.isRightAnswer(event.getMessage()
								.substring(1).toLowerCase())) {
					question.returnAnswer(event.getMessage().substring(1)
							.toLowerCase());
					questions.remove(question);
					event.setCancelled(true);
					break;
				}
			}
		}
	}
}
