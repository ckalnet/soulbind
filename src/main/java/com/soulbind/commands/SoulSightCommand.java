package com.soulbind.commands;

import com.soulbind.managers.MilestoneManager;
import com.soulbind.managers.SoulFragmentManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoulSightCommand implements CommandExecutor {
    
    private final SoulFragmentManager soulFragmentManager;
    private final MilestoneManager milestoneManager;
    
    public SoulSightCommand(SoulFragmentManager soulFragmentManager, MilestoneManager milestoneManager) {
        this.soulFragmentManager = soulFragmentManager;
        this.milestoneManager = milestoneManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        String status = milestoneManager.getPlayerStatus(player);
        player.sendMessage(status);
        
        return true;
    }
}