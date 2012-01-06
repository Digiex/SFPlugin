package net.digiex.simplefeatures.questioner;

import java.util.Vector;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class SFQuestionerPlayerListener extends PlayerListener {
	private final Vector<Question> questions;

	public SFQuestionerPlayerListener(Vector<Question> questions) {
		this.questions = questions;
	}

	@Override
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
