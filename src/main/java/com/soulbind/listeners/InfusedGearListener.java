package com.soulbind.listeners;

import com.soulbind.managers.InfusedGearManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class InfusedGearListener implements Listener {
    
    private final InfusedGearManager infusedGearManager;
    
    public InfusedGearListener(InfusedGearManager infusedGearManager) {
        this.infusedGearManager = infusedGearManager;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update boots effect when player joins
        infusedGearManager.updateBootsOfEchoesEffect(event.getPlayer());
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        // Check if boots slot was modified
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 36) { // Boots slot
            // Delay the update to allow the inventory change to complete
            infusedGearManager.getPlugin().getServer().getScheduler().runTaskLater(
                infusedGearManager.getPlugin(), 
                () -> infusedGearManager.updateBootsOfEchoesEffect(player), 
                1L
            );
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        if (killer != null) {
            // Handle Sword of the Drained effect
            infusedGearManager.handleSwordOfDrainedKill(killer, victim);
        }
        
        // Update boots effect for victim after respawn
        infusedGearManager.getPlugin().getServer().getScheduler().runTaskLater(
            infusedGearManager.getPlugin(),
            () -> infusedGearManager.updateBootsOfEchoesEffect(victim),
            20L // 1 second delay
        );
    }
}