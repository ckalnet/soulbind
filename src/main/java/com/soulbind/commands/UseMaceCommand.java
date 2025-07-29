package com.soulbind.commands;

import com.soulbind.managers.CursedMaceManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UseMaceCommand implements CommandExecutor {
    
    private final CursedMaceManager cursedMaceManager;
    
    public UseMaceCommand(CursedMaceManager cursedMaceManager) {
        this.cursedMaceManager = cursedMaceManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!cursedMaceManager.hasCursedMace(player)) {
            player.sendMessage("§c✗ §7You don't possess the Cursed Mace!");
            return true;
        }
        
        if (!cursedMaceManager.isCursedMace(player.getInventory().getItemInMainHand())) {
            player.sendMessage("§c✗ §7You must be holding the Cursed Mace to use this command!");
            return true;
        }
        
        cursedMaceManager.useMaceAbility(player);
        
        return true;
    }
}