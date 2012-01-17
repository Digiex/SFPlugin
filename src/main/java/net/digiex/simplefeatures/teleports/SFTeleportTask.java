package net.digiex.simplefeatures.teleports;

import java.util.HashMap;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTranslation;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class SFTeleportTask implements Runnable {
	private final Player who;
	// wants
	private final Player what;
	// to
	private final Location where;
	private final Player askSubject;
	private final String whoLang;
	private final String whatLang;
	private final String askSubjectLang;
	private final SFTranslation t = SFTranslation.getInstance();
	private final boolean ask;
	private final String question;
	private final String infoMsg;
	private boolean timer;
	public static HashMap<String, Integer> teleporters = new HashMap<String, Integer>();

	public SFTeleportTask(Player who, Player what, Player askSubject,
			Location where, boolean ask, String question, String infoMsg) {
		this.who = who;
		this.what = what;
		this.where = where;
		this.askSubject = askSubject;
		this.ask = ask;
		this.question = question;
		this.infoMsg = infoMsg;
		if (what.getGameMode().equals(GameMode.CREATIVE)
				|| what.hasPermission(new Permission("sf.tpoverride",
						PermissionDefault.OP))) {
			timer = false;
		} else {
			timer = true;
		}
		if (who != null) {
			whoLang = new SFPlayer(who).getLanguage();
		} else {
			whoLang = "en_US";
		}
		if (what != null) {
			whatLang = new SFPlayer(what).getLanguage();
		} else {
			whatLang = "en_US";
		}
		if (askSubject != null) {
			askSubjectLang = new SFPlayer(askSubject).getLanguage();
		} else {
			askSubjectLang = "en_US";
		}
	}

	@Override
	public void run() {
		String answer;
		if (ask) {
			teleporters.put(askSubject.getName(),
					teleporters.get(what.getName()));
			answer = SFPlugin.questioner.ask(askSubject, question, "yes", "no");
			if (answer == "yes") {
				sendLocMsgto3("teleport.accepted");
				startCountDown();
			} else {
				sendLocMsgto3("teleport.rejected");
			}
			teleporters.remove(askSubject.getName());
		} else {
			startCountDown();
		}

		teleporters.remove(what.getName());
	}

	public void sendLocMsgto3(String node) {
		what.sendMessage(t.translateKey(node, whatLang));
		if (what != askSubject && askSubject != null) {
			askSubject.sendMessage(t.translateKey(node, askSubjectLang));
		}
		if (who != askSubject && who != what && who != null) {
			who.sendMessage(t.translateKey(node, whoLang));
		}
	}

	private void startCountDown() {
		if (timer) {
			try {
				what.sendMessage(infoMsg + " "
						+ t.translateKeyFormat("teleport.intime", whatLang, 30));
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage(t.translateKeyFormat(
							"teleport.counterstart", askSubjectLang, 30));
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage(t.translateKeyFormat(
							"teleport.counterstart", whoLang, 30));
				}
				Thread.sleep(10000);
				what.sendMessage("20...");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage("20...");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("20...");
				}
				Thread.sleep(10000);
				what.sendMessage("10...");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage("10...");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("10...");
				}
				Thread.sleep(5000);
				what.sendMessage("5...");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage("5...");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("5...");
				}
				Thread.sleep(1000);
				what.sendMessage("4...");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage("4...");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("4...");
				}
				Thread.sleep(1000);
				what.sendMessage("3...");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage("3...");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("3...");
				}
				Thread.sleep(1000);
				what.sendMessage("2...");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage("2...");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("2...");
				}
				Thread.sleep(1000);
				what.sendMessage("1...");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage("1...");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("1...");
				}
				Thread.sleep(1000);
				sendLocMsgto3("teleport.poof");
			} catch (InterruptedException e) {
				sendLocMsgto3("teleport.aborted");
				return;
			}
		} else {
			what.sendMessage(infoMsg);
		}
		what.teleport(where);
		return;
	}
}
