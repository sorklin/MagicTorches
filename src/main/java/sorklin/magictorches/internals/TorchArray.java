package sorklin.magictorches.internals;

import java.util.ArrayList;
import java.util.ListIterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import sorklin.magictorches.MagicTorches;

public class TorchArray {
    
    //types of torch relationships.
    public static final byte NONE = 0x0;       //0000000
    public static final byte DIRECT = 0x1;     //0000001
    public static final byte INVERSE = 0x2;    //0000010
    public static final byte DELAY  = 0x4;     //0000100
    
    private String arrayName;
    private Location transmitter;
    private ArrayList<TorchReceiver> receiverArray = new ArrayList<TorchReceiver>();
    
    public TorchArray() {
        arrayName = String.valueOf(this.hashCode());
    }
    
    public boolean add(Block block, byte type) {
        return add(block.getLocation(), type);
    }
    
    public boolean add(Location loc, byte type) {
        TorchReceiver tr = new TorchReceiver(loc, type);
        return receiverArray.add(tr);
        //return receiverArray.add(loc);
    }
    
    public Location getLocation(){
        return this.transmitter;
    }
    
    public String getName() {
        return this.arrayName;
    }
    
    public ArrayList getReceiverArray(){
        ArrayList<Location> result = new ArrayList<Location>(receiverArray.size());
        ListIterator<TorchReceiver> tr = receiverArray.listIterator();
        while(tr.hasNext()) {
            result.add(tr.next().getLocation());
        }
        return result;
    }
    
    public boolean isReceiver(Location loc){
        return receiverArray.contains(new TorchReceiver(loc));
    }
    
    public boolean isTransmitter(Location loc){
        return (transmitter != null) ? this.transmitter.equals(loc) : false;
    }
    
    public boolean isValid(){
        return (this.transmitter != null && 
                !this.receiverArray.isEmpty() &&
                this.arrayName != null);
    }
    
    public boolean receiverSet(){
        return (!this.receiverArray.isEmpty());
    }
    
    public boolean remove(Block block) {
        return remove(block.getLocation());
    }
    
    public boolean remove(Location loc) {
        TorchReceiver torch = new TorchReceiver(loc);
        if (receiverArray.contains(torch)) {
            return receiverArray.remove(torch);
        }
        return false;
    }
    
    public void setName(String name){
        this.arrayName = name;
    }
    
    public void setTransmitter(Location loc){
        this.transmitter = loc;
    }
    
    public boolean setType(Block block, byte type) {
        return setType(block.getLocation(), type);
    }
    
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
    
    public boolean transmit(){
        if(transmitter == null) 
            return false;
//        MagicTorches.spamt("block power: " + transmitter.getBlock().getBlockPower() 
//                + "; isPowered?: " + transmitter.getBlock().isBlockPowered() 
//                + "; indirectly?: " + transmitter.getBlock().isBlockIndirectlyPowered());
        return transmit(transmitter.getBlock().getBlockPower() != 0);
    }
    
    public boolean transmit(boolean current){
        if(transmitter == null) 
            return false;
        Material torch = transmitter.getBlock().getType();
        if(!(torch.equals(Material.REDSTONE_TORCH_ON) 
                || torch.equals(Material.REDSTONE_TORCH_OFF)))
            return false;
        
        //MagicTorches.spamt("Transmit Current: " + transmitter.getBlock().getBlockPower());
        //Do this to make sure its one or the other (or return false for transmit)
        //MagicTorches.spamt("Transmitting " + current);
        ListIterator<TorchReceiver> tr = receiverArray.listIterator();
        while(tr.hasNext()) {
            tr.next().receive(current);
        }
        return true;
    }
    
    public boolean transmitterSet(){
        return (transmitter != null);
    }
}
