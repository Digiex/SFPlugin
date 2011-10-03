package net.digiex.simplefeatures.commands;

/**
 * @author xzKinGzxBuRnzx
 */

import net.digiex.simplefeatures.SFPlugin;
import net.digiex.simplefeatures.SFTeleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class CMDabort implements CommandExecutor {
    
    private SFPlugin parent;
    
    public CMDabort(SFPlugin parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            if (parent.teleporters.containsKey(player)) {
                SFTeleport teleport = parent.teleporters.get(player);
                int id = teleport.getId();
                /*if (strings.length == 0) {
                    if (strings[0].equals("-f")) {
                        parent.getServer().getScheduler().cancelTask(id);
                 *  This is for testing purposes only.
                        return true;
                    }
                }*/
                if (teleport.getCounting()) {
                    parent.getServer().getScheduler().cancelTask(id);
                    player.sendMessage(ChatColor.GRAY + "Teleport aborted!");
                } else {
                    player.sendMessage(ChatColor.GRAY + "Nothing to abort.");
                }
                return true;
            }
        }
        return false;
    }
}