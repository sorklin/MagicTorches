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

import java.util.List;
import org.bukkit.command.CommandSender;
import sorklin.magictorches.Exceptions.InsufficientPermissionsException;
import sorklin.magictorches.Exceptions.MissingOrIncorrectParametersException;
import sorklin.magictorches.internals.MTUtil;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.TorchArray;

public class InfoCmd extends GenericCmd {
    
    /*Default the generic to must be executed by a player, and no minimum arguments.
    String permission = "";
    boolean mustBePlayer = true;
    int minArg = 0;
    */
    
    public InfoCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
        this.minArg = 2;
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException{
        errorCheck();
        // /mt info <name> [page]
        
        TorchArray ta = mt.mtHandler.getArray(args[1]);
        
        if(ta == null)
            throw new MissingOrIncorrectParametersException("No TorchArray by that name.");
        
        List<String> infoMsg = mt.mtHandler
                .getInfo(ta.getLocation().getBlock(), player.getName(), 
                MTUtil.hasPermission(player, Properties.permAdmin), false);
        
        int page = -1;
        
        if (args.length < 3)
            page = 1;
        else if (args.length == 3)
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                throw new MissingOrIncorrectParametersException();
            }
        
        if(page < 1)
            page = 1;
        
        if(page > MTUtil.getNumPages(infoMsg)){
            Messaging.send(cs, "`rNo such page.");
            page = 1;
        }
        
        //Ownership and perms handled by .getInfo()
        String intro = "`Y+-------------- MagicTorches Info (" + page + "/" 
                + MTUtil.getNumPages(infoMsg) + ") --------------+";
        Messaging.send(cs, intro);
        Messaging.mlSend(cs, MTUtil.getListPage(infoMsg, page));
        
        return true;
    }
}
