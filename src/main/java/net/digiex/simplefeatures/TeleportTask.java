package net.digiex.simplefeatures;

import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Location;

public class TeleportTask implements Runnable {

    private SFPlugin plugin;
    private String question;
    private int id;
    
    private World world;
    private Location location;
    private Player fromPlayer, toPlayer;
    private boolean tpa, tpahere, tph, tpw, coolingdown = false;

    public TeleportTask(SFPlugin plugin, Player fromPlayer, Player toPlayer, World world, Location location, boolean tpa, boolean tpahere, boolean tph, boolean tpw) {
        this.plugin = plugin;
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.tpahere = tpahere;
        this.world = world;
        this.location = location;
        this.tpa = tpa;
        this.tpahere = tpahere;
        this.tph = tph;
        this.tpw = tpw;
    }

    @Override
    public void run() {
        question = setQuestion();
        if (tpa) {
            if (SFPlugin.questioner.ask(toPlayer, question, "yes", "no").equals("yes")) {
                fromPlayer.sendMessage("Teleport request accepted");
                toPlayer.sendMessage("Teleport request accepted");
                startCountDown(fromPlayer, toPlayer);
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    // sharks
                }
                plugin.teleporters.remove(fromPlayer.getName());
            } else {
                fromPlayer.sendMessage("Teleport request rejected");
                toPlayer.sendMessage("Teleport request rejected");
            }
        } else if (tpahere) {
            if (SFPlugin.questioner.ask(toPlayer, question, "yes", "no").equals("yes")) {
                fromPlayer.sendMessage("Teleport request accepted");
                toPlayer.sendMessage("Teleport request accepted");
                startCountDown(toPlayer, fromPlayer);
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    // sharks
                }
                plugin.teleporters.remove(toPlayer.getName());
            } else {
                fromPlayer.sendMessage("Teleport request rejected");
                toPlayer.sendMessage("Teleport request rejected");
            }
        } else if (tph) {
            if (SFPlugin.questioner.ask(fromPlayer, question, "yes", "no").equals("yes")) {
                fromPlayer.sendMessage("Teleport accepted");
                startHomeCountDown(fromPlayer, location);
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    // sharks
                }
                plugin.teleporters.remove(fromPlayer.getName());
            } else {
                fromPlayer.sendMessage("Teleport rejected");
            }
        } else if (tpw) {
            if (SFPlugin.questioner.ask(fromPlayer, question, "yes", "no").equals("yes")) {
                fromPlayer.sendMessage("Teleport accepted");
                startWorldCountDown(fromPlayer, world);
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    // sharks
                }
                plugin.teleporters.remove(fromPlayer.getName());
            } else {
                fromPlayer.sendMessage("Teleport rejected");
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
            plugin.teleporters.remove(f.getName());
            // warn server of potential sharks... ;)
        }
    }
    
    private void startHomeCountDown(Player p, Location l) {
        try {
            p.sendMessage("You will be teleported home in 30 seconds...");
            Thread.sleep(10000);
            p.sendMessage("20...");
            Thread.sleep(10000);
            p.sendMessage("10...");
            Thread.sleep(5000);
            p.sendMessage("5...");
            Thread.sleep(1000);
            p.sendMessage("4...");
            Thread.sleep(1000);
            p.sendMessage("3...");
            Thread.sleep(1000);
            p.sendMessage("2...");
            Thread.sleep(1000);
            p.sendMessage("1...");
            Thread.sleep(1000);
            p.sendMessage("Poof!");
            p.teleport(l);
        } catch (InterruptedException e) {
            plugin.teleporters.remove(p.getName());
            // activates features ;)
        }
    }
    
    private void startWorldCountDown(Player p, World w) {
        try {
            p.sendMessage("You will be teleported to " + w.getName().replace("_", " ") + " in 30 seconds...");
            Thread.sleep(10000);
            p.sendMessage("20...");
            Thread.sleep(10000);
            p.sendMessage("10...");
            Thread.sleep(5000);
            p.sendMessage("5...");
            Thread.sleep(1000);
            p.sendMessage("4...");
            Thread.sleep(1000);
            p.sendMessage("3...");
            Thread.sleep(1000);
            p.sendMessage("2...");
            Thread.sleep(1000);
            p.sendMessage("1...");
            Thread.sleep(1000);
            p.sendMessage("Poof!");
            p.teleport(w.getSpawnLocation());
        } catch (InterruptedException e) {
            plugin.teleporters.remove(p.getName());
            // shouts im a shark ;)
        }
    }

    private String setQuestion() {
        if (tpa) {
            return fromPlayer.getDisplayName() + " wants to teleport to you. Do you want to accept?";
        } else if (tpahere) {
            return fromPlayer.getDisplayName() + " wants to teleport you to him/her. Do you want to accept?";
        } else if (tph) {
            return "Are you sure you want to teleport home?";
        } else if (tpw) {
            return "Are you sure you want to teleport to " + world.getName() + "?";
        }
        return "Uhh oh, something has went wrong, please shout at the feature commander.";
    }

    public Player getFrom() {
        return this.fromPlayer;
    }

    public Player getTo() {
        return this.toPlayer;
    }

    public boolean isCooling() {
        return this.coolingdown;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public void setLocation(Location l) {
        this.location = l;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public void setWorld(World w) {
        this.world = w;
    }
}