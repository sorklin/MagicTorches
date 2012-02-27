package sorklin.magictorches.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sorklin.magictorches.Exceptions.InsufficientPermissionsException;
import sorklin.magictorches.Exceptions.MissingOrIncorrectParametersException;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.MTUtil;
import sorklin.magictorches.internals.interfaces.Cmd;

/**
* Copyright (C) 2011 Sorklin <sorklin@gmail.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
abstract class GenericCmd implements Cmd {
    
    CommandSender cs; //Always populated
    String[] args; //Original Args from CommandListener
    Player player; //Only populated if cs is a player.  Only throws an error if mustBeAPlayer is true.
    
    //Default the generic to must be executed by a player, and no minimum arguments.
    String permission = "";
    boolean mustBePlayer = true;
    int minArg = 0;
    
    MagicTorches mt;
    
    public GenericCmd(CommandSender cs, String args[]){
        this.cs = cs;
        this.args = args;
        this.mt = MagicTorches.get();
    }
    
    protected void errorCheck() throws MissingOrIncorrectParametersException, InsufficientPermissionsException {
        
        //Try to cast it and only throw a problem if command must be executed as player.
        try {
            this.player = (Player)cs;
        } catch (Exception ex) {
            if(mustBePlayer)
                throw new InsufficientPermissionsException("This command must be executed as a player.");
        }
        
        if(!(MTUtil.hasPermission(cs, permission) || MTUtil.isAdmin(cs)))
            throw new InsufficientPermissionsException();
        
        if(args.length < minArg)
            throw new MissingOrIncorrectParametersException("The command is missing required arguments.");
    }
}
