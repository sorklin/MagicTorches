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
            if(args.length == 2){
                if (pl.mt.delete(args[1].toLowerCase().trim())) {
                    sender.sendMessage(pl.g + "Deleted MagicTorch Array: " + 
                            pl.b + args[1].toLowerCase().trim());
                }
                return true;
            }
            sender.sendMessage(pl.r + "Incorrect number of parameters.");
            return false;
        } else
        
        if(args[0].equalsIgnoreCase("help")) {
            //TODO: help clause
            sender.sendMessage(pl.r + "Not yet implemented.  You're on your own, chuck.");
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("list")) {
//            if(sender.hasPermission(MagicTorches.perm_create) 
//                    || sender.hasPermission(MagicTorches.perm_admin)){
                sender.sendMessage(pl.g + pl.mt.list(sender, sender.hasPermission(MagicTorches.perm_admin)));
//            }
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("Reloading TorchArrays from db.");
            pl.mt.reload();
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("test")) {
            //sender.sendMessage(pl.mt.listRecievers(sender));
            pl.mt.transmitAll();
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("create")) {
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
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(pl.mt.isInEditMode(player)){
                    pl.mt.setNextType(player, TorchArray.DIRECT);
                    sender.sendMessage(pl.g + "Receiver type set to DIRECT.");
                }
            }
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("inverse")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(pl.mt.isInEditMode(player)){
                    pl.mt.setNextType(player, TorchArray.INVERSE);
                    sender.sendMessage(pl.g + "Receiver type set to INVERSE.");
                }
            }
            return true;
        } else
        
        if(args[0].equalsIgnoreCase("delay")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(pl.mt.isInEditMode(player)){
                    pl.mt.setNextType(player, TorchArray.DELAY);
                    sender.sendMessage(pl.g + "Receiver type set to DELAY.");
                }
            }
            return true;
        } else
        
        {
            sender.sendMessage(pl.r + "Unrecognized parameter.");
            return false;
        }
        
    }
    
}
