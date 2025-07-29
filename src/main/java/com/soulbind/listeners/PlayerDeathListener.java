package com.soulbind.listeners;

import com.soulbind.managers.CursedMaceManager;
import com.soulbind.managers.SoulFragmentManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {
    
    private final SoulFragmentManager soulFragmentManager;
    private final CursedMaceManager cursedMaceManager;
    
    public PlayerDeathListener(SoulFragmentManager soulFragmentManager, CursedMaceManager cursedMaceManager) {
        this.soulFragmentManager = soulFragmentManager;
        this.cursedMaceManager = cursedMaceManager;
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();
        Player killer = deadPlayer.getKiller();
        
        // Check if player died while wielding cursed mace
        ItemStack mainHand = deadPlayer.getInventory().getItemInMainHand();
        ItemStack offHand = deadPlayer.getInventory().getItemInOffHand();
        
        boolean hasCursedMace = cursedMaceManager.isCursedMace(mainHand) || cursedMaceManager.isCursedMace(offHand);
        
        if (hasCursedMace) {
            // Consume fragments silently instead of dropping
            int statFragments = soulFragmentManager.getStatFragments(deadPlayer);
            if (statFragments > 0) {
                soulFragmentManager.removeStatFragments(deadPlayer, 1);
            }
            return; // No soul fragment drop
        }
        
        // Normal death - drop soul fragment
        ItemStack soulFragment = soulFragmentManager.createSoulFragment();
        deadPlayer.getWorld().dropItemNaturally(deadPlayer.getLocation(), soulFragment);
        
        // Update cursed mace kill timers for the killer
        if (killer != null && cursedMaceManager.hasCursedMace(killer)) {
            cursedMaceManager.recordKill(killer);
        }
    }
}