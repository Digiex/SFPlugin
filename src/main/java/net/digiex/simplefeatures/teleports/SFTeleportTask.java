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
			answer = SFPlugin.questioner.ask(askSubject, question, "yes", "no");
			if (answer == "yes") {
				askSubject.sendMessage("Teleport request accepted");
				if (what != askSubject) {
					what.sendMessage("Teleport request accepted");
				}
				if (who != askSubject) {
					who.sendMessage("Teleport request accepted");
				}
				startCountDown();
			} else {
				askSubject.sendMessage("Teleport request rejected");
				if (what != askSubject) {
					what.sendMessage("Teleport request rejected");
				}
				if (who != askSubject) {
					who.sendMessage("Teleport request rejected");
				}
			}
		} else {
			startCountDown();
		}

		teleporters.remove(what.getName());
	}

	private void startCountDown() {
		if (timer) {
			try {
				what.sendMessage(infoMsg + " in 30 seconds.");
				Thread.sleep(10000);
				what.sendMessage("20...");
				Thread.sleep(10000);
				what.sendMessage("10...");
				Thread.sleep(5000);
				what.sendMessage("5...");
				Thread.sleep(1000);
				what.sendMessage("4...");
				Thread.sleep(1000);
				what.sendMessage("3...");
				Thread.sleep(1000);
				what.sendMessage("2...");
				Thread.sleep(1000);
				what.sendMessage("1...");
				Thread.sleep(1000);
				what.sendMessage("Poof!");
			} catch (InterruptedException e) {
				what.sendMessage(infoMsg + ChatColor.RED + " aborted!");
				return;
			}
		} else {
			what.sendMessage(infoMsg);
		}
		what.teleport(where);
		return;
	}
}
