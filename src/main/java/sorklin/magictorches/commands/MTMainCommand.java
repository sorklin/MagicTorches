package sorklin.magictorches.commands;

import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.TorchArray;

public class MTMainCommand implements CommandExecutor{
    private final MagicTorches pl;
    
    public MTMainCommand(MagicTorches mt) {
        this.pl = mt;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(pl.r + "Incorrect number of parameters.");
            return false;
        }
        
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
            String name = (MagicTorches.canCreate(sender)) ? sender.getName() : "";
            boolean isAdmin = MagicTorches.isAdmin(sender);
            
            if(!name.isEmpty() || isAdmin) {
                if(args.length == 2){
                    if (pl.mt.delete(args[1].toLowerCase().trim(), name, isAdmin)){
                        sender.sendMessage(pl.g + "Deleted MagicTorch Array: " + 
                                pl.b + args[1].toLowerCase().trim());
                    }
                    return true;
                }
                sender.sendMessage(pl.r + "Incorrect number of parameters.");
                return false;
            }
            sender.sendMessage(pl.r + "Insufficient permissions. Say that three times fast.");
            return true;
        } else
        
            
        if(args[0].equalsIgnoreCase("help")) {
            //TODO: help clause
            sender.sendMessage(pl.r + "Not yet implemented.  You're on your own, chuck.");
            return true;
        } else
        
        
        if(args[0].equalsIgnoreCase("info")){
            if(MagicTorches.canCreate(sender) || MagicTorches.isAdmin(sender)){
                if(args.length < 2){
                    sender.sendMessage(pl.r + "For general MagicTorch information, equip a lever and");
                    sender.sendMessage(pl.r + "right click on a torch.  For more specific information, type");
                    sender.sendMessage(pl.r + "/info <name>.");
                    return true;
                }
                pl.mt.showInfo(args[1], sender, MagicTorches.isAdmin(sender));
            }
            return true;
        } else 
        
            
        if(args[0].equalsIgnoreCase("list")) {
            if(MagicTorches.canCreate(sender) || MagicTorches.isAdmin(sender)){
                String intro = (MagicTorches.isAdmin(sender)) ? "All Torches:" : "Your torches:";
                sender.sendMessage(pl.g + intro);
                sender.sendMessage(pl.g + pl.mt.list(sender, MagicTorches.isAdmin(sender)));
            } else {
                sender.sendMessage(pl.r + "Insufficient permissions. Say that three times fast.");
            }
            return true;
        } else
        
            
        if(args[0].equalsIgnoreCase("reload")) {
            if(MagicTorches.isAdmin(sender)){
                sender.sendMessage("Reloading TorchArrays from db.");
                pl.mt.reload();
                return true;
            }
            sender.sendMessage(pl.r + "Insufficient permissions. Say that three times fast.");
            return true;
        } else
        
            
        if(args[0].equalsIgnoreCase("test")) {
            MagicTorches.spam(pl.mt.listAllReceivers());
            return true;
        } else
        
            
        if(args[0].equalsIgnoreCase("create")) {
            //Perms handled by that command handler
            ArrayList<String> argArray = new ArrayList<String>();
            for(int i=1, length = args.length; i < length; i++){
                argArray.add(args[i]);
            }
            MTCreateCommand mt = new MTCreateCommand(pl);
            String[] a = new String[argArray.size()];
            a = argArray.toArray(a);
            return mt.createExecute(sender, a);
        } else
        
            
        if(args[0].equalsIgnoreCase("finish")) {
            //Perms handled by that command handler
            ArrayList<String> argArray = new ArrayList<String>();
            for(int i=1, length = args.length; i < length; i++){
                argArray.add(args[i]);
            }
            MTFinishCommand mt = new MTFinishCommand(pl);
            String[] a = new String[argArray.size()];
            a = argArray.toArray(a);
            return mt.finish(sender, a);
        } else
        
            
        if(args[0].equalsIgnoreCase("direct")) {
            //Edit mode implies player and permissions.
            if(sender instanceof Player) {
                if(pl.mt.isInEditMode((Player)sender)){
                    pl.mt.setNextType((Player)sender, TorchArray.DIRECT);
                    sender.sendMessage(pl.g + "Receiver type set to DIRECT.");
                }
                return true;
            } else {
                sender.sendMessage(pl.r + "You must be a player to use this command.");
                return true;
            }
        } else
        
            
        if(args[0].equalsIgnoreCase("inverse")) {
            //Edit mode implies player and permissions.
            if(sender instanceof Player) {
                if(pl.mt.isInEditMode((Player)sender)){
                    pl.mt.setNextType((Player)sender, TorchArray.INVERSE);
                    sender.sendMessage(pl.g + "Receiver type set to INVERSE.");
                }
                return true;
            } else {
                sender.sendMessage(pl.r + "You must be a player to use this command.");
                return true;
            }
        } else
        
            
        if(args[0].equalsIgnoreCase("delay")) {
            //Edit mode implies player and permissions.
            if(sender instanceof Player) {
                if(pl.mt.isInEditMode((Player)sender)){
                    pl.mt.setNextType((Player)sender, TorchArray.DELAY);
                    sender.sendMessage(pl.g + "Receiver type set to DELAY.");
                }
                return true;
            } else {
                sender.sendMessage(pl.r + "You must be a player to use this command.");
                return true;
            }
        } else
        
        //No commands matched
        {
            sender.sendMessage(pl.r + "Unrecognized parameter.");
            return false;
        }
    }
}
