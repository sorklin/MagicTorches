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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Torch;
import sorklin.magictorches.Events.RecieveEvent;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.interfaces.MTReceiver;


abstract class Receiver implements MTReceiver {
    
    Location torchLocation;
    Location parentLocation;
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
    
    /**
     * Allows parent location to be set after instantiation.
     * @return 
     */
    public void setParent(Location parent) {
        this.parentLocation = parent;
    }
    
    /**
     * Get parent's location
     * @return Location of Parent transmitter.
     */
    public Location getParent(){
        return this.parentLocation;
    }
    
    public double getDelay(){
        return 0;
    }
    
    public void reset(){        
        //Make sure this is a valid receiver
        if(torchInvalid())
            return;
        
        //Now that we know its a torch, lets just set it to redstone_on
        BlockFace facing = getFacing(torchLocation);
        torchLocation.getBlock().setType(Material.REDSTONE_TORCH_ON);
        torchLocation.getBlock().setData(getFacingData(facing));
    }
    
    @Override
    public String toString() {
        String result;
        result = this.torchLocation.toString();
        result = result + ":Type{"+ type.getType() +"}";
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
    
    protected void sendReceiveEvent(){
        Bukkit.getServer().getPluginManager().callEvent(new RecieveEvent(torchLocation));
    }
    
    protected BlockFace getFacing(Location l){
        Block b = l.getBlock();
        try{
            return ((Torch)b.getType().getNewData(b.getData())).getFacing();
        } catch (Exception ex) {
            return null;
        }
    }
    
    protected byte getFacingData (BlockFace bf){
        
        byte data;
        switch (bf) {
            case SOUTH:
                data = 0x1;
                break;

            case NORTH:
                data = 0x2;
                break;

            case WEST:
                data = 0x3;
                break;

            case EAST:
                data = 0x4;
                break;

            case UP:
            default:
                data = 0x5;
        }
        return data;
    }
}
