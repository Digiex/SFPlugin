package net.digiex.simplefeatures;

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
        return this.id = parent.getServer().getScheduler().scheduleAsyncDelayedTask(parent, task);
    }

    private String setQuestion() {
        if (getType().equals(TeleportTypes.tpa)) {
            return getFrom().getDisplayName() + " wants to teleport to you. Do you want to accept?";
        } else if (getType().equals(TeleportTypes.tpahere)) {
            return getFrom().getDisplayName() + " wants to teleport you to him/her. Do you want to accept?";
        }
        return "Uhh oh, something has went wrong, please shout at the feature commander.";
    }

    public enum TeleportTypes {

        tpa, tpahere, spawn, home, world, unknown
    }

    private class SFTeleportTask implements Runnable {

        @Override
        public void run() {
            question = setQuestion();
            if (getType().equals(TeleportTypes.tpa)) {
                if (SFPlugin.questioner.ask(getTo(), question, "yes", "no").equals("yes")) {
                    getFrom().sendMessage("Teleport request accepted");
                    getTo().sendMessage("Teleport request accepted");
                    startCountDown();
                    parent.teleporters.remove(getFrom());
                } else {
                    getFrom().sendMessage("Teleport request rejected");
                    getTo().sendMessage("Teleport request rejected");
                    parent.teleporters.remove(getFrom());
                }
            } else if (getType().equals(TeleportTypes.tpahere)) {
                if (SFPlugin.questioner.ask(getTo(), question, "yes", "no").equals("yes")) {
                    getFrom().sendMessage("Teleport request accepted");
                    getTo().sendMessage("Teleport request accepted");
                    startCountDown();
                    parent.teleporters.remove(getTo());
                } else {
                    getFrom().sendMessage("Teleport request rejected");
                    getTo().sendMessage("Teleport request rejected");
                    parent.teleporters.remove(getTo());
                }
            } else if (getType().equals(TeleportTypes.home)) {
                startCountDown();
                parent.teleporters.remove(getFrom());
            } else if (getType().equals(TeleportTypes.spawn)) {
                startCountDown();
                parent.teleporters.remove(getFrom());
            } else if (getType().equals(TeleportTypes.world)) {
                startCountDown();
                parent.teleporters.remove(getFrom());
            }
        }

        private void startCountDown() {
            if (getType().equals(TeleportTypes.tpa)) {
                if (getTimer()) {
                    counting = true;
                    try {
                        getFrom().sendMessage("You will be teleported to " + getTo().getDisplayName() + " in 30 seconds...");
                        Thread.sleep(10000);
                        getFrom().sendMessage("20...");
                        Thread.sleep(10000);
                        getFrom().sendMessage("10...");
                        Thread.sleep(5000);
                        getFrom().sendMessage("5...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("4...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("3...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("2...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("1...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("Poof!");
                        getFrom().teleport(getTo());
                    } catch (InterruptedException e) {
                        parent.teleporters.remove(getFrom());
                    }
                } else {
                    getFrom().teleport(getTo());
                    getFrom().sendMessage("Poof!");
                    parent.teleporters.remove(getFrom());
                }
            } else if (getType().equals(TeleportTypes.tpahere)) {
                if (getTimer()) {
                    counting = true;
                    try {
                        getTo().sendMessage("You will be teleported to " + getFrom().getDisplayName() + " in 30 seconds...");
                        Thread.sleep(10000);
                        getTo().sendMessage("20...");
                        Thread.sleep(10000);
                        getTo().sendMessage("10...");
                        Thread.sleep(5000);
                        getTo().sendMessage("5...");
                        Thread.sleep(1000);
                        getTo().sendMessage("4...");
                        Thread.sleep(1000);
                        getTo().sendMessage("3...");
                        Thread.sleep(1000);
                        getTo().sendMessage("2...");
                        Thread.sleep(1000);
                        getTo().sendMessage("1...");
                        Thread.sleep(1000);
                        getTo().sendMessage("Poof!");
                        getTo().teleport(getFrom());
                    } catch (InterruptedException e) {
                        parent.teleporters.remove(getTo());
                    }
                } else {
                    getTo().teleport(getFrom());
                    getTo().sendMessage("Poof!");
                    parent.teleporters.remove(getTo());
                }
            } else if (getType().equals(TeleportTypes.home)) {
                if (getTimer()) {
                    counting = true;
                    try {
                        getFrom().sendMessage("You will be teleported home in 30 seconds...");
                        Thread.sleep(10000);
                        getFrom().sendMessage("20...");
                        Thread.sleep(10000);
                        getFrom().sendMessage("10...");
                        Thread.sleep(5000);
                        getFrom().sendMessage("5...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("4...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("3...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("2...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("1...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("Poof!");
                        getFrom().teleport(getHome());
                    } catch (InterruptedException e) {
                        parent.teleporters.remove(getFrom());
                    }
                } else {
                    getFrom().teleport(getHome());
                    getFrom().sendMessage("Poof!");
                    parent.teleporters.remove(getFrom());
                }
            } else if (getType().equals(TeleportTypes.spawn)) {
                if (getTimer()) {
                    counting = true;
                    try {
                        getFrom().sendMessage("You will be teleported to spawn in 30 seconds...");
                        Thread.sleep(10000);
                        getFrom().sendMessage("20...");
                        Thread.sleep(10000);
                        getFrom().sendMessage("10...");
                        Thread.sleep(5000);
                        getFrom().sendMessage("5...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("4...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("3...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("2...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("1...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("Poof!");
                        getFrom().teleport(getFrom().getWorld().getSpawnLocation());
                    } catch (InterruptedException e) {
                        parent.teleporters.remove(getFrom());
                    }
                } else {
                    getFrom().teleport(getFrom().getWorld().getSpawnLocation());
                    getFrom().sendMessage("Poof!");
                    parent.teleporters.remove(getFrom());
                }
            } else if (getType().equals(TeleportTypes.world)) {
                if (getTimer()) {
                    counting = true;
                    try {
                        getFrom().sendMessage("You will be teleported to " + getWorld().getName() + " in 30 seconds...");
                        Thread.sleep(10000);
                        getFrom().sendMessage("20...");
                        Thread.sleep(10000);
                        getFrom().sendMessage("10...");
                        Thread.sleep(5000);
                        getFrom().sendMessage("5...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("4...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("3...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("2...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("1...");
                        Thread.sleep(1000);
                        getFrom().sendMessage("Poof!");
                        getFrom().teleport(getWorld().getSpawnLocation());
                    } catch (InterruptedException e) {
                        parent.teleporters.remove(getFrom());
                    }
                } else {
                    getFrom().teleport(getWorld().getSpawnLocation());
                    getFrom().sendMessage("Poof!");
                    parent.teleporters.remove(getFrom());
                }
            }
        }
    }
}
