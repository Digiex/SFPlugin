package net.digiex.simplefeatures;

import org.bukkit.entity.Player;

public class TeleportConfirmTask implements Runnable {
    private final Player fromPlayer;
    private final Player toPlayer;
    private String question;
    private final boolean tpahere;
    private SFPlugin plugin;
    private boolean coolingDown = false;
    private int id;

    public TeleportConfirmTask(Player fromPlayer, Player toPlayer, boolean tpahere, SFPlugin plugin) {
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.tpahere = tpahere;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        boolean accepted = false;
        this.question = setQuestion();
        if (SFPlugin.questioner.ask(toPlayer, question, "yes", "no").equals("yes")) {
            accepted = true;
            fromPlayer.sendMessage("Teleport request accepted");
            toPlayer.sendMessage("Teleport request accepted");
            if (tpahere) {
                startCountDown(toPlayer, fromPlayer);
            } else {
                startCountDown(fromPlayer, toPlayer);
            }
        } else {
            fromPlayer.sendMessage("Teleport request rejected");
            toPlayer.sendMessage("Teleport request rejected");
        }
        if (accepted) {
            try {
                coolingDown = true;
                Thread.sleep(300000);
                if (tpahere) {
                    plugin.teleporters.remove(toPlayer.getName());
                } else {
                    plugin.teleporters.remove(fromPlayer.getName());
                }
                plugin.getServer().getScheduler().cancelTask(id);
            } catch (InterruptedException e) {
            }
        }
    }

    private void startCountDown(Player f, Player t) {
        try {
            f.sendMessage("You will be teleported to " + t.getDisplayName() + " in 30 seconds...");
            Thread.sleep(10000);
            f.sendMessage("20...");
            Thread.sleep(10000);
            f.sendMessage("10...");
            Thread.sleep(5000);
            f.sendMessage("5...");
            Thread.sleep(1000);
            f.sendMessage("4...");
            Thread.sleep(1000);
            f.sendMessage("3...");
            Thread.sleep(1000);
            f.sendMessage("2...");
            Thread.sleep(1000);
            f.sendMessage("1...");
            Thread.sleep(1000);
            f.sendMessage("Poof!");
            f.teleport(t);
        } catch (InterruptedException e) {
        }
    }

    private String setQuestion() {
        if (tpahere) {
            return fromPlayer.getDisplayName() + " wants to teleport you to him/her. Do you want to accept?";
        }
        return fromPlayer.getDisplayName() + " wants to teleport to you. Do you want to accept?";
    }

    public Player getFrom() {
        return this.fromPlayer;
    }

    public Player getTo() {
        return this.toPlayer;
    }

    public boolean isCooling() {
        return this.coolingDown;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}