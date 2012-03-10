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
import org.bukkit.block.BlockFace;
import sorklin.magictorches.internals.Properties.MtType;

public class InverseReceiver extends Receiver {
    
    public InverseReceiver (Location loc){
        super(loc);
        this.type = MtType.INVERSE;
    }
    
    /**
     * Receives and processes transmitted signal, per receiver type.
     * @param signal transmitted signal.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    @Override
    public boolean receive(boolean signal){ //torch On = true, off = false
        
        //Lets check for a location and a torch at that location.
        if(torchInvalid())
            return false;
        
        Block torch = torchLocation.getBlock();
        
        //Get the current blockface its facing.
        BlockFace facing = getFacing(torchLocation);
        
        //Event must come first.
        sendReceiveEvent();
        if(signal){
            torch.setType(Material.REDSTONE_TORCH_ON);
        } else {
            torch.setType(Material.TORCH);
        }
        if(facing != null)
            torch.setData(getFacingData(facing));
        
        return true;
    }
}
