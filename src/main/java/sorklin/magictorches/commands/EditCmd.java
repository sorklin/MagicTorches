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
        
        TorchArray ta = mt.mtHandler.getArray(args[1]);
        
        if(ta != null){
            if(!MTUtil.hasPermission(player, Properties.permAdmin) ||
                    !ta.getOwner().equalsIgnoreCase(player.getName()))
                throw new InsufficientPermissionsException("That is not your torcharray.");
            
            TorchEditor te = (TorchEditor)ta;
            te.setOriginal(ta);
            mt.mtHandler.removeArray(ta.getLocation()); //remove it from active arrays.
            te.resetReceivers();
            mt.editQueue.put(player, te);
            
            Messaging.send(player, "`gNow editing the `w" + ta.getName() + "`g array.");
            Messaging.send(player, "Left click on a torch to set it a transmitter. "
                + "%cr%Right click on torches to add/remove them from the receiver"
                + "%cr%array.  Hold a lever and right-click a torch, to receive "
                + "%cr%information about any clicked torch. Type `Y/mt finish `wto "
                + "%cr%finish editing the array.");
        } else {
            throw new MissingOrIncorrectParametersException("No array by that name was found.");
        }
        return true;
    }
}
