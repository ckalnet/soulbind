package com.soulbind.commands;

import com.soulbind.managers.MilestoneManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BloodrageCommand implements CommandExecutor {
    
    private final MilestoneManager milestoneManager;
    
    public BloodrageCommand(MilestoneManager milestoneManager) {
        this.milestoneManager = milestoneManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!milestoneManager.canUseBloodrage(player)) {
            if (milestoneManager.getBloodrageRemainingCooldown(player) > 0) {
                long seconds = milestoneManager.getBloodrageRemainingCooldown(player) / 1000;
                player.sendMessage("§c✗ §7Bloodrage is on cooldown for §e" + seconds + " §7seconds!");
            } else {
                player.sendMessage("§c✗ §7You need §e30 stat fragments §7to use Bloodrage!");
            }
            return true;
        }
        
        if (milestoneManager.useBloodrage(player)) {
            player.sendMessage("§4⚔ §cBloodrage activated! §7Strength III for 30 seconds!");
        } else {
            player.sendMessage("§c✗ §7Failed to activate Bloodrage!");
        }
        
        return true;
    }
}