package net.digiex.simplefeatures.listeners;

import net.digiex.simplefeatures.SFPlugin;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

public class EListener extends EntityListener {

    private SFPlugin plugin;

    public EListener(SFPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityExplode(EntityExplodeEvent e) {
        e.setCancelled(true);
        if (e.getEntity() instanceof org.bukkit.entity.TNTPrimed) {
            e.getLocation().getWorld().dropItemNaturally(e.getLocation(), new ItemStack(Material.TNT, 1));
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (plugin.gods.containsKey(player.getName())) {
                // will still take damage if hungry, bukkit must not tigger this event when dieing by hunger
                event.setCancelled(true);
                player.setFireTicks(0);
                player.setMaximumAir(player.getMaximumAir());
            }
        }
    }
    
}