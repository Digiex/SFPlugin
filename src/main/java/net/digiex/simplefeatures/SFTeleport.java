package net.digiex.simplefeatures;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Location;

public class SFTeleport {

	private SFPlugin parent;
	private Player from;
	private Player to;
	private World world;
	private int id = -1;
	private Location home;
	private String question;
	private boolean counting;
	private boolean timer = true;
	private TeleportTypes type = TeleportTypes.unknown;

	public SFTeleport(SFPlugin parent, TeleportTypes type) {
		this.parent = parent;
		this.type = type;
	}

	public TeleportTypes getType() {
		return this.type;
	}

	public Player getFrom() {
		return this.from;
	}

	public void setFrom(Player from) {
		this.from = from;
	}

	public Player getTo() {
		return this.to;
	}

	public Location getHome() {
		return this.home;
	}

	public void setHome(Location home) {
		this.home = home;
	}

	public void setTo(Player to) {
		this.to = to;
	}

	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public int getId() {
		return this.id;
	}

	public String getQuestion() {
		return this.question;
	}

	public boolean getTimer() {
		return this.timer;
	}

	public void setTimer(boolean timer) {
		this.timer = timer;
	}

	public boolean getCounting() {
		return this.counting;
	}

	public int startTeleport() {
		SFTeleportTask task = new SFTeleportTask();
		return this.id = parent.getServer().getScheduler()
				.scheduleAsyncDelayedTask(parent, task);
	}

	private void setQuestion() {
		switch (getType()) {
		case tpa:
			question = getFrom().getDisplayName()
					+ " wants to teleport to you. Do you want to accept?";
			break;
		case tpahere:
			question = getFrom().getDisplayName()
					+ " wants to teleport you to him/her. Do you want to accept?";
			break;
		default:
			question = "Uhh oh, something has went wrong, please shout at the feature commander.";
		}
	}

	public enum TeleportTypes {

		tpa, tpahere, spawn, home, world, unknown
	}

	public enum TPAnswers {
		yes, no
	}

	public TPAnswers getAnswer(String ans) {
		if (ans == "yes")
			return TPAnswers.yes;

		return TPAnswers.no;
	}

	public boolean shouldIaskQuestions() {
		switch (getType()) {
		case tpa:
		case tpahere:
			return true;
		default:
			return false;
		}
	}

	public void removeFromTPList() {
		switch (getType()) {
		case tpa:
		case home:
		case spawn:
		case world:
			parent.teleporters.remove(getFrom());
			break;
		case tpahere:
			parent.teleporters.remove(getTo());
			break;
		}
	}

	private class SFTeleportTask implements Runnable {

		@Override
		public void run() {
			setQuestion();
			switch (getType()) {
			case tpa:
			case tpahere:
				TPAnswers answer = getAnswer(SFPlugin.questioner.ask(getTo(),
						question, "yes", "no"));
				switch (answer) {
				case yes:
					if (shouldIaskQuestions()) {
						getFrom().sendMessage("Teleport request accepted");
						getTo().sendMessage("Teleport request accepted");
					}
					startCountDown();
					removeFromTPList();
					break;
				case no:
					if (shouldIaskQuestions()) {
						getFrom().sendMessage("Teleport request rejected");
						getTo().sendMessage("Teleport request rejected");
					}
					removeFromTPList();
					break;
				}
				break;
			default:
				startCountDown();
				removeFromTPList();
			}
		}

		private void doTeleport() {
			switch (getType()) {
			case tpa:
				getFrom().teleport(getTo());
				break;
			case tpahere:
				getTo().teleport(getFrom());
			case home:
				getFrom().teleport(getHome());
				break;
			case spawn:
				getFrom().teleport(getFrom().getWorld().getSpawnLocation());
				break;
			case world:
				getFrom().teleport(getWorld().getSpawnLocation());
				break;
			}
		}

		private void startCountDown() {
			Player fromP = getFrom();
			String infoMsg = "";
			switch (getType()) {
			case tpa:
				infoMsg = "You will be teleported to " + to.getDisplayName()
						+ " in 30 seconds...";
				break;
			case tpahere:
				fromP = getTo();
				infoMsg = "You will be teleported to " + from.getDisplayName()
						+ " in 30 seconds...";
				break;
			case home:
				infoMsg = "You will be teleported home in 30 seconds...";
				break;
			case spawn:
				infoMsg = "You will be teleported to spawn in 30 seconds...";
				break;
			case world:
				infoMsg = "You will be teleported to " + getWorld().getName()
						+ " in 30 seconds...";
				break;
			}

			if (getTimer()) {
				counting = true;
				try {
					fromP.sendMessage(infoMsg);

					Thread.sleep(10000);
					fromP.sendMessage("20...");
					Thread.sleep(10000);
					fromP.sendMessage("10...");
					Thread.sleep(5000);
					fromP.sendMessage("5...");
					Thread.sleep(1000);
					fromP.sendMessage("4...");
					Thread.sleep(1000);
					fromP.sendMessage("3...");
					Thread.sleep(1000);
					fromP.sendMessage("2...");
					Thread.sleep(1000);
					fromP.sendMessage("1...");
					Thread.sleep(1000);
					fromP.sendMessage("Poof!");
					doTeleport();
					removeFromTPList();
				} catch (InterruptedException e) {
					removeFromTPList();
				}
			} else {
				doTeleport();
				fromP.sendMessage("Poof!");
				removeFromTPList();
			}
			SFPlugin.log(Level.INFO, parent.teleporters.toString());
			return;
		}
	}
}
