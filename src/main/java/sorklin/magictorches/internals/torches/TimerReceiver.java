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
import sorklin.magictorches.internals.Properties;

public class TimerReceiver extends Receiver {
    
    private int delayTask;
    private long delayTime = 0;
    
    public TimerReceiver (Location loc){
        super(loc);
        this.type = Properties.TIMER;
        this.delayTime = Properties.toMillis(Properties.timerDelay);
    }
    
    public TimerReceiver (Location loc, double delay){
        super(loc);
        this.type = Properties.TIMER;
        this.delayTime = Properties.toMillis(delay);
    }
    
    /**
     * Receives and processes transmitted signal, per receiver type.
     * @param signal transmitted signal.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    @Override
    public boolean receive(boolean signal){ //torch On = true, off = false
        
        //Lets check for a location and a torch at that location.
        if(this.torchLocation == null)
            return false;
        
        final Block torch = torchLocation.getBlock();
        final Material originalMat = torch.getType();
        
        if(!(originalMat.equals(Material.TORCH) ||
                originalMat.equals(Material.REDSTONE_TORCH_ON))) {
            return false;
        }
        
        if(!torch.getChunk().isLoaded()){
            if(!Properties.forceChunkLoad)
                return false;
            else
                torch.getChunk().load();
        }
       
        MagicTorches mt = MagicTorches.getPluginInstance();
        
        if(originalMat.equals(Material.TORCH)) {
            torch.setType(Material.REDSTONE_TORCH_ON);
        } else

        if(originalMat.equals(Material.REDSTONE_TORCH_ON)
                || originalMat.equals(Material.REDSTONE_TORCH_OFF)) {
            torch.setType(Material.TORCH);
        }
        
        //If the delay is already functioning, ignore the received signal.
        if(!mt.getServer().getScheduler().isCurrentlyRunning(delayTask)){
            this.delayTask = mt.getServer().getScheduler().scheduleSyncDelayedTask(mt, new Runnable() {
                public void run() {
                    torch.setType(originalMat);
                }
            }, delayTime);
        }
        
        return true;
    }
    
    /**
     * Sets the delay time for the torch.
     * @param delay Time in seconds.
     */
    public void setDelay(double delay){
        this.delayTime = Properties.toMillis(delay);
    }
}
