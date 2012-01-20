package sorklin.magictorches.internals;

import java.util.ArrayList;
import java.util.ListIterator;
import org.bukkit.Location;
import org.bukkit.Material;
import sorklin.magictorches.internals.torches.DelayReceiver;
import sorklin.magictorches.internals.torches.DirectReceiver;
import sorklin.magictorches.internals.torches.InverseReceiver;
import sorklin.magictorches.internals.torches.TimerReceiver;

public class TorchArray {
    
    private String arrayName;
    private Location transmitter;
    private String owner;
    private final ArrayList receiverArray = new ArrayList();
    
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
    public boolean add(Location loc, byte type) {
        if(this.transmitter.equals(loc))
            return false;
        
        DirectReceiver tr = null;
        
        switch(type) {
            case Properties.DELAY:
                tr = new DelayReceiver(loc);
            case Properties.DIRECT:
                tr = new DirectReceiver(loc);
            case Properties.INVERSE:
                tr = new InverseReceiver(loc);
            case Properties.TIMER:
                tr = new TimerReceiver(loc);
        }
        
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
    public ArrayList<? extends DirectReceiver> getReceiverArray(){
        return this.receiverArray;
    }
    
    /**
     * Returns whether a torch is a receiver.
     * @param loc the location of the torch being tested
     * @return <code>true</code> is a receiver, <code>false</code> is not.
     */
    public boolean isReceiver(Location loc){
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
        DirectReceiver torch = new DirectReceiver(loc);
        if (receiverArray.contains(torch)) {
            return receiverArray.remove(torch);
        }
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
    
    @Override
    public String toString() {
        String result = "Name{" + arrayName + "};";
        result = result + ((this.transmitter == null) 
                ? "Transmitter{NULL};" 
                : "Transmitter{" + this.transmitter.toString() + "};");
        if(!receiverArray.isEmpty()) {
            ListIterator<? extends DirectReceiver> it = receiverArray.listIterator();
            while(it.hasNext()) {
                result = result + "Receiver{" + it.next().toString() + "};";
            }
        }
        return result;
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
        
        ListIterator<? extends DirectReceiver> tr = receiverArray.listIterator();
        while(tr.hasNext()) {
            tr.next().receive(current);
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
