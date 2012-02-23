/*
 * Copyright (C) 2012 Sorklin <sorklin at gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sorklin.magictorches.commands;

import org.bukkit.command.CommandSender;
import sorklin.magictorches.Exceptions.InsufficientPermissionsException;
import sorklin.magictorches.Exceptions.MissingOrIncorrectParametersException;
import sorklin.magictorches.internals.Properties;

public class HelpCmd extends GenericCmd {
    
    /*Default the generic to must be executed by a player, and no minimum arguments.
    String permission = "";
    boolean mustBePlayer = true;
    int minArg = 0;
    */
    
    public HelpCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException{
        errorCheck();
        
        //DO work, son.
        
                    
//        if(cmd.equalsIgnoreCase("help")) {
//            if(!MagicTorches.canCreate(sender) && !MagicTorches.isAdmin(sender))
//                return true;
//            
//            List<String> help = new ArrayList<String>();
//
//            help.add(pl.g + "/mt create [direct|inverse|delay] " + pl.w + "- Creates a MagicTorch ");
//            help.add("array.  Receiver torches selected will be direct (default), ");
//            help.add("inverse or delay.");
//            
//            help.add(pl.g + "/mt cancel " + pl.w + "- Cancels a torch creation or edit.");
//            
//            help.add(pl.g + "/mt finish <name> " + pl.w + "- Finishes the creation of a MagicTorch ");
//            help.add("array, and names it " + pl.g + "<name>" + pl.w + ".");
//            
//            help.add(pl.g + "/mt direct " + pl.w + "- Sets the next receiver torches selected to be");
//            help.add("direct receivers");
//                    
//            help.add(pl.g + "/mt inverse " + pl.w + "- Sets the next receiver torches selected to be");
//            help.add("inverse receivers");
//            
//            help.add(pl.g + "/mt delay " + pl.w + "- Sets the next receiver torches selected to be");
//            help.add("delay receivers");
//            
//            help.add(pl.g + "/mt delete <name> " + pl.w + "- Delete the named torch array.");
//                    
//            help.add(pl.g + "/mt info <name> " + pl.w + "- Shows info for the named torch array.");
//            
//            if(MagicTorches.isAdmin(sender))
//                help.add(pl.g + "/mt reload " + pl.w + "- reloads MagicTorches from the database.");
//            
//            MagicTorches.listMessage(sender, help);
//            return true;
//        } else

        return true;
    }
}
