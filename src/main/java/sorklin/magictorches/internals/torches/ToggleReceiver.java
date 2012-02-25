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
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.MTUtil;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.Properties.MtType;

public class ToggleReceiver extends Receiver {
    
    private long delayTicks = 0;
    private double delay;
    private boolean isRunning = false;
    
    public ToggleReceiver (Location loc){
        super(loc);
        this.type = MtType.TOGGLE;
        this.delay = -1;
        this.delayTicks = MTUtil.secondsToTicks(Properties.toggleDelay);
    }
    
    public ToggleReceiver (Location loc, double delay){
        super(loc);
        this.type = MtType.TOGGLE;
        this.delay = delay;
        if(delay < 0)
            this.delayTicks = MTUtil.secondsToTicks(Properties.toggleDelay);
        else
            this.delayTicks = MTUtil.secondsToTicks(delay);
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
        if(torchInvalid())
            return false;
        
        final Block torch = torchLocation.getBlock();
        final Material originalMat = torch.getType();
        MagicTorches mt = MagicTorches.get();
        
        if(!torch.getChunk().isLoaded())
            if(!Properties.forceChunkLoad)
                return false;
            else
                torch.getChunk().load();
        
        //If the delay is already functioning, ignore the received signal.
        if(!isRunning){
            
            if(torch.getType().equals(Material.TORCH)) {
                torch.setType(Material.REDSTONE_TORCH_ON);
            } else

            if(torch.getType().equals(Material.REDSTONE_TORCH_ON)
                    || torch.getType().equals(Material.REDSTONE_TORCH_OFF)) {
                torch.setType(Material.TORCH);
            }
            
            isRunning = true;
            
            mt.getServer().getScheduler().scheduleSyncDelayedTask(mt, new Runnable() {
                public void run() {
                    isRunning = false;
                }
            }, delayTicks);
        }
        return true;
    }
    
    /**
     * Sets the delay time for the torch.
     * @param delay Time in seconds.
     */
    public void setDelay(double delay){
        this.delayTicks = MTUtil.secondsToTicks(delay);
    }

    @Override
    public String toString() {
        String result = super.toString();
        result += ":Delay{" + this.delay + "}";
        return result;
    }
}
