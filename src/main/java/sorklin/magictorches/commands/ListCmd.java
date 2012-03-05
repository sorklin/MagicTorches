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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import sorklin.magictorches.Exceptions.InsufficientPermissionsException;
import sorklin.magictorches.Exceptions.MissingOrIncorrectParametersException;
import sorklin.magictorches.internals.MTUtil;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.TorchArray;

public class ListCmd extends GenericCmd {
    
    public ListCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
        mustBePlayer = false;
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException{
        errorCheck();
        
        //command: /mt list [name of player] [page in listing]
        
        int page = -1;
        String playerToList = null; //player name to search for
        boolean isAdmin = MTUtil.isAdmin(cs);
        
        if (args.length < 2)
            page = 1;
        else if (args.length == 2)
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                playerToList = args[1];
                //MagicTorches.spam("nfe (args == 2): " + playerToList);
            }
        else if(args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                //MagicTorches.spam("nfe (args == 3): " + args[1] + ", " + args[2]);
                throw new MissingOrIncorrectParametersException();
            }
            playerToList = args[1];
        } else {
            //MagicTorches.spam("args.length = " + args.length);
            throw new MissingOrIncorrectParametersException();
        }
        
        if(page < 1)
            page = 1;
          
        if(!isAdmin)
            playerToList = player.getName();
        
        List<String> torchList = new ArrayList<String>();
        HashMap<Location, TorchArray> allArrays = mt.mtHandler.getAllArrays();
        
        for(Entry<Location, TorchArray> e : allArrays.entrySet()){
            if(playerToList == null 
                    || e.getValue().getOwner().equalsIgnoreCase(playerToList)){
                TorchArray ta = e.getValue();
                StringBuilder sb = new StringBuilder();
                
                sb.append("`Y").append(ta.getName());
                
                if(isAdmin)
                    sb.append(" `w(").append(ta.getOwner()).append(")");
                
                sb.append(" `a[");
                sb.append(ta.getLocation().getWorld().getName()).append(": ");
                sb.append(ta.getLocation().getBlockX()).append(", ");
                sb.append(ta.getLocation().getBlockY()).append(", ");
                sb.append(ta.getLocation().getBlockZ()).append("]");
                
                torchList.add(sb.toString());
            }
        }
        
        if(torchList.isEmpty())
            torchList.add("`YNo torches found.");
        
        if(page > MTUtil.getNumPages(torchList)){
            Messaging.send(cs, "`rNo such page.");
            page = 1;
        }
        
        String intro = "`gMagicTorches For `w" + ((playerToList == null) ? "Everyone" : playerToList)
                + " `g(Page " + page + " of " + MTUtil.getNumPages(torchList) + ")";
        Messaging.send(cs, intro);
        Messaging.mlSend(cs, MTUtil.getListPage(torchList, page));
        
        return true;
    }
}
