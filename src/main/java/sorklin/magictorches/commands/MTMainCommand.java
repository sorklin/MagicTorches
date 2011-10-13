package sorklin.magictorches.commands;

import java.util.ArrayList;
import java.util.List;
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
        final String cmd = args[0];
        
        if(cmd.equalsIgnoreCase("cancel")) {
            if(sender instanceof Player) {
                pl.mt.setEditMode((Player)sender, false);
                sender.sendMessage(pl.g + "MagicTorch setup cancelled.");
                return true;
            } else {
                sender.sendMessage(pl.r + "You must be a player to use this command.");
                return true;
            }
        } else
        
        if(cmd.equalsIgnoreCase("create")) {
            //Perms handled by that command handler
            ArrayList<String> argArray = new ArrayList<String>();
            for(int i=1, length = args.length; i < length; i++){
                argArray.add(args[i]);
            }
            String[] a = new String[argArray.size()];
            a = argArray.toArray(a);
            return create(sender, a);
        } else
                
        if(cmd.equalsIgnoreCase("delete")) {
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
        
        
        if(cmd.equalsIgnoreCase("direct")) {
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
            
            
        if(cmd.equalsIgnoreCase("finish")) {
            //Perms handled by that command handler
            ArrayList<String> argArray = new ArrayList<String>();
            for(int i=1, length = args.length; i < length; i++){
                argArray.add(args[i]);
            }
            String[] a = new String[argArray.size()];
            a = argArray.toArray(a);
            return finish(sender, a);
        } else
            
            
        if(cmd.equalsIgnoreCase("help")) {
            if(!MagicTorches.canCreate(sender) && !MagicTorches.isAdmin(sender))
                return true;
            
            List<String> help = new ArrayList<String>();

            help.add(pl.g + "/mt create [direct|inverse|delay] " + pl.w + "- Creates a MagicTorch ");
            help.add("array.  Receiver torches selected will be direct (default), ");
            help.add("inverse or delay.");
            
            help.add(pl.g + "/mt cancel " + pl.w + "- Cancels a torch creation or edit.");
            
            help.add(pl.g + "/mt finish <name> " + pl.w + "- Finishes the creation of a MagicTorch ");
            help.add("array, and names it " + pl.g + "<name>" + pl.w + ".");
            
            help.add(pl.g + "/mt direct " + pl.w + "- Sets the next receiver torches selected to be");
            help.add("direct receivers");
                    
            help.add(pl.g + "/mt inverse " + pl.w + "- Sets the next receiver torches selected to be");
            help.add("inverse receivers");
            
            help.add(pl.g + "/mt delay " + pl.w + "- Sets the next receiver torches selected to be");
            help.add("delay receivers");
            
            help.add(pl.g + "/mt delete <name> " + pl.w + "- Delete the named torch array.");
                    
            help.add(pl.g + "/mt info <name> " + pl.w + "- Shows info for the named torch array.");
            
            if(MagicTorches.isAdmin(sender))
                help.add(pl.g + "/mt reload " + pl.w + "- reloads MagicTorches from the database.");
            
            MagicTorches.listMessage(sender, help);
            return true;
        } else
        
        
        if(cmd.equalsIgnoreCase("info")){
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
        
            
        if(cmd.equalsIgnoreCase("inverse")) {
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
                
                
        if(cmd.equalsIgnoreCase("list")) {
            if(MagicTorches.canCreate(sender) || MagicTorches.isAdmin(sender)){
                String intro = (MagicTorches.isAdmin(sender)) ? "All Torches:" : "Your torches:";
                sender.sendMessage(pl.g + intro);
                sender.sendMessage(pl.g + pl.mt.list(sender, MagicTorches.isAdmin(sender)));
            } else {
                sender.sendMessage(pl.r + "Insufficient permissions. Say that three times fast.");
            }
            return true;
        } else
        
        
        if(cmd.equalsIgnoreCase("prune")){
            if(MagicTorches.isAdmin(sender)){
                sender.sendMessage(pl.g + "Pruning db.");
                pl.mt.prune();
            }
            return true;
        } else
        
            
        if(cmd.equalsIgnoreCase("reload")) {
            if(MagicTorches.isAdmin(sender)){
                sender.sendMessage("Reloading TorchArrays from db.");
                pl.mt.reload();
                pl.mt.prune();
                return true;
            }
            sender.sendMessage(pl.r + "Insufficient permissions. Say that three times fast.");
            return true;
        } else
        
            
        if(cmd.equalsIgnoreCase("test")) {
            MagicTorches.spam(pl.mt.listAllReceivers());
            pl.mt.transmitAll();
            return true;
        } else
            
            
        if(cmd.equalsIgnoreCase("delay")) {
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
    
    public boolean create(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(pl.r + "You must be a player to use this command.");
            return true;  //only works for playahs.
        }
        Player player = (Player)sender;
        if(!pl.canCreate(player)){
            sender.sendMessage(pl.r + "Insufficient permissions. Say that three times fast.");
            return true;
        }
        
        if(args.length == 0) {
            //Assume a DIRECT type of linkage
            pl.mt.setEditMode(player, false); //reset the plvars, if they exist.
            pl.mt.setEditMode(player);
            sender.sendMessage(pl.g + "Creating a MagicTorch array. " + pl.w + "Left click on a torch to set it as");
            sender.sendMessage("a transmitter. Right click on torches to add/remove them from");
            sender.sendMessage("the receiver array.");
            return true;
        } else
        
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("direct")) {
                pl.mt.setEditMode(player, false); //reset the plvars, if they exist.
                pl.mt.setEditMode(player, TorchArray.DIRECT);
            } else
            
            if(args[0].equalsIgnoreCase("inverse")) {
                pl.mt.setEditMode(player, false); //reset the plvars, if they exist.
                pl.mt.setEditMode(player, TorchArray.INVERSE);
            } else
            
            if(args[0].equalsIgnoreCase("delay")) {
                pl.mt.setEditMode(player, false); //reset the plvars, if they exist.
                pl.mt.setEditMode(player, TorchArray.DELAY);
            } else
            
            {
                return false;
            }
            
            sender.sendMessage(pl.g + "Creating a MagicTorch array. " + pl.w + "Left click on a torch to set it as");
            sender.sendMessage("a transmitter. Right click on torches to add/remove them from");
            sender.sendMessage("the receiver array.");
            return true;
        } else
            
        {
            return false;
        }
    }
    
    public boolean finish(CommandSender sender, String[] args){
        if(!MagicTorches.canCreate(sender)){
            return true;
        }
        
        Player player = (Player)sender;
        if(!pl.mt.isInEditMode(player)) {
            sender.sendMessage(pl.r + "You are not in edit mode. Type /mtcreate to begin.");
            return false;
        }
        
        if(args.length != 1) {
            sender.sendMessage(pl.r + "Incorrect number of arguments.");
            return false;
        }

        if(pl.mt.create(player, args[0])) {
            sender.sendMessage(pl.g + "Successfully created MagicTorch array: " + pl.b + pl.mt.message);
            pl.mt.setEditMode(player, false);
        } else {
            sender.sendMessage(pl.r + pl.mt.message);
        }
        return true;
    }
}
