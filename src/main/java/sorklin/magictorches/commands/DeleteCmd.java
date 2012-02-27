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
import sorklin.magictorches.internals.MTUtil;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.TorchArray;

public class DeleteCmd extends GenericCmd {
    
    public DeleteCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
        this.mustBePlayer = false;
        
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException {
        errorCheck();
        
        TorchArray ta = mt.mtHandler.getArray(args[1]);
        
        if(ta == null)
            throw new MissingOrIncorrectParametersException("No TorchArray by that name.");
                
        if(!MTUtil.hasPermission(cs, Properties.permAdmin) || !ta.getOwner().equalsIgnoreCase(cs.getName()))
            throw new InsufficientPermissionsException("That is not your torcharray.");
        
        mt.mtHandler.removeArray(ta.getLocation());
        MagicTorches.getMiniDB().remove(ta.getName());
        Messaging.send(cs, "`YRemoved the `a" + ta.getName() + "`Y array.");
        
        return true;
    }
}
