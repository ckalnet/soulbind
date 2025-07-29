package com.soulbind.listeners;

import com.soulbind.managers.MilestoneManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    
    private final MilestoneManager milestoneManager;
    
    public PlayerJoinListener(MilestoneManager milestoneManager) {
        this.milestoneManager = milestoneManager;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Update milestone buffs when player joins
        milestoneManager.updatePlayerBuffs(player);
    }
}