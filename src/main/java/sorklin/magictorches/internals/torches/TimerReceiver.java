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

public class TimerReceiver extends Receiver {
    
    private int delayTask;
    private long delayTicks = 0;
    private double delay;
    
    public TimerReceiver (Location loc){
        super(loc);
        this.type = MtType.TIMER;
        this.delay = -1;
        this.delayTicks = MTUtil.secondsToTicks(Properties.timerDelay);
    }
    
    public TimerReceiver (Location loc, double delay){
        super(loc);
        this.type = MtType.TIMER;
        this.delay = delay;
        this.delayTicks = MTUtil.secondsToTicks(delay);
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
        
        final Block torch = torchLocation.getBlock();
        final Material originalMat = torch.getType();
        MagicTorches mt = MagicTorches.get();
        
        //Check to see if chunk is loaded, and if not, should it be?
        if(!torch.getChunk().isLoaded()){
            if(!Properties.forceChunkLoad)
                return false;
            else
                torch.getChunk().load();
        }
        
        //If the delay is already functioning, ignore the received signal.
        if(!mt.getServer().getScheduler().isCurrentlyRunning(delayTask)){
            
            //Set initial torch signal receive:
            if(originalMat.equals(Material.TORCH)) {
                torch.setType(Material.REDSTONE_TORCH_ON);
            } 

            else if(originalMat.equals(Material.REDSTONE_TORCH_ON)
                    || originalMat.equals(Material.REDSTONE_TORCH_OFF)) {
                torch.setType(Material.TORCH);
            }
            
            //Create the timed task to flip it back:
            this.delayTask = mt.getServer().getScheduler().scheduleSyncDelayedTask(mt, new Runnable() {
                public void run() {
                    torch.setType(originalMat);
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
        result += ":Delay{" + this.delayTicks + "}";
        return result;
    }
}
