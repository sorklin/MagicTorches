package sorklin.magictorches.internals;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class TorchArray {
    
    //types of torch relationships.
    public static final byte NONE = 0x0;       //0000000
    public static final byte DIRECT = 0x1;     //0000001
    public static final byte INVERSE = 0x2;    //0000010
    public static final byte DELAY  = 0x4;     //0000100
    
    private String arrayName;
    private Location transmitter;
    private List<TorchReceiver> receiverArray = new ArrayList<TorchReceiver>();
    //private ArrayList<Location> receiverArray;
    
    public TorchArray() {
        arrayName = String.valueOf(this.hashCode());
    }
    
    public TorchArray(String savedTorches) {        
        arrayName = String.valueOf(this.hashCode()); //this should change to the saved name.
        //this will convert a properly formed STring to an array of 
        // torchReceivers and transmitter.
        //Make sure that receivers and transmitters exist.
        
        //TODO: code to convert from STring into something usuable.
    }
    
    public void setTransmitter(Location loc){
        this.transmitter = loc;
    }
    
    public void setName(String name){
        this.arrayName = name;
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
    
    public boolean isReceiver(Location loc){
        return receiverArray.contains(new TorchReceiver(loc));
    }
    
    public boolean isTransmitter(Location loc){
        return (transmitter != null) ? this.transmitter.equals(loc) : false;
    }
    
    public boolean transmitterSet(){
        return (transmitter != null);
    }
    
    public boolean receiverSet(){
        return (!this.receiverArray.isEmpty());
    }
    
    public boolean isValid(){
        return (this.transmitter != null && 
                !this.receiverArray.isEmpty() &&
                this.arrayName != null);
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
    
    public boolean transmit(){
        boolean transmitted = false;
        //ListIterator<torchReceiver> tr = receiverArray.listIterator();
        //while(tr.hasNext()) {
            //TODO: implement transmit
            //tr.next().getLocation()
            //If the button is a delay do the setUsed and LastUsed to 
            // deterimine if the torch state should change.
            
            //Make sure it tests for torch at receiver spot.  If not there, 
            // remove from Array.
        //}
        
        return transmitted;
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
    
    public String getName() {
        return this.arrayName;
    }
}
