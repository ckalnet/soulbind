package com.soulbind.commands;

import com.soulbind.managers.MilestoneManager;
import com.soulbind.managers.SoulFragmentManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawSoulCommand implements CommandExecutor {
    
    private final SoulFragmentManager soulFragmentManager;
    private final MilestoneManager milestoneManager;
    
    public WithdrawSoulCommand(SoulFragmentManager soulFragmentManager, MilestoneManager milestoneManager) {
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
        
        if (args.length != 1) {
            player.sendMessage("§c✗ §7Usage: /withdrawsoul <amount>");
            return true;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§c✗ §7Please enter a valid number!");
            return true;
        }
        
        if (amount <= 0) {
            player.sendMessage("§c✗ §7Amount must be greater than 0!");
            return true;
        }
        
        int currentFragments = soulFragmentManager.getStatFragments(player);
        if (amount > currentFragments) {
            player.sendMessage("§c✗ §7You only have §e" + currentFragments + " §7stat fragments!");
            return true;
        }
        
        if (soulFragmentManager.withdrawFragments(player, amount)) {
            player.sendMessage("§5✦ §7Withdrew §e" + amount + " §7stat fragments as items!");
            player.sendMessage("§7Remaining stat fragments: §e" + soulFragmentManager.getStatFragments(player));
            
            // Update milestone buffs
            milestoneManager.updatePlayerBuffs(player);
        } else {
            player.sendMessage("§c✗ §7Failed to withdraw fragments!");
        }
        
        return true;
    }
}