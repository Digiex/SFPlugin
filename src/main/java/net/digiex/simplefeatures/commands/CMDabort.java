package net.digiex.simplefeatures.commands;

/**
 * @author xzKinGzxBuRnzx
 */

import net.digiex.simplefeatures.SFPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CMDabort implements CommandExecutor {
    
    private SFPlugin parent;
    
    public CMDabort(SFPlugin parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
    	cs.sendMessage("Sorry, aborting is not implemented yet :(");
    	return true;
    }
}