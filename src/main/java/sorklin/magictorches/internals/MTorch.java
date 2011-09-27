package sorklin.magictorches.internals;

import com.mini.Arguments;
import com.mini.Mini;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import java.util.Map.Entry;
import java.util.regex.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sorklin.magictorches.MagicTorches;

public final class MTorch {
    
    //transmitter to TorchArray:
    private Map<Location, TorchArray> mtArray = new HashMap<Location, TorchArray>();
    private Map<Location, String> mtNameArray = new HashMap<Location, String>();
    
    private ArrayList<Location> allReceiverArray = new ArrayList<Location>();
    
    //These three are for magic creation by different players.
    private Map<Player, TorchArray> plTArray = new HashMap<Player, TorchArray>();
    private Map<Player, Boolean> plEditMode = new HashMap<Player, Boolean>();
    private Map<Player, Byte> plNextLinkType = new HashMap<Player, Byte>();
    
    private Mini mb_database;
    private MagicTorches pl;
    private File miniDB;
    private double maxDistance; //TODO: drive this with a config setting.
    
    public String message = "";
    
    public MTorch (File db, MagicTorches instance) {
        miniDB = db;
        mb_database = new Mini(miniDB.getParent(), miniDB.getName());
        pl = instance;
        maxDistance = 100.0;
    }
    
    public MTorch (File db, MagicTorches instance, double distance) {
        miniDB = db;
        mb_database = new Mini(miniDB.getParent(), miniDB.getName());
        pl = instance;
        maxDistance = distance;
    }
    
    public boolean create(Player player, String name){
        if(plTArray.containsKey(player)) {
            plTArray.get(player).setName(name.toLowerCase().trim());
            if(plTArray.get(player).isValid()) {
                if(saveToDB(player, plTArray.get(player))) {
                    reload(); //reloads the mt array from file.
                    this.message = name.toLowerCase().trim();
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
        return (mtNameArray.containsKey(loc)) ? delete(mtNameArray.get(loc)) : false;
    }
    
    public boolean delete(String name){
        if(mb_database.hasIndex(name)) {
            mb_database.removeIndex(name);
            mb_database.update();
            reload();
            this.message = name;
            return true;
        }
        return false;
    }
    
    public boolean isInEditMode(Player player) {
        return (plEditMode.containsKey(player)) ? plEditMode.get(player) : false;
    }
    
    public boolean isMT(Block block) {
        return isMT(block.getLocation());
    }
    
    public boolean isMT(Location loc) {
        return mtArray.containsKey(loc);
    }
    
    public boolean isReciever(Block block){
        return isReciever(block.getLocation());
    }
    
    public boolean isReciever(Location loc){
        return allReceiverArray.contains(loc);
    }
    
    public boolean isSetTransmitter(Player player, Block block) {
        //This is a work around to the double event when the event it cancelled.
        if(plTArray.containsKey(player)){
            return plTArray.get(player).isTransmitter(block.getLocation());
        }
        return false;
    }
        
    public String list(CommandSender sender, boolean isAdmin) {
        String result = "";
        String comma = "";
        
        if(mtNameArray.isEmpty())
            result = "No MagicTorch Arrays found.";
        
        for (Entry<Location, String> entry : mtNameArray.entrySet()) {
            result = result + comma + entry.getValue();
            //result += "[" + trLocationFromData(mtArray.get(entry.getKey()).toString()) + "]";
            comma = ", ";
        }
        return result;
    }
    
    public String listRecievers(CommandSender sender){
        String result = "";
        String comma = "";
        
        if(allReceiverArray.isEmpty())
            return "No Receivers found.";
        
        ListIterator<Location> it = allReceiverArray.listIterator();
        
        while(it.hasNext()){
            result += (comma + it.next().toString());
            comma = ", ";
        }
        return result;
    }
    
    public void reload(){
        clearCache();
        //force a reload of the minidb.
        mb_database = null;
        mb_database = new Mini(miniDB.getParent(), miniDB.getName());
        loadFromDB();
        transmitAll(); //initial transmit to set all the receivers.
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
    
    public void setNextType(Player player, byte type) {
        plNextLinkType.put(player, type);
    }
    
    public boolean setReceiver(Player player, Block block) {
        return setReceiver(player, block.getLocation());
    }
    
    public boolean setReceiver(Player player, Location loc) {
        if(plTArray.containsKey(player)) {
            if(!plTArray.get(player).isReceiver(loc)){
                //TODO: distance check.  Requires Transmitter to be set.
                plTArray.get(player).add(loc, plNextLinkType.get(player));
                this.message = "Added receiver torch.";
                return true;
            } else {
                plTArray.get(player).remove(loc);
                this.message = "Removed receiver torch.";
                return true;
            }
        }
        this.message = "Cannot set receiver. Not in edit mode.";
        return false;
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
    
    public boolean transmit(Location loc, boolean current){
        return (isMT(loc)) ? mtArray.get(loc).transmit(current) : false;
    }
    
    public boolean transmit(Location loc) {
        return (isMT(loc)) ? mtArray.get(loc).transmit() : false;
    }
    
    public void transmitAll(){
        for (Entry<Location, TorchArray> entry : mtArray.entrySet()) {
            transmit(entry.getKey());
        }
    }
    
    /****************************** PRIVATE ************************************/
    
    private void clearCache() {
        mtArray.clear();
        mtNameArray.clear();
        allReceiverArray.clear();
        this.message = "";
    }
    
    private boolean isOwner(Player player, Location loc) {
        if(mtNameArray.containsKey(loc)){
            String name = mtNameArray.get(loc);
            if(name != null) {
                Arguments entry = null;
                entry = mb_database.getArguments(name);
                if(entry != null) {
                    return (entry.getValue("owner").equals(player.getName()));
                }
            }
        }
        return false;
    }
    
    private void loadFromDB(){
        String data = "";
        Location loc;
        TorchArray ta;
        
        for(String name: mb_database.getIndices().keySet()) {
            Arguments entry = mb_database.getArguments(name);
            data = entry.getValue("data");
            pl.spam("LoadDB data: " + data);
            loc = trLocationFromData(data);
            ta = torchArrayFromData(data, name);
            if(loc != null && ta != null) {
                mtArray.put(loc, ta);
                mtNameArray.put(loc, name);
                allReceiverArray.addAll(ta.getReceiverArray());
            } else {
                pl.spam("Deleting malformed entry: " + name);
                this.message = name + "'s entry was malformed, or the transmitting"
                        + "torch is missing. Deleting entry from DB.";
                delete(name);
            }
        }
    }
    
    private Location locationFromString(String data){
        //World: (?<=name=)\w+
        //Coords: (?<==)-?\d+\.\d+  (returns 5 matches (x, y, z, yaw, pitch).

        String world = "";
        List<String> coords = new ArrayList<String>();
        Location result = null;
        
        Pattern p = Pattern.compile("(?<=name=)\\w+");
        Matcher m = p.matcher(data);
        if(m.find()){
            world = m.group();
        }

        p = Pattern.compile("(?<==)-?\\d+\\.\\d+");
        m = p.matcher(data);
        if(m != null) {
            while(m.find()) {
                coords.add(m.group());
            }
        }
        
        if(!world.isEmpty() && coords.size() == 5) {
            //Valid pull data.
            result = new Location(pl.getServer().getWorld(world), 
                    Double.valueOf(coords.get(0)), 
                    Double.valueOf(coords.get(1)), 
                    Double.valueOf(coords.get(2)));
        }
        return result;
    }
    
    private void removePLVars(Player player) {
        plTArray.remove(player);
        plNextLinkType.remove(player);
        plEditMode.remove(player);
        this.message = "";
    }
    
    private boolean saveToDB(Player player, TorchArray t){
        if(!t.isValid())
            return false;
        
        String name = t.getName();
//        Location loc = t.getLocation();
        String data = t.toString();
        pl.spam("Saving to DB:" + t.getName() + ", " + t.toString());
        Arguments entry = new Arguments(name.toLowerCase());
        entry.setValue("owner", player.getName());
        entry.setValue("data", data);
        
        mb_database.addIndex(entry.getKey(), entry);
        mb_database.update();
        reload();
        return true;
    }
    
    private Location trLocationFromData(String data){
        
        if(!data.contains(";")){
            return null;
        }
        
        Location result = null;
        
        String[] sub = data.split(";");
        
        for(int i = 0, length = sub.length; i < length; i++){
            if(sub[i].contains("Transmitter")){
                result = locationFromString(sub[i]);
            }
        }
        return result;
    }
    
    private TorchArray torchArrayFromData(String data, String name){
        
        if(!data.contains(";"))
            return null; //can't split, this is malformed string
        
        String[] sub = data.split(";");
        if(sub.length < 3)
            return null; //Less than three segments is malformed.
                
        TorchArray ta = new TorchArray();
        ta.setName(name);
        
        for(int i=0, length = sub.length; i < length; i++) {
            if(sub[i].contains("Transmitter")){
                //Only set the transmitter location if the location is not null,
                //AND there is a redstone torch already there.
                //IF transmitter left unset, the TorchArray will not be valid, causing the 
                //TA to be deleted from the db. (a good thing)
                Location loc = locationFromString(sub[i]);
                if(loc != null){
                    Material m = loc.getBlock().getType();
                    if(m.equals(Material.REDSTONE_TORCH_OFF) 
                            || m.equals(Material.REDSTONE_TORCH_ON)) {
                        ta.setTransmitter(loc);
                    }
                }
            } else 
            
            if(sub[i].contains("Receiver")){
                ta.add(locationFromString(sub[i]), typeFromString(sub[i]));
            }
        }
        return ((ta.isValid()) ? ta : null);
    }
    
    private byte typeFromString(String data){
        //type: (?<=Type{)\d{1,2}
        byte result = TorchArray.DIRECT; //Default to direct.

        Pattern p = Pattern.compile("(?<=Type\\{)\\d{1,2}");
        Matcher m = p.matcher(data);
        if(m.find()){
            result = Byte.parseByte(m.group());
        }
        return result;
    }
}
