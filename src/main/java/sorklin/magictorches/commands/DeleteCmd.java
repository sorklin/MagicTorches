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
import sorklin.magictorches.internals.Properties;

public class DeleteCmd extends GenericCmd {
    
    public DeleteCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permCreate;
    }
    
    public boolean execute() {
        if(errorCheck())
            return true;
        
//            String name = (MagicTorches.canCreate(sender)) ? sender.getName() : "";
//            boolean isAdmin = MagicTorches.isAdmin(sender);
//            
//            if(!name.isEmpty() || isAdmin) {
//                if(args.length == 2){
//                    if (pl.mt.delete(args[1].toLowerCase().trim(), name, isAdmin)){
//                        sender.sendMessage(pl.g + "Deleted MagicTorch Array: " + 
//                                pl.b + args[1].toLowerCase().trim());
//                    }
//                    return true;
//                }
//                sender.sendMessage(pl.r + "Incorrect number of parameters.");
//                return false;
//            }
//            sender.sendMessage(pl.r + "Insufficient permissions. Say that three times fast.");
//            return true;
        
        return true;
    }
}
