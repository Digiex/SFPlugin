package net.digiex.simplefeatures.questioner;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.entity.Player;

class Question {
	private String answer = null;
	private String answer1 = null;
	private String answer2 = null;
	private final String questionMessage;
	private final Player respondent;
	private final int respondentHash;
	private final long start;

	Question(Player respondent, String questionMessage, String answer1,
			String answer2) {
		start = System.currentTimeMillis();
		this.respondent = respondent;
		respondentHash = respondent.getName().hashCode();
		this.questionMessage = questionMessage;
		this.answer1 = answer1.toLowerCase();
		this.answer2 = answer2.toLowerCase();
	}

	synchronized String ask() {
		if (SFPlugin.clientAddons.contains(respondent.getName())) {
			respondent.sendRawMessage("§1§1§2/askgui");
			respondent.sendRawMessage("§1§1§2/msg1=" + questionMessage);
			respondent
					.sendRawMessage("§1§1§2/msg2=Please click one of the buttons below.");
			respondent.sendRawMessage("§1§1§2/btn1=" + answer1.toUpperCase());
			respondent.sendRawMessage("§1§1§2/btn1cmd=" + answer1);
			respondent.sendRawMessage("§1§1§2/btn2=" + answer2.toUpperCase());
			respondent.sendRawMessage("§1§1§2/btn2cmd=" + answer2);
		}
		final StringBuilder options = new StringBuilder();
		options.append("/" + answer1 + ", ");
		options.append("/" + answer2 + ", ");
		options.delete(options.length() - 2, options.length());
		respondent.sendMessage(questionMessage);
		respondent.sendMessage("- " + options + "?");
		try {
			this.wait();
		} catch (final InterruptedException ex) {
			answer = "interrupted";
		}
		return answer;
	}

	synchronized boolean isExpired() {
		if (System.currentTimeMillis() - start > 60000) {
			answer = "timed out";
			notify();
			return true;
		}
		return false;
	}

	boolean isPlayerQuestioned(int playerNameHash) {
		return playerNameHash == respondentHash;
	}

	boolean isRightAnswer(String answer) {
		return (answer.equalsIgnoreCase(answer1) || answer
				.equalsIgnoreCase(answer));
	}

	synchronized void returnAnswer(String answer) {
		if (answer.equalsIgnoreCase(answer1)) {
			this.answer = answer1;
		} else if (answer.equalsIgnoreCase(answer2)) {
			this.answer = answer2;
		}
		notify();
	}
}
