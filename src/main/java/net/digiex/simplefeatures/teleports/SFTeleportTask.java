package net.digiex.simplefeatures.teleports;

import java.util.HashMap;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
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
	private final boolean ask;
	private final String question;
	private final String infoMsg;
	private boolean timer;
	private final Player askSubject;
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
	}

	@Override
	public void run() {
		String answer;
		if (ask) {
			teleporters.put(askSubject.getName(),
					teleporters.get(what.getName()));
			answer = SFPlugin.questioner.ask(askSubject, question, "yes", "no");
			if (answer == "yes") {

				askSubject.sendMessage("Teleport request accepted");
				if (what != askSubject && what != null) {
					what.sendMessage("Teleport request accepted");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("Teleport request accepted");
				}
				startCountDown();
			} else {
				askSubject.sendMessage("Teleport request rejected");
				if (what != askSubject && what != null) {
					what.sendMessage("Teleport request rejected");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("Teleport request rejected");
				}
			}
			teleporters.remove(askSubject.getName());
		} else {
			startCountDown();
		}

		teleporters.remove(what.getName());
	}

	private void startCountDown() {
		if (timer) {
			try {
				what.sendMessage(infoMsg + " in 30 seconds.");
				if (what != askSubject && askSubject != null) {
					askSubject
							.sendMessage("Teleport will happen in 30 seconds.");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("Teleport will happen in 30 seconds.");
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
				what.sendMessage("Poof!");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage("Poof!");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage("Poof!");
				}
			} catch (InterruptedException e) {
				what.sendMessage(infoMsg + ChatColor.RED + " aborted!");
				if (what != askSubject && askSubject != null) {
					askSubject.sendMessage(ChatColor.RED
							+ "Teleportation aborted!");
				}
				if (who != askSubject && who != what && who != null) {
					who.sendMessage(ChatColor.RED + "Teleportation aborted!");
				}
				return;
			}
		} else {
			what.sendMessage(infoMsg);
		}
		what.teleport(where);
		return;
	}
}
