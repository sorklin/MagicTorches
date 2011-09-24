package sorklin.magictorches.internals;

import org.bukkit.Location;


public class TorchReceiver implements Cloneable {
    
    private Location torchLocation;
    private byte torchType;
    private long lastUsed = 0;

    public TorchReceiver(Location loc) {
        this.torchLocation = loc;
        this.torchType = 0x0; //NONE -- if left undefined == direct?
    }
    
    public TorchReceiver(Location loc, byte type) {
        this.torchLocation = loc;
        this.torchType = type;
    }
    
    public Location getLocation() {
        return torchLocation;
    }
    
    public byte getType() {
        return torchType;
    }
    
    public void setType(byte type) {
        this.torchType = type;
    }
    
    public void setUsed() {
        this.lastUsed = System.currentTimeMillis();
    }
    
    public long lastUsed() {
        return this.lastUsed;
    }
    
    @Override
    public String toString() {
        String result;
        result = this.torchLocation.toString();
        result = result + ";Type{" + String.valueOf(this.torchType) + "}";
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof TorchReceiver)) {
            return false;
        }
        TorchReceiver objTR = (TorchReceiver)obj;
        return (this.torchLocation.equals(objTR.torchLocation));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.torchLocation != null ? this.torchLocation.hashCode() : 0);
        return hash;
    }

    @Override
    public TorchReceiver clone() {
        try {
            TorchReceiver l = (TorchReceiver) super.clone();
            l.torchLocation = torchLocation;
            l.torchType = torchType;
            l.lastUsed = lastUsed;
            return l;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
