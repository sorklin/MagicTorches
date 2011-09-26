package sorklin.magictorches.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
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
            //TEMP:
            sender.sendMessage(pl.g + pl.mt.list(sender, true));
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("Reloading TorchArrays from db.");
            pl.mt.reload();
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("test")) {
            //whatever i need
            return true;
        } else
        
//        if(args[0].equalsIgnoreCase("create")) {
//            List<String> argArray = new ArrayList<String>();
//            for(int i=1, length = args.length; i < length; i++){
//                argArray.add(args[i]);
//            }
//            try {
//                PluginCommand cmd = Bukkit.getServer().getPluginCommand("create");
//                return cmd.execute(sender, label, argArray.toArray(new String[0]));
//            } catch (CommandException e) {
//                pl.spam("Exception: " + e.toString());
//            }
//            return true;
//        } else
        
//        if(args[0].equalsIgnoreCase("finish")) {
//            //whatever i need
//            return true;
//        } else
            
        {
            sender.sendMessage(pl.r + "Unrecognized parameter.");
            return false;
        }
        
    }
    
}
