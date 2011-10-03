package net.digiex.simplefeatures.listeners;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockCanBuildEvent.getMaterialId;


public class BListener extends BlockListener
{
	SFPlugin plugin;
	public BListener(SFPlugin parent){
		plugin = parent;
	}

	@Override
	public void onBlockBreak(final BlockBreakEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		Player p = event.getPlayer();
		if(!p.isOp()){
			boolean inspawn = SFPlugin.isInSpawnProtect(event.getBlock().getLocation());
			if(inspawn){
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED+"You are in a spawn protection area. Please walk further.");
			} else if(.getMaterialId() = 7) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED+"Removal of bedrock is not allowed.");
			}
		}
	}


	@Override
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		Player p = event.getPlayer();
		if(!p.isOp()){
			boolean inspawn = SFPlugin.isInSpawnProtect(event.getBlock().getLocation());
			if(inspawn){
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED+"You are in a spawn protection area. Please walk further.");
			}
		}
	}
}
