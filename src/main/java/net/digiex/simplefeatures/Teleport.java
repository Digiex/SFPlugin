package net.digiex.simplefeatures;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Teleport implements Runnable {
	private static class Target {
		private final Location location;
		private final Entity entity;

		public Target(Entity entity) {
			this.entity = entity;
			this.location = null;
		}

		public Target(Location location) {
			this.location = location;
			this.entity = null;
		}

		public Location getLocation() {
			if (this.entity != null) {
				return this.entity.getLocation();
			}
			return location;
		}
	}

	private static final double MOVE_CONSTANT = 0.3;
	private final Player user;
	private int teleTimer = -1;
	private long started; // time this task was initiated
	private long delay; // how long to delay the teleport
	private int health;
	// note that I initially stored a clone of the location for reference,
	// but...
	// when comparing locations, I got incorrect mismatches (rounding errors,
	// looked like)
	// so, the X/Y/Z values are stored instead and rounded off
	private long initX;
	private long initY;
	private long initZ;
	private Target teleportTarget;
	private TeleportCause cause;

	public Teleport(Player user) {
		this.user = user;
	}

	public void cancel() {
		cancel(false);
	}

	public void cancel(boolean notifyUser) {
		if (teleTimer == -1) {
			return;
		}
		try {
			Bukkit.getScheduler().cancelTask(teleTimer);
			if (notifyUser) {
				user.sendMessage("Teleport in progress cancelled");
			}
		} finally {
			teleTimer = -1;
		}
	}

	public void cooldown(boolean check) throws Exception {
		Calendar now = new GregorianCalendar();
		if (SFPlayer.getSFPlayer(user).getLastTeleportTimestamp() > 0) {
			double cooldown = 60;
			Calendar cooldownTime = new GregorianCalendar();
			cooldownTime.setTimeInMillis(SFPlayer.getSFPlayer(user)
					.getLastTeleportTimestamp());
			cooldownTime.add(Calendar.SECOND, (int) cooldown);
			cooldownTime.add(Calendar.MILLISECOND,
					(int) ((cooldown * 1000.0) % 1000.0));
			if (cooldownTime.after(now) && !user.isOp()) {
				throw new Exception("You need to wait " + cooldown
						+ " seconds!");
			}
		}
		// if justCheck is set, don't update lastTeleport; we're just checking
		if (!check) {
			SFPlayer.getSFPlayer(user).setLastTeleportTimestamp(
					now.getTimeInMillis());
		}
	}

	public void home(Player user, String home) throws Exception {
		teleport(SFPlayer.getSFPlayer(user).getHomeLoc(user.getWorld()),
				TeleportCause.COMMAND);
	}

	private void initTimer(long delay, Target target, TeleportCause cause) {
		this.started = System.currentTimeMillis();
		this.delay = delay;
		this.health = user.getHealth();
		this.initX = Math.round(user.getLocation().getX() * MOVE_CONSTANT);
		this.initY = Math.round(user.getLocation().getY() * MOVE_CONSTANT);
		this.initZ = Math.round(user.getLocation().getZ() * MOVE_CONSTANT);
		this.teleportTarget = target;
		this.cause = cause;
	}

	public void now(Entity entity, boolean cooldown, TeleportCause cause)
			throws Exception {
		if (cooldown) {
			cooldown(false);
		}
		now(new Target(entity), cause);
	}

	public void now(Location loc, boolean cooldown, TeleportCause cause)
			throws Exception {
		if (cooldown) {
			cooldown(false);
		}
		now(new Target(loc), cause);
	}

	public void now(Location loc, TeleportCause cause) throws Exception {
		cooldown(false);
		now(new Target(loc), cause);
	}

	private void now(Target target, TeleportCause cause) throws Exception {
		cancel();
		user.teleport(Util.getSafeDestination(target.getLocation()), cause);
	}

	@Override
	public void run() {

		if (user == null || !user.isOnline() || user.getLocation() == null) {
			cancel();
			return;
		}
		if (Math.round(user.getLocation().getX() * MOVE_CONSTANT) != initX
				|| Math.round(user.getLocation().getY() * MOVE_CONSTANT) != initY
				|| Math.round(user.getLocation().getZ() * MOVE_CONSTANT) != initZ
				|| user.getHealth() < health) { // user moved, cancel teleport
			cancel(true);
			return;
		}

		health = user.getHealth(); // in case user healed, then later gets
									// injured

		long now = System.currentTimeMillis();
		if (now > started + delay) {
			try {
				cooldown(false);
				user.sendMessage("Teleport incoming");
				try {

					now(teleportTarget, cause);
				} catch (Throwable ex) {
					ex.printStackTrace();
					user.sendMessage(ChatColor.RED + "Could not teleport :/");
				}
			} catch (Exception ex) {
				user.sendMessage(ChatColor.RED + "Could not teleport: "
						+ ex.getMessage());
			}
		}
	}

	public void teleport(Entity entity, TeleportCause cause) throws Exception {
		teleport(new Target(entity), cause);
	}

	public void teleport(Location loc) throws Exception {
		teleport(new Target(loc), TeleportCause.PLUGIN);
	}

	public void teleport(Location loc, TeleportCause cause) throws Exception {
		teleport(new Target(loc), cause);
	}

	private void teleport(Target target, TeleportCause cause) throws Exception {
		double delay = 30;
		cooldown(true);
		if (delay <= 0 || user.isOp()
				|| user.getGameMode() == GameMode.CREATIVE) {
			cooldown(false);
			now(target, cause);
			return;
		}

		cancel();
		Calendar c = new GregorianCalendar();
		c.add(Calendar.SECOND, (int) delay);
		c.add(Calendar.MILLISECOND, (int) ((delay * 1000.0) % 1000.0));
		user.sendMessage("You need to wait " + c.getTimeInMillis() / 1000
				+ " seconds");
		initTimer((long) (delay * 1000.0), target, cause);

		teleTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(
				Bukkit.getPluginManager().getPlugin("SimpleFeatures"), this,
				10, 10);
	}
}