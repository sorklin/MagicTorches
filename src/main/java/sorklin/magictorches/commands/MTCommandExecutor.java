package sorklin.magictorches.commands;

import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import sorklin.magictorches.Exceptions.InsufficientPermissionsException;
import sorklin.magictorches.Exceptions.MissingOrIncorrectParametersException;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.interfaces.Cmd;

public class MTCommandExecutor implements CommandExecutor{
    private final MagicTorches pl;
    private Cmd cmd;
    
    public MTCommandExecutor(MagicTorches mt) {
        this.pl = mt;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String c;
        if(args.length == 0)
            c = "help";
        else 
            c = args[0];
        
        try {
            if(c.equalsIgnoreCase("activate"))
                cmd = new ActivateCmd(sender, args);
            else if(c.equalsIgnoreCase("cancel"))
                cmd = new CancelCmd(sender, args);
            else if(c.equalsIgnoreCase("create"))
                cmd = new CreateCmd(sender, args);
            else if(c.equalsIgnoreCase("deactivate"))
                cmd = new DeactivateCmd(sender, args);
            else if(c.equalsIgnoreCase("debug"))
                cmd = new DebugCmd(sender, args);
            else if(c.equalsIgnoreCase("delay"))
                cmd = new DelayCmd(sender, args);
            else if(c.equalsIgnoreCase("delete") || c.equalsIgnoreCase("del"))
                cmd = new DeleteCmd(sender, args);
            else if(c.equalsIgnoreCase("direct"))
                cmd = new DirectCmd(sender, args);
            else if(c.equalsIgnoreCase("edit"))
                cmd = new EditCmd(sender, args);
            else if(c.equalsIgnoreCase("finish") || c.equalsIgnoreCase("fin"))
                cmd = new FinishCmd(sender, args);
            else if(c.equalsIgnoreCase("help") || c.equalsIgnoreCase("?"))
                cmd = new HelpCmd(sender, args);
            else if(c.equalsIgnoreCase("info"))
                cmd = new InfoCmd(sender, args);
            else if(c.equalsIgnoreCase("inverse"))
                cmd = new InverseCmd(sender, args);
            else if(c.equalsIgnoreCase("list"))
                cmd = new ListCmd(sender, args);
            else if(c.equalsIgnoreCase("prune"))
                cmd = new PruneCmd(sender, args);
            else if(c.equalsIgnoreCase("reload"))
                cmd = new ReloadCmd(sender, args);
            else if(c.equalsIgnoreCase("price"))
                cmd = new PriceCmd(sender, args);
            else if(c.equalsIgnoreCase("timer"))
                cmd = new TimerCmd(sender, args);
            else if(c.equalsIgnoreCase("toggle"))
                cmd = new ToggleCmd(sender, args);
            
            else
                throw new MissingOrIncorrectParametersException();
            
            return cmd.execute();
            
        } catch (InsufficientPermissionsException ex) {
            sender.sendMessage(ex.getMessage());
        } catch (MissingOrIncorrectParametersException ex) {
            sender.sendMessage(ex.getMessage());
        } catch (Exception ex) {
            MagicTorches.log(Level.WARNING, "Exception in Commandlistner:");
            ex.printStackTrace();
        }
        
        return true;
    }
}
