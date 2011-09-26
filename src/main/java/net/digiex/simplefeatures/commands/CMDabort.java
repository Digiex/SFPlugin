package net.digiex.simplefeatures.commands;

/**
 * @author xzKinGzxBuRnzx
 */

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.TeleportTask;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDabort implements CommandExecutor {
    
    private SFPlugin parent;
    
    public CMDabort(SFPlugin parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            if (parent.teleporters.containsKey(player.getName())) {
                TeleportTask task = parent.teleporters.get(player.getName());
                int id = task.getId();
                if (task.isCooling()) {
                    player.sendMessage("Nice try, but your cooling down from the last teleport. ;P");
                    return false;
                }
                parent.getServer().getScheduler().cancelTask(id);
                player.sendMessage("Teleport aborted! Error: Feature Power Failure");
                return true;
            }
        }
        return false;
    }
}