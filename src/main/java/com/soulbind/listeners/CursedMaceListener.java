package com.soulbind.listeners;

import com.soulbind.managers.CursedMaceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class CursedMaceListener implements Listener {
    
    private final CursedMaceManager cursedMaceManager;
    
    public CursedMaceListener(CursedMaceManager cursedMaceManager) {
        this.cursedMaceManager = cursedMaceManager;
    }
    
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        
        if (cursedMaceManager.isCursedMace(item)) {
            // Bind the cursed mace to the player
            cursedMaceManager.bindCursedMace(player);
            player.sendMessage("§4⚠ §cYou have picked up the Cursed Mace! §4The curse now binds you...");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        
        if (cursedMaceManager.isCursedMace(item) && cursedMaceManager.hasCursedMace(player)) {
            // Prevent dropping the cursed mace
            event.setCancelled(true);
            player.sendMessage("§4✗ §cThe Cursed Mace cannot be dropped! §7It is bound to your soul...");
        }
    }
}