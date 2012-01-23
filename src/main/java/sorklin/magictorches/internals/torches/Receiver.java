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
import org.bukkit.entity.Player;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.interfaces.MTReceiver;


abstract class Receiver implements MTReceiver {
    
    Location torchLocation;
    MtType type;

    public Receiver (Location loc) {
        this.torchLocation = loc;
    }
    
    /**
     * Returns location of DirectReceiver torch.
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
    
    @Override
    public String toString() {
        String result;
        result = this.torchLocation.toString();
        result = result + ":Type{"+ type +"}";
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        try {
            MTReceiver objTR = (MTReceiver)obj;
            return (this.torchLocation.equals(objTR.getLocation()));
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
    
    protected boolean torchInvalid(){
        if(this.torchLocation == null)
            return true;
        
        Material m = this.torchLocation.getBlock().getType();
        return (!(m.equals(Material.TORCH)
                || m.equals(Material.REDSTONE_TORCH_ON)
                || m.equals(Material.REDSTONE_TORCH_OFF)));
    }
}
