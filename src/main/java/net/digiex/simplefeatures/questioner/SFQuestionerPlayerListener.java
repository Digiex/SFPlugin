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
			final int answerHash = event.getMessage().substring(1)
					.toLowerCase().hashCode();
			for (final Question question : questions) {
				if (question.isPlayerQuestioned(playerHash)
						&& question.isRightAnswer(answerHash)) {
					question.returnAnswer(answerHash);
					questions.remove(question);
					event.setCancelled(true);
					break;
				}
			}
		}
	}
}
