package sorklin.magictorches.internals;

import java.util.ArrayList;
import java.util.ListIterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class TorchArray {
    
    //types of torch relationships.
    public static final byte NONE = 0x0;       //0000000
    public static final byte DIRECT = 0x1;     //0000001
    public static final byte INVERSE = 0x2;    //0000010
    public static final byte DELAY  = 0x4;     //0000100
    
    private String arrayName;
    private Location transmitter;
    private String owner;
    private ArrayList<TorchReceiver> receiverArray = new ArrayList<TorchReceiver>();
    
    public TorchArray(String owner) {
        arrayName = String.valueOf(this.hashCode());
        this.owner = owner;
    }
    
    /**
     * Adds a receiver to the array.
     * @param block  the torch being added as receiver.
     * @param type  the type of receiver being added.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean add(Block block, byte type) {
        return add(block.getLocation(), type);
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
        TorchReceiver tr = new TorchReceiver(loc, type);
        return receiverArray.add(tr);
        //return receiverArray.add(loc);
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
    public ArrayList getReceiverArray(){
        return this.receiverArray;
    }
    
    /**
     * Returns whether a torch is a receiver.
     * @param loc the location of the torch being tested
     * @return <code>true</code> is a receiver, <code>false</code> is not.
     */
    public boolean isReceiver(Location loc){
        return receiverArray.contains(new TorchReceiver(loc));
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
     * Removes a receiver (if it exists) at the block.
     * @param block  torch to be removed.
     * @return <code>true</code> removed a receiver, <code>false</code> could 
     * not remove a receiver (either not there or not able to remove).
     */
    public boolean remove(Block block) {
        return remove(block.getLocation());
    }
    
    /**
     * Removes a receiver (if it exists) at the location.
     * @param loc location of torch to be removed.
     * @return <code>true</code> removed a receiver, <code>false</code> could 
     * not remove a receiver (either not there or not able to remove).
     */
    public boolean remove(Location loc) {
        TorchReceiver torch = new TorchReceiver(loc);
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
    
    /**
     * Set the receiver type for the block given. [Unused at this point]
     * @param block torch to change type.
     * @param type type to change receiver to.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean setType(Block block, byte type) {
        return setType(block.getLocation(), type);
    }
    
    /**
     * Set the receiver type for the block given. [Unused at this point]
     * @param loc location of the torch to change type.
     * @param type type to change receiver to.
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean setType(Location loc, byte type) {
        //Not sure this will ever be used.  If not, I'll remove it.
        TorchReceiver torch = new TorchReceiver(loc);
        if(receiverArray.contains(torch)) { 
            receiverArray.get(receiverArray.indexOf(torch)).setType(type);
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        String result = "Name{" + arrayName + "};";
        result = result + ((this.transmitter == null) 
                ? "Transmitter{NULL};" 
                : "Transmitter{" + this.transmitter.toString() + "};");
        if(!receiverArray.isEmpty()) {
            ListIterator it = receiverArray.listIterator();
            while(it.hasNext()) {
                TorchReceiver tr = (TorchReceiver)it.next();
                result = result + "Receiver{" + tr.toString() + "};";
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
//        MagicTorches.spamt("Block: " + transmitter.getBlock().toString());
//        MagicTorches.spamt("[" + arrayName + "] block power: " + transmitter.getBlock().getBlockPower() 
//                + "; isPowered?: " + transmitter.getBlock().isBlockPowered() 
//                + "; indirectly?: " + transmitter.getBlock().isBlockIndirectlyPowered());
        boolean powered = transmitter.getBlock().getType().equals(Material.REDSTONE_TORCH_OFF);
        return transmit(powered);
        //return transmit(transmitter.getBlock().getBlockPower() != 0);
    }
    
    /**
     * Transmit current to all receivers.
     * @param current the current that should be transmitted (i.e., on or off)
     * @return <code>true</code> success, <code>false</code> failure.
     */
    public boolean transmit(boolean current){
        if(transmitter == null) 
            return false;
        Material torch = transmitter.getBlock().getType();
        if(!(torch.equals(Material.REDSTONE_TORCH_ON) 
                || torch.equals(Material.REDSTONE_TORCH_OFF)))
            return false;
        
//        MagicTorches.spamt("[" + arrayName + "] Transmit Current: " + transmitter.getBlock().getBlockPower());
        //Do this to make sure its one or the other (or return false for transmit)
//        MagicTorches.spamt("Transmitting " + current);
        ListIterator<TorchReceiver> tr = receiverArray.listIterator();
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
}
