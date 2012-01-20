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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.TorchCreator;

public class CreateCmd extends GenericCmd {
    
    public CreateCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permCreate;
    }
    
    public boolean execute() {
        if(errorCheck())
            return true;
        
        //Default message
        List<String> msg = new ArrayList<String>();
        msg.add("`gCreating a MagicTorch array. `wLeft click on a torch to set it as");
        msg.add("a transmitter. Right click on torches to add/remove them from");
        msg.add("the receiver array.");
        
        //Load or create a new Creator object
        TorchCreator tc;
        if(mt.todo.containsKey(player))
            tc = mt.todo.get(player);
        else
            tc = new TorchCreator(player);
        
        if(args.length <= 1) {
            //Assume a DIRECT type of linkage
            tc.setNextType(MtType.DIRECT);
            Messaging.mlSend(player, msg);
        } else
        
        if(args.length >= 2) {
            if(args[1].equalsIgnoreCase("direct")) {
                tc.setNextType(MtType.DIRECT);
            } else if(args[1].equalsIgnoreCase("inverse")) {
                tc.setNextType(MtType.INVERSE);
            } else if(args[1].equalsIgnoreCase("delay")) {
                tc.setNextType(MtType.DELAY);
            } else if(args[1].equalsIgnoreCase("timer")) {
                tc.setNextType(MtType.TIMER);
                double timeOut = Properties.timerDelay;
                if(args.length > 2)
                    try { timeOut = Double.valueOf(args[2]);                        
                    } catch (NumberFormatException nfe) {}
                tc.setTimeOut(timeOut);
            } else {
                return false;
            }
            Messaging.mlSend(player, msg);
        } else {
            return false;
        }
        return true;
    }
}
