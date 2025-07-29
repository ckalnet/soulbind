package com.soulbind.commands;

import com.soulbind.managers.MilestoneManager;
import com.soulbind.managers.SoulFragmentManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BindSoulCommand implements CommandExecutor {
    
    private final SoulFragmentManager soulFragmentManager;
    private final MilestoneManager milestoneManager;
    
    public BindSoulCommand(SoulFragmentManager soulFragmentManager, MilestoneManager milestoneManager) {
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
        
        int converted = soulFragmentManager.convertItemsToStats(player);
        
        if (converted == 0) {
            player.sendMessage("§c✗ §7You have no Soul Fragment items to convert!");
            return true;
        }
        
        player.sendMessage("§5✦ §7Converted §e" + converted + " §7Soul Fragment items to stat fragments!");
        player.sendMessage("§7Total stat fragments: §e" + soulFragmentManager.getStatFragments(player));
        
        // Update milestone buffs
        milestoneManager.updatePlayerBuffs(player);
        
        return true;
    }
}