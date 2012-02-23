package sorklin.magictorches.internals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import org.bukkit.Location;
import org.bukkit.Material;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.interfaces.MTReceiver;
import sorklin.magictorches.internals.torches.*;

public class TorchArray {
    
    private String arrayName;
    private Location transmitter;
    private String owner;
    final ArrayList<MTReceiver> receiverArray = new ArrayList<MTReceiver>();
    
    public TorchArray(String owner) {
        arrayName = String.valueOf(this.hashCode());
        this.owner = owner;
    }
    
    /**
     * Adds a receiver to the array.
     * @param loc  location of the torch being added as receiver.
     * @param type  the type of receiver being added.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean add(Location loc, MtType type) {
        return this.add(loc, type, -1);
    }
    
    /**
     * Adds a receiver to the array.
     * @param loc  location of the torch being added as receiver.
     * @param type  the type of receiver being added.
     * @param delay the length of delay for the time-based receivers.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean add(Location loc, MtType type, double delay) {
        if(this.transmitter.equals(loc))
            return false;
        
        MTReceiver tr;
        
        switch(type) {
            case DIRECT:
                tr = new DirectReceiver(loc);
                break;
            case DELAY:
                if(delay < 0)
                    tr = new DelayReceiver(loc);
                else
                    tr = new DelayReceiver(loc, delay);
                break;
            case INVERSE:
                tr = new InverseReceiver(loc);
                break;
            case TIMER:
                if(delay < 0)
                    tr = new TimerReceiver(loc);
                else
                    tr = new TimerReceiver(loc, delay);
                break;
            case TOGGLE:
                if(delay < 0)
                    tr = new ToggleReceiver(loc);
                else
                    tr = new ToggleReceiver(loc, delay);
                break;
            default:
                tr = new DirectReceiver(loc);
        }
        
        //Try to set the parent's location
        if(transmitterSet())
            tr.setParent(transmitter);
        
        return this.receiverArray.add(tr);
    }
    
    /**
     * Gets location of Array's transmitter.
     * @return Location of the transmitter.
     */
    public Location getLocation(){
        return this.transmitter;
    }
    
    /**
     * Gets the name of the Array.
     * @return String of the Array's name.
     */
    public String getName() {
        return this.arrayName;
    }
    
    /**
     * Gets the name of the Array's owner.
     * @return (String) owner
     */
    public String getOwner(){
        return this.owner;
    }
    
    /**
     * Returns a list of all Receivers in the array.
     * @return ArrayList containing TR.
     */
    public ArrayList<MTReceiver> getReceiverArray(){
        return this.receiverArray;
    }
    
    /**
     * Returns whether a torch is a receiver.
     * @param loc the location of the torch being tested
     * @return <code>true</code> is a receiver, <code>false</code> is not.
     */
    public boolean isReceiver(Location loc){
        //Test to make sure this actually returns something for non-direct receivers
        return receiverArray.contains(new DirectReceiver(loc));
    }
    
    /**
     * Returns whether a torch is a transmitter.
     * @param loc the location of the torch being tested
     * @return <code>true</code> is a transmitter, <code>false</code> is not.
     */
    public boolean isTransmitter(Location loc){
        return (transmitter != null) ? this.transmitter.equals(loc) : false;
    }
    
    /**
     * Returns whether the current TorchArray is valid (i.e. contains a transmitter,
     * name, and at least one receiver).
     * @return <code>true</code> is valid, <code>false</code> is not.
     */
    public boolean isValid(){
        return (this.transmitter != null && 
                !this.receiverArray.isEmpty() &&
                this.arrayName != null);
    }
    
    /**
     * Returns the reasons why a TorchArray is invalid in Messaging format.
     * @return 
     */
    public String getInvalidReason() {
        String msg = "";
        if(isValid())
            msg = "This torch array is valid.";
        if(this.transmitter == null)
            msg = "No transmitter has been set.%cr%";
        if(this.receiverArray.isEmpty())
            msg += "No receivers have been set up.%cr%";
        if(this.arrayName == null)
            msg += "The torch array has not been named.";
        return msg;
    }
    
    /**
     * Returns whether at least one receiver is set.
     * @return <code>true</code> at least one is set, <code>false</code> no receivers.
     */
    public boolean receiverSet(){
        return (!this.receiverArray.isEmpty());
    }
    
    /**
     * Removes a receiver (if it exists) at the location. 
     * Note: Does not remove from DB. That must be done by the handler.
     * @param loc location of torch to be removed.
     * @return <code>true</code> removed a receiver, <code>false</code> could 
     * not remove a receiver (either not there or not able to remove).
     */
    public boolean remove(Location loc) {
        MTReceiver torch = new DirectReceiver(loc);
        if (receiverArray.contains(torch)) {
            return receiverArray.remove(torch);
        } else
            return false;
    }
    
    /**
     * Sets the name of the TorchArray.
     * @param name name to use.
     */
    public void setName(String name){
        this.arrayName = name;
    }
    
    /**
     * Sets the name of the TorchArray's owner.
     * @param owner the owner of the array.
     */
    public void setOwner(String owner){
        this.owner = owner;
    }
    
    /**
     * Sets the transmitter for the TorchArray.
     * @param loc the location of the torch to use as transmitter.
     */
    public void setTransmitter(Location loc){
        this.transmitter = loc;
        if(isReceiver(loc))
            remove(loc);
    }
    
    /**
     * Resets the parent transmitters for all receivers in the array.  This will allow
     * for the transmitter to change as needed.
     * @return 
     */
    public void setReceiverParents(){
        if(transmitterSet()){
            for (Iterator<MTReceiver> it = receiverArray.iterator(); it.hasNext();) {
                MTReceiver r = it.next();
                r.setParent(transmitter);
            }
        }
    }
    
    @Override
    public String toString() {
        String result = "Name{" + arrayName + "};";
        result = result + ((this.transmitter == null) 
                ? "Transmitter{NULL};" 
                : "Transmitter{" + this.transmitter.toString() + "};");
        if(!receiverArray.isEmpty()) {
            ListIterator<MTReceiver> it = receiverArray.listIterator();
            while(it.hasNext()) {
                result = result + "Receiver{" + it.next().toString() + "};";
            }
        }
        return result;
    }
    
    /**
     * Initialize the torches.  Basically, sends a transmit to all direct and 
     * inverse torches, but ignores the other types (leaving them in their saved state).
     * @return 
     */
    public boolean init(){
        if(transmitter == null)
            return false;
        
        boolean current = isNotPowered(transmitter.getBlock().getType());
        
        for (Iterator<MTReceiver> it = receiverArray.iterator(); it.hasNext();) {
            MTReceiver r = it.next();
            if(r instanceof DirectReceiver || r instanceof InverseReceiver)
                r.receive(current);
        }
        
        return true;
    }
    
    /**
     * Transmit torch state to all receivers.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean transmit(){
        if(transmitter == null) 
            return false;
        return transmit(isNotPowered(transmitter.getBlock().getType()));
    }
    
    /**
     * Transmit current to all receivers.
     * @param current the current that should be transmitted (i.e., on or off)
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean transmit(boolean current){
        if(transmitter == null) 
            return false;
        
        if(isNotPowered(transmitter.getBlock().getType()))
            return false;
        
        for (Iterator<MTReceiver> it = receiverArray.iterator(); it.hasNext();) {
            MTReceiver r = it.next();
            r.receive(current);
        }
        
        return true;
    }
    
    /**
     * Returns whether a transmitter has been set for this array.
     * @return <code>true</code> is set, <code>false</code> is not set.
     */
    public boolean transmitterSet(){
        return (transmitter != null);
    }
    
    /**
     * Returns true if the Torch is not powered.
     * @param mat Material of torch.
     */
    private boolean isNotPowered(Material mat){
        //Will return true if material is anything but a lit redstone torch
        //While i'm not crazy about this, we're pretty sure it'll always be a torch
        //there.
        return (!(mat.equals(Material.REDSTONE_TORCH_ON)));
    }
}
