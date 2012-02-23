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
import sorklin.magictorches.internals.Properties;

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
        
        /*
         * Can finish two things: creation and editing.  Handle both (should be similar).
         * But editing won't require an array name (since its already established).
         */
        
//        if(cmd.equalsIgnoreCase("finish")) {
//            //Perms handled by that command handler
//            ArrayList<String> argArray = new ArrayList<String>();
//            for(int i=1, length = args.length; i < length; i++){
//                argArray.add(args[i]);
//            }
//            String[] a = new String[argArray.size()];
//            a = argArray.toArray(a);
//            return finish(sender, a);
//        } else
   
//    public boolean finish(CommandSender sender, String[] args){
//        if(!MagicTorches.canCreate(sender)){
//            return true;
//        }
//        
//        Player player = (Player)sender;
//        if(!pl.mt.isInEditMode(player)) {
//            sender.sendMessage(pl.r + "You are not in edit mode. Type /mtcreate to begin.");
//            return false;
//        }
//        
//        if(args.length != 1) {
//            sender.sendMessage(pl.r + "Incorrect number of arguments.");
//            return false;
//        }
//
//        if(pl.mt.create(player, args[0])) {
//            sender.sendMessage(pl.g + "Successfully created MagicTorch array: " + pl.b + pl.mt.message);
//            pl.mt.setEditMode(player, false);
//        } else {
//            sender.sendMessage(pl.r + pl.mt.message);
//        }
//        return true;
        
        return true;
    }
}
