package net.digiex.simplefeatures.teleports;

import java.util.Set;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class SFTeleportTask implements Runnable{
	private Player who;
	// wants
	private Player what;
	// to
	private Location where;
	private boolean ask;
	private String question;
	private String infoMsg;
	private boolean timer;
	private Player askSubject;
	public static Set<String> teleporters;
	public SFTeleportTask(Player who, Player what, Player askSubject, Location where,boolean ask, String question, String infoMsg){
		this.who = who;
		this.what = what;
		this.where = where;
		this.askSubject = askSubject;
		this.ask=ask;
		this.question = question;
		this.infoMsg = infoMsg;
        if (what.getGameMode().equals(GameMode.CREATIVE) || what.hasPermission(new Permission("sf.tpoverride", PermissionDefault.OP))) {
            this.timer = false;
        }else{
        	this.timer = true;
        }
        teleporters.add(what.getName());
	}
	public Player getWhat(){
		return what;
	}
	@Override
	public void run() {
		String answer;
		if (ask) {
			answer = SFPlugin.questioner.ask(askSubject, question, "yes", "no");
			if (answer == "yes") {
				who.sendMessage("Teleport request accepted");
				what.sendMessage("Teleport request accepted");
				startCountDown();
			} else {
				what.sendMessage("Teleport request rejected");
				who.sendMessage("Teleport request rejected");
			}
		} else {
			startCountDown();
		}

		teleporters.remove(what.getName());
	}

	private void startCountDown() {
		if (timer) {
			try {
				what.sendMessage(infoMsg);
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
			} catch (InterruptedException e) {
				what.sendMessage("Teleportation aborted!");
				return;
			}
		}
		what.sendMessage("Poof!");
		what.teleport(where);
		return;
	}

}
