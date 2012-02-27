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
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.*;

public class EditCmd extends GenericCmd {
    
    /*Default the generic to must be executed by a player, and no minimum arguments.
    String permission = "";
    boolean mustBePlayer = true;
    int minArg = 0;
    */
    
    public EditCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
        this.minArg = 2;
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException{
        errorCheck();
        //MagicTorches.spam("in edit execute.");
        String msg = "Left click on a torch to set it a transmitter. Right click on"
                + "%cr%torches to add/remove them from the receiver array.  Hold a "
                + "%cr%lever and right-click a torch, to receive information about it."
                + "%cr%Type `Y/mt finish `wto finish editing the array.";
        if(Properties.useEconomy){
            msg += "%cr%`YBase price for editing an array is `a";
            msg += MagicTorches.econ.format(Properties.priceArrayEdit);
            msg += "`Y.";
        }
        
        TorchArray ta = mt.mtHandler.getArray(args[1]);
        //MagicTorches.spam("getarray = " + ta.getName());
        if(ta != null){
            if(!MTUtil.hasPermission(player, Properties.permAdmin) ||
                    !ta.getOwner().equalsIgnoreCase(player.getName()))
                throw new InsufficientPermissionsException("That is not your torcharray.");
            
            TorchEditor te = new TorchEditor(ta);
            mt.mtHandler.removeArray(ta.getLocation()); //remove it from active arrays.
            te.resetReceivers();
            mt.editQueue.put(player, te);
            
            Messaging.send(player, "`gNow editing the `a" + ta.getName() + "`g array.");
            Messaging.send(player, msg);
        } else {
            throw new MissingOrIncorrectParametersException("No array by that name was found.");
        }
        return true;
    }
}
