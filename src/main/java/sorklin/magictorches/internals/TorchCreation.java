/*
 * Copyright (C) 2011 Sorklin <sorklin at gmail.com>
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
package sorklin.magictorches.internals;

import org.bukkit.entity.Player;

/**
 *
 * @author Sorklin <sorklin at gmail.com>
 */
public class TorchCreation {
    
    private Player player = null;
    private TorchArray ta = null;
    private byte nextLinkType = 0x0;
    
    public String message = null;
    
    public TorchCreation(Player player){
        //I'm not sure what gets passed in.
    }
    
    public void setPlayer(Player player){
        this.player = player;
    }
    
    
    /**
     * Creates a MagicTorch array from the previously input Transmitter and Receiver
     * selections.
     * @param player Player object of the person creating the array (the owner).
     * @param name The name of the array.
     * @return <code>true</code> if the Array was created, <code>false</code> if it was invalid.
     */
    public boolean create(){
        if(Properties.db.isTorchArray(ta.getName().toLowerCase())){
            this.message = "A MagicTorch Array of that name already exists.";
            return false;
        }
        
        if(player != null){
            if(ta.isValid()) {
                if(Properties.db.save(ta)) {
                    return true;
                } else {
                    this.message = "Failed to save MagicTorch array.";
                }
            } else {
                this.message = "MagicTorch array not valid.";
                if(!ta.transmitterSet())
                    this.message = this.message + " [transmitter not selected]";
                if(!ta.receiverSet())
                    this.message = this.message + " [receivers not selected]";
            }
        }
        return false;
    }
}
