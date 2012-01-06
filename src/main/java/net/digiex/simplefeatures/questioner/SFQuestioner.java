package net.digiex.simplefeatures.questioner;

import java.util.Vector;

import org.bukkit.entity.Player;

public class SFQuestioner {
	public final Vector<Question> questions = new Vector<Question>();

	public String ask(Player respondent, String questionMessage,
			String answer1, String answer2) {
		final Question question = new Question(respondent, questionMessage,
				answer1, answer2);
		questions.add(question);
		return question.ask();
	}
}
