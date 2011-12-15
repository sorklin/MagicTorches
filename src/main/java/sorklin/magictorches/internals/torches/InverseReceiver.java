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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import sorklin.magictorches.internals.Properties;

/**
 *
 * @author Sorklin <sorklin at gmail.com>
 */
public class InverseReceiver extends Receiver {
    
    public InverseReceiver (Location loc){
        super(loc);
    }
    
    /**
     * Receives and processes transmitted signal, per receiver type.
     * @param signal transmitted signal.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    @Override
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
        
        if(signal){
            torch.setType(Material.REDSTONE_TORCH_ON);
        } else {
            torch.setType(Material.TORCH);
        }
        
        return true;
    }
    
    
    @Override
    public String toString() {
        String result;
        result = this.torchLocation.toString();
        result = result + ":Type{"+ Properties.INVERSE +"}";
        return result;
    }
}
