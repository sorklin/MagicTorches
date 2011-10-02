package sorklin.magictorches.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sorklin.magictorches.internals.TorchArray;
import sorklin.magictorches.MagicTorches;

public class MTCreateCommand implements CommandExecutor{
    private MagicTorches pl;

    public MTCreateCommand(MagicTorches mt) {
        this.pl = mt;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return createExecute(sender, args);
    }
    
    public boolean createExecute(CommandSender sender, String[] args) {
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
}
