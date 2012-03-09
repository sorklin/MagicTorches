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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.interfaces.MTReceiver;
import sorklin.magictorches.internals.torches.*;

public class TorchEditor extends TorchArray {
    
    private MtType nextLinkType = MtType.DIRECT;
    private double timerValue = -1;
    private String message = "";
    private TorchArray original = null;
    
    /**
     * For new Torch arrays (creation)
     * @param player 
     */
    public TorchEditor(Player player){
        super(player.getName());
    }
    
    public TorchEditor(TorchArray ta){
        //This is for editing.
        super(ta.getOwner());
        this.receiverArray.addAll(ta.receiverArray);
        setTransmitter(ta.getLocation());
        setName(ta.getName());
        this.original = ta;
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
    
    public double priceArray() {
        
        double subTotal;

        if(original == null){
            subTotal = Properties.priceArrayCreate;
            subTotal += priceReceivers(receiverArray);
        } else {
            subTotal = Properties.priceArrayEdit;
            double newReceiverCost = (priceReceivers(receiverArray) - priceReceivers(original.receiverArray));
            subTotal += (newReceiverCost >= 0) ? newReceiverCost : 0;
        }
        
        return subTotal;
    }
    
    public TorchArray getOriginal(){
        return original;
    }
    
    public boolean isEdited(){
        return (original != null);
    }
    
    private double priceReceivers(ArrayList<MTReceiver> receivers){
        double subTotal = 0;
        
        for(MTReceiver r : receivers){
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
    
    /**
     * Duplicated from SimpleTorchHandler for the purposes of getting info on 
     * Created or edited torches.
     */
    public List<String> getInfo(Location loc){
        List<String> result = new ArrayList<String>();
        String sb;
        
        if(loc.equals(this.getLocation()))
                result.add("`YTransmitter Torch for current array.");
        else if (isReceiver(loc)) {
            for(MTReceiver tr : receiverArray)
                if(tr.getLocation().equals(loc)){
                    result.add("`YReceiver: `a" + getReceiverInfo(tr) + "`Y.");
                    result.add("`YIts transmitter is at `a[" + tr.getParent().getWorld().getName() + ": " 
                            + tr.getParent().getX() + ", " +
                            tr.getParent().getY() + ", " + 
                            tr.getParent().getZ() + "]`Y .");
                }
        } else {
            result.add("`rThis is not a MagicTorch in the array currently being created or edited.");
        }
        
        return result;
    }

    /**
     * Duplicated from SimpleTorchHandler for the purposes of getting info on 
     * Created or edited torches.
     */
    private String getReceiverInfo(MTReceiver tr){
        StringBuilder sb = new StringBuilder();
        
        if(tr instanceof DirectReceiver)
            sb.append("Direct");
        else if(tr instanceof DelayReceiver)
            sb.append("Delay");
        else if(tr instanceof InverseReceiver)
            sb.append("Inverse");
        else if(tr instanceof ToggleReceiver)
            sb.append("Toggle");
        else if(tr instanceof TimerReceiver)
            sb.append("Timer");
        else 
            sb.append("Unknown");
        
        if(tr instanceof DelayReceiver || tr instanceof ToggleReceiver || tr instanceof TimerReceiver)
            sb.append(" (").append(tr.getDelay()).append("s)");
        
        sb.append(" receiver at ");
        sb.append("[").append(tr.getLocation().getWorld().getName()).append(": ");
        sb.append(tr.getLocation().getBlockX()).append(", ");
        sb.append(tr.getLocation().getBlockY()).append(", ");
        sb.append(tr.getLocation().getBlockZ()).append("]");
        
        return sb.toString();
    }
}
