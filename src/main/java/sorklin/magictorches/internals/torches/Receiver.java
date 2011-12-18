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
package sorklin.magictorches.internals.torches;

import sorklin.magictorches.internals.interfaces.Torch;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import sorklin.magictorches.internals.Properties;

/**
 *
 * @author Sorklin <sorklin at gmail.com>
 */
public class Receiver implements Torch {
    
    protected Location torchLocation;

    public Receiver (Location loc) {
        this.torchLocation = loc;
    }
    
    /**
     * Returns location of Receiver torch.
     * @return Location of torch
     */
    public Location getLocation() {
        return torchLocation;
    }
    
    /**
     * Allows location of the torch to be set after instantiation.
     */
    public void setLocation(Location loc) {
        this.torchLocation = loc;
    }
    
    /**
     * Receives and processes transmitted signal, per receiver type.
     * @param signal transmitted signal.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean receive(boolean signal){ //torch On = true, off = false
        //Return true if I can process signal, else false to indicate
        //something wrong with this torch receiver.
        
        //Lets check for a location and a torch at that location.
        if(this.torchLocation == null)
            return false;
        Block torch = torchLocation.getBlock();
        if(!(torch.getType().equals(Material.TORCH) ||
                torch.getType().equals(Material.REDSTONE_TORCH_ON))) {
            return false;
        }
        
        if(!torch.getChunk().isLoaded()){
            if(!Properties.forceChunkLoad)
                return false;
            else
                torch.getChunk().load();
        }
        
        if(signal){
            torch.setType(Material.TORCH);
        } else {
            torch.setType(Material.REDSTONE_TORCH_ON);
        }
        
        return true;
    }
    
    
    @Override
    public String toString() {
        String result;
        result = this.torchLocation.toString();
        result = result + ":Type{"+ Properties.DIRECT +"}";
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
//        if(!(obj instanceof Receiver))
//            MagicTorches.log(Level.INFO, "Obj not instance of Receiver.");
        try {
            Receiver objTR = (Receiver)obj;
            return (this.torchLocation.equals(objTR.torchLocation));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.torchLocation != null ? this.torchLocation.hashCode() : 0);
        return hash;
    }

    public void teleportTo(Player player) {
        player.sendMessage("Not yet implemented.");
    }
}
