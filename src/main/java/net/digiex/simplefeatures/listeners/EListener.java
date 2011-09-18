/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.digiex.simplefeatures.listeners;

import net.digiex.simplefeatures.SFPlugin;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author jessenic
 */
public class EListener extends EntityListener{
    	SFPlugin plugin;
	public EListener(SFPlugin parent){
		plugin = parent;
	}
        @Override
        public void onEntityExplode(EntityExplodeEvent e){
            e.setCancelled(true);
            if(e.getEntity() instanceof org.bukkit.entity.TNTPrimed){
                e.getLocation().getWorld().dropItemNaturally(e.getLocation(), new ItemStack(Material.TNT,1));
            }
        }
}
