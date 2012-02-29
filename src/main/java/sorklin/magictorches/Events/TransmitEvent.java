/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sorklin.magictorches.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import sorklin.magictorches.internals.TorchArray;

/**
 *
 * @author Sork
 */
public class TransmitEvent extends Event {
    
    private TorchArray ta;
    private static final HandlerList handlers = new HandlerList();
    private boolean init = false;
	
    public TransmitEvent(TorchArray ta, boolean init) {
        this.ta = ta;
        this.init = init;
    }
    
    public TransmitEvent(TorchArray ta){
        this.ta = ta;
        this.init = false;
        //MagicTorches.spam("Event initialized.");
    }
    
    public TorchArray getTorchArray(){
        return this.ta;
    }
    
    public boolean isInit() {
        return this.init;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
