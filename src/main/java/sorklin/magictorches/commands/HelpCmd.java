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

public class HelpCmd extends GenericCmd {
    
    
    public HelpCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
        mustBePlayer = false;
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException{
        errorCheck();
        
        List<String> helpMsg = initializeHelp();
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
        
        String intro = "`gMagicTorches Help (Page " + page + " of " + MTUtil.getNumPages(helpMsg) + ")";
        Messaging.send(cs, intro);
        Messaging.mlSend(cs, MTUtil.getListPage(helpMsg, page));

        return true;
    }
    
    public List<String> initializeHelp(){
        List<String> help = new ArrayList<String>();
        boolean canCreate = MTUtil.hasPermission(cs, Properties.permCreateDelay) 
                || MTUtil.hasPermission(cs, Properties.permCreateDirect)
                || MTUtil.hasPermission(cs, Properties.permCreateInverse)
                || MTUtil.hasPermission(cs, Properties.permCreateTimer)
                || MTUtil.hasPermission(cs, Properties.permCreateToggle);
        
        if(canCreate){
            help.add("`g/mt instructions `w- Show basic torch creation instructions.");
            help.add("`g/mt create `s<name> [next receiver type] `w- Creates a MagicTorch ");
            help.add("array named `s<name>`w.  `s[next receiver type]`w can be Direct,");
            help.add("Inverse, Toggle, Delay, or Timer.  Default is Direct.");
            help.add("`g/mt cancel `w- Cancels torch creating or editing.");
            help.add("`g/mt finish `w- Finishes torch creating or editing.");
        }
        
        if(MTUtil.hasPermission(cs, Properties.permCreateDirect)){
            help.add("`g/mt direct `w- Sets the next added receiver torches to be");
            help.add("a direct receiver.");
        }
        
        if(MTUtil.hasPermission(cs, Properties.permCreateInverse)){
            help.add("`g/mt inverse `w- Sets the next added receiver torches to be");
            help.add("an inverse receiver.");
        }
        
        if(MTUtil.hasPermission(cs, Properties.permCreateToggle)){
            help.add("`g/mt toggle `s[time] `w- Sets the next added receiver torches to be");
            help.add("a toggle receiver. `s[time] `wis the amount of time to wait");
            help.add("before accepting another signal. Default is `a" + Properties.toggleDelay + " `wseconds.");
        }
        
        if(MTUtil.hasPermission(cs, Properties.permCreateDelay)){
            help.add("`g/mt delay `s[time] `w- Sets the next added receiver torches to be");
            help.add("a delay receiver. `s[time] `wis the amount of time to wait");
            help.add("before processing the signals. Default is `a" + Properties.delayDelay + " `wseconds.");
        }
        
        if(MTUtil.hasPermission(cs, Properties.permCreateTimer)){
            help.add("`g/mt timer `s[time] `w- Sets the next added receiver torches to be");
            help.add("a timer receiver. `s[time] `wis the amount of time to wait");
            help.add("before switching back to the initial state. Default is `a" + Properties.timerDelay + " `wseconds.");
        }
        
        if(canCreate){
            help.add("`g/mt edit `s<name> `w- Edits the named torch array.");
        }
        
        if(MTUtil.isAdmin(cs)){
            help.add("`g/mt list `s[player] [page] `w- Lists the torch arrays for");
            help.add("everyone, or a `s[player] `wyou specify.  `s[page] is the ");
            help.add("page of the listing.");
        } else {
            help.add("`g/mt list [page] `w- Lists the torch arrays that you");
            help.add("own.`s[page] specifies the page of the listing.");
        }
        
        help.add("`g/mt delete <name> `w- Delete the named torch array.");
        help.add("`g/mt info <name> `w- Shows info for the named torch array.");
        
        if(Properties.useEconomy){
            help.add("`g/mt price `w- Show the current price for the array you are creating or editing.");
            help.add("`g/mt rate `w- Show the current rates for creating Torch Arrays.");
        }
        
        if(MTUtil.isAdmin(cs)){
            help.add("`g/mt enable `w- Reenable all loaded MT Torch arrays, after they have been disabled.");
            help.add("`g/mt disable `w- Disable all loaded MT Torch arrays.");
            help.add("`g/mt prune `w- Delete all non-loaded torch arrays.");
            help.add("`g/mt reload `w- Reloads all torch arrays from db.");
        }
        
        return help;
    }
}
