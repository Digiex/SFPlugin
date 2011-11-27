package net.digiex.simplefeatures.commands;

/**
 * @author xzKinGzxBuRnzx
 */

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.teleports.SFTeleportTask;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitWorker;

public class CMDabort implements CommandExecutor {

	private final SFPlugin parent;

	public CMDabort(SFPlugin parent) {
		this.parent = parent;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string,
			String[] strings) {
		boolean found = false;
		for (BukkitWorker worker : parent.getServer().getScheduler()
				.getActiveWorkers()) {
			if (worker.getOwner() instanceof SFPlugin) {
				if (SFTeleportTask.teleporters.get(cs.getName()).equals(
						worker.getTaskId())) {
					found = true;
					parent.getServer().getScheduler()
							.cancelTask(worker.getTaskId());
				}
			}
		}
		if (!found) {
			cs.sendMessage("Nothing to cancel!");
		}
		return true;
	}
}