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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import sorklin.magictorches.Exceptions.InsufficientPermissionsException;
import sorklin.magictorches.Exceptions.MissingOrIncorrectParametersException;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.TorchEditor;
import sorklin.magictorches.internals.TransmitEvent;

public class FinishCmd extends GenericCmd {
    
    /*Default the generic to must be executed by a player, and no minimum arguments.
    String permission = "";
    boolean mustBePlayer = true;
    int minArg = 0;
    */
    
    public FinishCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAccess;
    }
    
    public boolean execute() throws MissingOrIncorrectParametersException, InsufficientPermissionsException{
        errorCheck();
        
        //Is there something being edited/created
        if(!mt.editQueue.containsKey(player))
            throw new MissingOrIncorrectParametersException("You are not creating or editing a torch array.");
        
        TorchEditor te = mt.editQueue.get(player);
        
        //Is it valid?
        if(!mt.editQueue.get(player).isValid())
            throw new MissingOrIncorrectParametersException(te.getInvalidReason());
        
        double price = te.priceArray();
        String name = player.getName();
        
        //Can the player afford it?
        if(Properties.useEconomy){
            if(MagicTorches.econ.has(name, price))
                MagicTorches.econ.withdrawPlayer(name, price);
            else
                throw new MissingOrIncorrectParametersException("You do not have enough money to buy create/edit this array (" 
                        + MagicTorches.econ.format(price) + ")");
        }
        
        //put it in the hashmap and save it.
        mt.mtHandler.addArray(te);
        MagicTorches.getMiniDB().save(te);
        mt.editQueue.remove(player); //remove from editing queue.
        
        Bukkit.getServer().getPluginManager().callEvent(new TransmitEvent(te, true));
        
        Messaging.send(player, "`gFinished the `w" + te.getName() + " `garray" 
                + ((Properties.useEconomy) 
                ? (", for " + MagicTorches.econ.format(price))+ "." 
                : "."));
        
        return true;
    }
}
