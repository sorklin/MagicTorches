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
import sorklin.magictorches.internals.MTUtil;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.TorchEditor;

public class CreateCmd extends GenericCmd {
    
    public CreateCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException {
        errorCheck();

        String msg = "`gCreating a MagicTorch array. `wLeft click on a torch to set it as "
                + "%cr%a transmitter. Right click on torches to add/remove them from"
                + "%cr%the receiver array.  Hold a lever to receive information about any"
                + "%cr%clicked torch.";
        
        TorchEditor te = new TorchEditor(player);
        
        if(args.length <= 1) {
            //Assume a DIRECT type of linkage
            if(!MTUtil.hasPermission(player, Properties.permCreateDirect))
                throw new InsufficientPermissionsException();
            te.setNextType(MtType.DIRECT);
            Messaging.send(player, msg);
        }
        
        else if(args.length >= 2) {
            if(args[1].equalsIgnoreCase("direct")) {
                if(!MTUtil.hasPermission(player, Properties.permCreateDirect))
                    throw new InsufficientPermissionsException();
                te.setNextType(MtType.DIRECT);
            } else if(args[1].equalsIgnoreCase("inverse")) {
                if(!MTUtil.hasPermission(player, Properties.permCreateInverse))
                    throw new InsufficientPermissionsException();
                te.setNextType(MtType.INVERSE);
            } else if(args[1].equalsIgnoreCase("delay")) {
                if(!MTUtil.hasPermission(player, Properties.permCreateDelay))
                    throw new InsufficientPermissionsException();
                te.setNextType(MtType.DELAY);
            } else if(args[1].equalsIgnoreCase("toggle")) {
                if(!MTUtil.hasPermission(player, Properties.permCreateToggle))
                    throw new InsufficientPermissionsException();
                te.setNextType(MtType.TOGGLE);
            } else if(args[1].equalsIgnoreCase("timer")) {
                if(!MTUtil.hasPermission(player, Properties.permCreateTimer))
                    throw new InsufficientPermissionsException();
                te.setNextType(MtType.TIMER);
                double timeOut = Properties.timerDelay;
                if(args.length > 2)
                    try { timeOut = Double.valueOf(args[2]);                        
                    } catch (NumberFormatException nfe) {}
                te.setTimeOut(timeOut);
            } else {
                throw new MissingOrIncorrectParametersException();
            }
            Messaging.send(player, msg);
        }
        //This will overwrite an existing creator (which we're okay with).
        mt.editQueue.put(player, te);
        return true;
    }
}
