package sorklin.magictorches.internals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sorklin.magictorches.MagicTorches;

public final class MTorch {
    
    //transmitter to TorchArray:
    private Map<Location, TorchArray> mtArray = new HashMap<Location, TorchArray>();
    
    //These three are for magic creation by different players (simultaneously).
    private Map<Player, TorchArray> plTArray = new HashMap<Player, TorchArray>();
    private Map<Player, Boolean> plEditMode = new HashMap<Player, Boolean>();
    private Map<Player, Byte> plNextLinkType = new HashMap<Player, Byte>(); //the next link type

    
//    private Mini mb_database;
    private MagicTorches pl;
    private int defaultCooldown;
    private File miniDB;
    
    public String message = "";
    
    public MTorch (MagicTorches instance) {
        //TODO: Remove this when i hve implemented db
        miniDB = null;
        //mb_database = new Mini(miniDB.getParent(), miniDB.getName());
        pl = instance;
        //reload(); //give it a shot if we're not in MV.
    }
    
    public MTorch (File db, MagicTorches instance) {
        miniDB = db;
        //mb_database = new Mini(miniDB.getParent(), miniDB.getName());
        pl = instance;
        //reload(); //give it a shot if we're not in MV.
    }
    
    public boolean reload(){
        //Loads from MiniDB or other source
        //TODO: implement reload
        return true;
    }
    
    public void close() {
        //This closes the class by saving an updated TorchArray to DB.
        //TODO: implement close
    }
    
    public boolean create(Player player, String name){
        if(plTArray.containsKey(player)) {
            plTArray.get(player).setName(name.toLowerCase().trim());
            if(plTArray.get(player).isValid()) {
                if(saveToDB(plTArray.get(player))) {
                    this.message = "Successfully created MagicTorch array: " 
                            + name.toLowerCase().trim();
                    reload(); //reloads the mt array from file.
                    return true;
                } else {
                    this.message = "Failed to create MagicTorch array.";
                }
            } else {
                this.message = "MagicTorch array not valid.";
                if(!plTArray.get(player).transmitterSet())
                    this.message = this.message + " [transmitter not selected]";
                if(!plTArray.get(player).receiverSet())
                    this.message = this.message + " [receivers not selected]";
            }
        }
        return false;
    }
    
    public boolean delete(Block block){
        return delete(block.getLocation());
    }
    
    public boolean delete(Location loc){
        //TODO: delete function
        //This needs to change to removing from dB and reloading.
        if(mtArray.containsKey(loc)) {
            this.message = mtArray.get(loc).getName();
            return (mtArray.remove(loc) != null);
        }
        return false;
    }
    
    public boolean isMT(Block block) {
        return isMT(block.getLocation());
    }
    
    public boolean isMT(Location loc) {
        return mtArray.containsKey(loc);
    }
    
    public boolean transmit(Location loc) {
        return (isMT(loc)) ? mtArray.get(loc).transmit() : false;
    }
    
    public boolean isInEditMode(Player player) {
        return (plEditMode.containsKey(player)) ? plEditMode.get(player) : false;
    }
    
    public String list(CommandSender sender, boolean isAdmin) {
        //TODO: list
        for (Map.Entry<Location, TorchArray> entry : mtArray.entrySet()) {
            pl.spam(entry.getValue().toString());
        }
        return "";
    }
    
    public void setEditMode(Player player) {
        setEditMode(player, true, TorchArray.DIRECT);
    }
    
    public void setEditMode(Player player, byte nextType) {
        setEditMode(player, true, nextType);
    }
    
    public void setEditMode(Player player, boolean mode) {
        setEditMode(player, mode, TorchArray.DIRECT);
    }
    
    public void setEditMode(Player player, boolean mode, byte nextType) {
        if(mode) {
            plEditMode.put(player, mode);
            plTArray.put(player, new TorchArray());
            plNextLinkType.put(player, nextType);
        } else {
            removePLVars(player);
        }
    }
    
    public boolean setTransmitter(Player player, Block block) {
        return setTransmitter(player, block.getLocation());
    }
    
    public boolean setTransmitter(Player player, Location loc) {
        if(plTArray.containsKey(player)) {
            if(mtArray.containsKey(loc)) {
                this.message = "This torch is already an existing transmitter.";
                return false;
            }
            plTArray.get(player).setTransmitter(loc);
            return true;
        }
        this.message = "Cannot set transmitter. Not in edit mode.";
        return false;
    }
    
    public boolean isSetTransmitter(Player player, Block block) {
        //This is a work around to the double event when the event it cancelled.
        if(plTArray.containsKey(player)){
            return plTArray.get(player).isTransmitter(block.getLocation());
        }
        return false;
    }
    
    public boolean setReceiver(Player player, Block block) {
        return setReceiver(player, block.getLocation());
    }
    
    public boolean setReceiver(Player player, Location loc) {
        if(plTArray.containsKey(player)) {
            if(!plTArray.get(player).isReceiver(loc)){
                plTArray.get(player).add(loc, plNextLinkType.get(player));
                this.message = "Added receiver torch.";
                //pl.spam("plTArray: " + plTArray.get(player).toString());
                return true;
            } else {
                plTArray.get(player).remove(loc);
                this.message = "Removed receiver torch.";
                //pl.spam("plTArray: " + plTArray.get(player).toString());
                return true;
            }
        }
        this.message = "Cannot set receiver. Not in edit mode.";
        return false;
    }
    
    public void setNextType(Player player, byte type) {
        plNextLinkType.put(player, type);
    }
    
    private boolean saveToDB(TorchArray t){
        //TODO: save to DB.
        //Temp:
        if(t.getLocation() != null) {
            mtArray.put(t.getLocation(), t);
            return true;
        }
        return false;
    }
    
    private void loadFromDB(){
        //TODO: load from DB.
    }
    
    private void removePLVars(Player player) {
        plTArray.remove(player);
        plNextLinkType.remove(player);
        plEditMode.remove(player);
    }
    
    private void clearCache() {
        mtArray.clear();  
    }
}