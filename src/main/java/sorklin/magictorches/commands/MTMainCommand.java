package sorklin.magictorches.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sorklin.magictorches.MagicTorches;

public class MTMainCommand implements CommandExecutor{
    private final MagicTorches pl;
    
    public MTMainCommand(MagicTorches mt) {
        this.pl = mt;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(pl.g + "Incorrect number of parameters.");
            return false;
        }
        //<cancel | delete <name> | help | list | reload>
        if(args[0].equalsIgnoreCase("cancel")) {
            if(sender instanceof Player) {
                pl.mt.setEditMode((Player)sender, false);
                sender.sendMessage(pl.g + "MagicTorch setup cancelled.");
                return true;
            } else {
                sender.sendMessage(pl.r + "You must be a player to use this command.");
                return true;
            }
        } else
        
        if(args[0].equalsIgnoreCase("delete")) {
            //TODO: delete clause
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("help")) {
            //TODO: help clause
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("list")) {
            //TODO: list clause
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("reload")) {
            //TODO: reload clause
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("test")) {
            //whatever i need
            return true;
        } else
            
        {
            sender.sendMessage(pl.r + "Unrecognized parameter.");
            return false;
        }
        
    }
    
}
