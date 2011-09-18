package net.digiex.simplefeatures.listeners;

import java.util.logging.Level;

import net.digiex.simplefeatures.SFPlayer;
import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitScheduler;


public class PListener extends PlayerListener{
	SFPlugin plugin;
	public BukkitScheduler tasks;
	public PListener(SFPlugin parent){
		plugin = parent;
	}



	@Override
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}

		if (event.getClickedBlock().getType() == Material.BED_BLOCK)
		{
			SFPlayer sfplayer = new SFPlayer(event.getPlayer());
			sfplayer.setHomeLocation();
			event.getPlayer().sendMessage(ChatColor.YELLOW+"Your home for this world is now set to this bed!");
		}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent e){
		setGameMode(e.getPlayer(),e.getPlayer().getWorld());
                if(e.getPlayer().isOp()){
                    e.getPlayer().setDisplayName(ChatColor.AQUA+e.getPlayer().getName()+ChatColor.WHITE);
                }else{
                    e.getPlayer().setDisplayName(ChatColor.GREEN+e.getPlayer().getName()+ChatColor.WHITE);
                }
	}
	@Override
	public void onPlayerPortal(PlayerPortalEvent e) {
		if (!(e.isCancelled()) && e.getTo() != null) {
			Teleported(e.getFrom().getWorld(),e.getTo().getWorld(),e.getPlayer());
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation((new SFPlayer(event.getPlayer())).getHomeLocation());
	}
	@Override
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (!(e.isCancelled()) && e.getTo() != null) {
			Teleported(e.getFrom().getWorld(),e.getTo().getWorld(),e.getPlayer());
		}
	}
	public void setGameMode(Player player, World world){
		int gamemode = plugin.config.getInt("worlds."+world.getName()+".gamemode",5);
		if(gamemode == 1){
			if(player.getGameMode() != GameMode.CREATIVE) {
				player.setGameMode(GameMode.CREATIVE);
				SFPlugin.log(Level.INFO, "Gamemode set to creative for "+player.getName());
			}
		}else if(gamemode == 0){
			if(player.getGameMode() != GameMode.SURVIVAL) {
				player.setGameMode(org.bukkit.GameMode.SURVIVAL);

				SFPlugin.log(Level.INFO, "Gamemode set to survival for "+player.getName());
			}
		}else{
			if(player.getGameMode() != plugin.getServer().getDefaultGameMode()) {
				player.setGameMode(plugin.getServer().getDefaultGameMode());
				SFPlugin.log(Level.INFO, "Gamemode set to default ("+plugin.getServer().getDefaultGameMode().toString()+") for "+player.getName());
			}
		}
	}
	public void Teleported(World from, World to, Player player){
		if(from != to){
			setGameMode(player,to);

			SFPlugin.log(Level.INFO,player.getName()+" teleported from "+from.getName()+" to "+to.getName());
		}
	}

}
