package net.digiex.simplefeatures;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class SFPlayer{
	public Player player;
	private final Configuration pdata;
	public SFPlayer(Player who){
		player = who;
		pdata = SFPlugin.playerconfig;
	}
	public Location getHomeLocation(){
		return getHomeLocation(player.getWorld());
	}
	public Location getHomeLocation(World world){
		if(pdata.getNode(player.getName()+".home") != null){
			return new Location(world, pdata.getDouble(player.getName()+".home."+world.getName()+".x", 0), pdata.getDouble(player.getName()+".home."+world.getName()+".y", 0), pdata.getDouble(player.getName()+".home."+world.getName()+".z", 0));
		}else{
			return getSpawn(world);
		}
	}
	public Location getSpawn(World world){
		return world.getSpawnLocation();
	}
	public void setHomeLocation(){
		setHomeLocation(player.getLocation());
	}
	public void setHomeLocation(Location loc){
		pdata.setProperty(player.getName()+".home."+loc.getWorld().getName()+".x", loc.getX());
		pdata.setProperty(player.getName()+".home."+loc.getWorld().getName()+".y", loc.getY());
		pdata.setProperty(player.getName()+".home."+loc.getWorld().getName()+".z", loc.getZ());
		pdata.setProperty(player.getName()+".home."+loc.getWorld().getName()+".yaw", loc.getYaw());
		pdata.setProperty(player.getName()+".home."+loc.getWorld().getName()+".pitch", loc.getPitch());
		pdata.save();
	}
	public void setSpawn(){
		setSpawn(player.getLocation());
	}
	public void setSpawn(Location loc){
		loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	public void teleportToHome(){
		player.teleport(getHomeLocation());
	}
	public void teleportToSpawn(){
		player.teleport(getSpawn(player.getWorld()));
	}
}
