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
import sorklin.magictorches.Exceptions.InsufficientPermissionsException;
import sorklin.magictorches.Exceptions.MissingOrIncorrectParametersException;
import sorklin.magictorches.internals.MTUtil;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.Properties;

public class InstructionsCmd extends GenericCmd {
    
    /*Default the generic to must be executed by a player, and no minimum arguments.
    String permission = "";
    boolean mustBePlayer = true;
    int minArg = 0;
    */
    
    public InstructionsCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
        mustBePlayer = false;
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException{
        errorCheck();
        
        List<String> helpMsg = initializeInstructions();
        int page = -1;
        
        if (args.length < 2)
            page = 1;
        else if (args.length == 2)
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                throw new MissingOrIncorrectParametersException();
            }
        else 
            throw new MissingOrIncorrectParametersException();
        
        if(page < 1)
            page = 1;
        
        if(page > MTUtil.getNumPages(helpMsg)){
            Messaging.send(cs, "`rNo such page.");
            page = 1;
        }
        
        String intro = "`gMagicTorches Instructions (Page " + page + " of " + MTUtil.getNumPages(helpMsg) + ")";
        Messaging.send(cs, intro);
        Messaging.mlSend(cs, MTUtil.getListPage(helpMsg, page));

        return true;
    }
    
    public List<String> initializeInstructions(){
        List<String> help = new ArrayList<String>();
        boolean canCreate = MTUtil.hasPermission(cs, Properties.permCreateDelay) 
                || MTUtil.hasPermission(cs, Properties.permCreateDirect)
                || MTUtil.hasPermission(cs, Properties.permCreateInverse)
                || MTUtil.hasPermission(cs, Properties.permCreateTimer)
                || MTUtil.hasPermission(cs, Properties.permCreateToggle);
        
        if(canCreate){
            help.add("`YBasic Instructions:");
            help.add("To create a magic torch array, set up the transmitter and receiver torches");
            help.add("");
            help.add("");
            help.add("");
        }
        
        
        return help;
    }
}
