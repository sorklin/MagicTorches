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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.interfaces.MTReceiver;
import sorklin.magictorches.internals.torches.*;

public class TorchEditor extends TorchArray {
    
    private MtType nextLinkType = MtType.DIRECT;
    private double timerValue = -1;
    private String message = "";
    private boolean created = false;
    private TorchArray original = null;
    
    /**
     * For new Torch arrays (creation)
     * @param player 
     */
    public TorchEditor(Player player){
        super(player.getName());
        created = true;
    }
    
    /**
     * These are wrappers for the regular add function.  They check for 
     * distance and to see if a transmitter has already been set.
     * @param loc
     * @param type
     * @return 
     */
    public boolean addCheck(Location loc, MtType type) {
        return this.addCheck(loc, type, -1);
    }
    
    /**
     * These are wrappers for the regular add function.  They check for 
     * distance and to see if a transmitter has already been set.
     * @param loc
     * @param type
     * @return 
     */
    public boolean addCheck(Location loc, MtType type, double delay) {
        if(!transmitterSet()){
            message = "`rFirst you must designate a transmitter.";
            return false;
        }
        
        if(Properties.useDistance 
                && (loc.distance(this.getLocation()) > Properties.maxDistance)){
            message = "`rThis receiver is too far from the transmitter.";
            return false;
        }
        
        return this.add(loc, type, delay);
    }
    
    public void setNextType(MtType type){
        this.nextLinkType = type;
    }
    
    public void setTimeOut(double timeOut){
        this.timerValue = timeOut;
    }
    
    public MtType getNextType(){
        return this.nextLinkType;
    }

    public double getTimeOut() {
        return this.timerValue;
    }

    public String getMessage() {
        return this.message;
    }
    
    /**
     * Turns all receivers into Redstone torches (for editing)
     */
    public void resetReceivers() {
        for(MTReceiver r : receiverArray){
            r.reset();
        }
    }
    
    public double priceArray() {
        
        double subTotal;
        if(created)
            subTotal = Properties.priceArrayCreate;
        else
            subTotal = Properties.priceArrayEdit;
        
        for(MTReceiver r : receiverArray){
            if(r instanceof DirectReceiver)
                subTotal += Properties.priceDirect;
            else if(r instanceof InverseReceiver)
                subTotal += Properties.priceInverse;
            else if(r instanceof DelayReceiver)
                subTotal += Properties.priceDelay;
            else if(r instanceof TimerReceiver)
                subTotal += Properties.priceTimer;
            else if(r instanceof ToggleReceiver)
                subTotal += Properties.priceToggle;
        }
        return subTotal;
    }
    
    public TorchArray getOriginal(){
        return original;
    }
    
    public void setOriginal(TorchArray original){
        this.original = original;
    }
    
    public boolean isEdited(){
        return !created;
    }
}
