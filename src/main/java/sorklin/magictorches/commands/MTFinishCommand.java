package sorklin.magictorches.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sorklin.magictorches.MagicTorches;

public class MTFinishCommand implements CommandExecutor {
    private final MagicTorches pl;

    public MTFinishCommand(MagicTorches mt) {
        this.pl = mt;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return finish(sender, args);
    }
    
    public boolean finish(CommandSender sender, String[] args){
        if(!(sender instanceof Player)) {
            sender.sendMessage(pl.r + "You must be a player to use this command.");
            return true;  //only works for playahs.
        }
        Player player = (Player)sender;
        if(!pl.canCreate(player)){
            sender.sendMessage(pl.r + "Insufficient permissions. Say that three times fast.");
            return true;
        }
        
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
