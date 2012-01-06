package net.digiex.simplefeatures.questioner;

import java.util.Vector;

import org.bukkit.entity.Player;

public class SFQuestioner {
	public final Vector<Question> questions = new Vector<Question>();

	public String ask(Player respondent, String questionMessage,
			String... answers) {
		final Question question = new Question(respondent, questionMessage,
				answers);
		questions.add(question);
		return question.ask();
	}
}
