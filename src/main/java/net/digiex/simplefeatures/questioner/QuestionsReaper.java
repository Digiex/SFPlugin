package net.digiex.simplefeatures.questioner;

import java.util.Enumeration;
import java.util.Vector;

@Deprecated
public class QuestionsReaper implements Runnable {
	private final Vector<Question> questions;

	public QuestionsReaper(Vector<Question> questions) {
		this.questions = questions;
	}

	@Override
	public void run() {
		final Enumeration<Question> enm = questions.elements();
		while (enm.hasMoreElements()) {
			final Question question = enm.nextElement();
			if (question.isExpired()) {
				questions.remove(question);
			}
		}
	}
}
