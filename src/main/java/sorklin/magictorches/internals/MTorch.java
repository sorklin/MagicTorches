package sorklin.magictorches.internals;

import com.mini.Arguments;
import com.mini.Mini;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private final Map<Location, TorchArray> mtArray = new HashMap<Location, TorchArray>();
    private final Map<Location, String> mtNameArray = new HashMap<Location, String>();
    
    private final ArrayList<TorchReceiver> allReceiverArray = new ArrayList<TorchReceiver>();
    
    //These three are for magic creation by different players.
    private final Map<Player, TorchArray> plTArray = new HashMap<Player, TorchArray>();
    private final Map<Player, Boolean> plEditMode = new HashMap<Player, Boolean>();
    private final Map<Player, Byte> plNextLinkType = new HashMap<Player, Byte>();
    
    private Mini mb_database;
    private MagicTorches pl;
    private File miniDB;
    private double maxDistance; //TODO: drive this with a config setting.
    
    public String message = "";
    
    /**
     * Instantiates the MTorch class, without a distance parameter.  Defaults to 
     * a distance radius of 100 blocks (not yet implemented).
     * @param db File object to the MiniDB.
     * @param instance  MagicTorches instance.
     */
    public MTorch (File db, MagicTorches instance) {
        miniDB = db;
        mb_database = new Mini(miniDB.getParent(), miniDB.getName());
        pl = instance;
        maxDistance = 100.0;
        reload();
    }
    
    /**
     * Instantiates the MTorch class.
     * @param db File object to the MiniDB.
     * @param instance MagicTorches instance.
     * @param distance TorchArray maximum distance radius.
     */
    public MTorch (File db, MagicTorches instance, double distance) {
        miniDB = db;
        mb_database = new Mini(miniDB.getParent(), miniDB.getName());
        pl = instance;
        maxDistance = distance;
        reload();
    }
    
    /**
     * Creates a MagicTorch array from the previously input Transmitter and Receiver
     * selections.
     * @param player Player object of the person creating the array (the owner).
     * @param name The name of the array.
     * @return <code>true</code> if the Array was created, <code>false</code> if it was invalid.
     */
    public boolean create(Player player, String name){
        if(mb_database.hasIndex(name.toLowerCase())){
            this.message = "A MagicTorch Array of that name already exists.";
            return false;
        }
        
        if(plTArray.containsKey(player)) {
            plTArray.get(player).setName(name.toLowerCase().trim());
            if(plTArray.get(player).isValid()) {
                if(saveToDB(player, plTArray.get(player))) {
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
    
    /**
     * Delete a MagicTorch Array.
     * @param block the transmitter torch's block.
     * @return <code>true</code> if deleted, <code>false</code> if unable to delete.
     */
    public boolean delete(Block block){
        return delete(block.getLocation());
    }
    
    /**
     * Delete a MagicTorch Array.
     * @param loc the transmitter torch's location.
     * @return <code>true</code> if deleted, <code>false</code> if unable to delete.
     */
    public boolean delete(Location loc){
        return (mtNameArray.containsKey(loc)) ? delete(mtNameArray.get(loc)) : false;
    }
    
    /**
     * Delete a MagicTorch Array.  This is called by automatic events -- i.e. block 
     * break, torch removal, etc.  It asserts admin status.
     * @param name the name of the torch array.
     * @return <code>true</code> if deleted, <code>false</code> if unable to delete.
     */
    public boolean delete(String name){
        return delete(name, "", true);
    }
    
    /**
     * Delete a MagicTorch Array. A delete will delete from the database and issue
     * a reload command.
     * @param name the name of the torch array.
     * @param plaer the name of the person issuing the delete command.
     * @param isAdmin if the person has admin perms.
     * @return <code>true</code> if deleted, <code>false</code> if unable to delete.
     */
    public boolean delete(String name, String player, boolean isAdmin){        
        Arguments entry;
        if(mb_database.hasIndex(name)){
            entry = mb_database.getArguments(name);
            if(entry != null){
                if(player.equalsIgnoreCase(entry.getValue("owner")) || isAdmin){
                    mb_database.removeIndex(name);
                    mb_database.update();
                    reload();
                    prune();
                    this.message = name;
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns info about a MT (receiver or transmitter) in a List form.
     * @param block
     * @return ArrayList<String> to be sent to listMessage() proc.
      */
    public List<String> getInfo(Block block){
        return getInfo(block, "", true, false);
    }
    
    /**
     * Returns info about a MT (receiver or transmitter) in a List form.
     * @param block the torch in question
     * @param player the player requesting info
     * @param isAdmin if the player has admin perms
     * @param clicked if the question comes from a click event (versus a command event)
     * @return 
     */
    public List<String> getInfo(Block block, String player, boolean isAdmin, boolean clicked){
        List<String> result = new ArrayList<String>();
        String sb = "";
        Location loc = block.getLocation();
        
        if(isMT(loc)){
            if(getOwner(loc).equalsIgnoreCase(player) || isAdmin){ //gets around NPE
                sb = (clicked) ? 
                        (pl.g + "Transmitter for the " + pl.b + getName(block) 
                        + pl.g + " array. ") :
                        (pl.g + getTransmitterInfo(mtArray.get(loc)));
                result.add(sb);
                result.add("Its receivers are: ");
                result.addAll(listReceivers(loc));
            }
        } else

        if(isReceiver(loc)){
            ListIterator<TorchReceiver> it = allReceiverArray.listIterator();
            TorchReceiver tr;
            //Not sure why this doesn't find the second instance.
            while(it.hasNext()){
                tr = it.next();
                if(tr.getLocation().equals(loc))
                    result.add(pl.g + "Receiver: " + pl.w + getReceiverInfo(tr) + ".");
            }
            
        } else {
            result.add(pl.g + "This is not a MagicTorch.");
        }
        return result;
    }
    
    /**
     * Retrieves the name of the TorchArray at the specified block or location.
     * @param block the block to test.
     * @return the name of the Torch array, or <code>null</code> if no TorchArray
     * at the specified block.
     */
    public String getName(Block block){
        return (mtNameArray.containsKey(block.getLocation())) ?
                mtNameArray.get(block.getLocation()) :
                null;
    }
    
    /**
     * Returns if the player is in edit mode.
     * @param player 
     */
    public boolean isInEditMode(Player player) {
        return (plEditMode.containsKey(player)) ? plEditMode.get(player) : false;
    }
    
    /**
     * Returns if the block is a MagicTorch Array transmitter.
     * @param block
     * @return <code>true</code> the block is a MT transmitter. <code>false</code>
     * the block is not a MT transmitter.
     */
    public boolean isMT(Block block) {
        return isMT(block.getLocation());
    }
    
    /**
     * Returns if the block is a MagicTorch Array transmitter.
     * @param loc location of block.
     * @return <code>true</code> the block is a MT transmitter. <code>false</code>
     * the block is not a MT transmitter.
     */
    public boolean isMT(Location loc) {
        return mtArray.containsKey(loc);
    }
    
    /**
     * Returns if the block is a MagicTorch Array receiver.
     * @param block block to be tested.
     * @return <code>true</code> the block is a MT receiver. <code>false</code>
     * the block is not a MT receiver.
     */
    public boolean isReceiver(Block block){
        return isReceiver(block.getLocation());
    }
    
    /**
     * Returns if the block is a MagicTorch Array receiver.
     * @param loc location of the block to be tested.
     * @return <code>true</code> the block is a MT receiver. <code>false</code>
     * the block is not a MT receiver.
     */
    public boolean isReceiver(Location loc){
        ListIterator<TorchReceiver> it = allReceiverArray.listIterator();
        while(it.hasNext()){
            if(it.next().getLocation().equals(loc))
                return true;
        }
        return false;
    }
    
    /**
     * Is the transmitter selected already set?
     * @param player Player doing the editing.
     * @param block the transmitter.
     * @return <code>true</code> if the block is already set as a transmitter.
     * <code>false</code> if the block is not a transmitter.
     */
    public boolean isSetTransmitter(Player player, Block block) {
        //This is a work around to the double event when the event it cancelled.
        if(plTArray.containsKey(player)){
            return plTArray.get(player).isTransmitter(block.getLocation());
        }
        return false;
    }
    
    /**
     * List the loaded MT arrays.
     * @param sender entity requesting the list.
     * @param isAdmin if the entity has admin permissions.
     * @return String containing all appropriate MT arrays by name.
     */
    public String list(CommandSender sender, boolean isAdmin) {
        String result = "";
        String comma = "";
        
        if(mtNameArray.isEmpty())
            result = "No MagicTorch Arrays found.";
        
        for (Entry<Location, String> entry : mtNameArray.entrySet()) {
            if(isAdmin){
                result += comma + pl.b + entry.getValue()
                        + " [" + getOwner(entry.getKey()) + "]";
                comma = pl.w + ", ";
            } else {
                if(isOwner((Player)sender, entry.getKey())){
                    result = result + comma + pl.b + entry.getValue();
                    comma = pl.w + ", ";
                }
            }
        }
        return result;
    }
    
    /**
     * List all the receivers loaded. DEV only.
     * @return String containing all receiver locations.
     */
    public String listAllReceivers(){
        String result = "";
        String comma = "";
        
        if(allReceiverArray.isEmpty())
            return "No Receivers found.";
        
        ListIterator<TorchReceiver> it = allReceiverArray.listIterator();
        
        while(it.hasNext()){
            result += (comma + it.next().getLocation().toString());
            comma = ", ";
        }
        return result;
    }
    
    /**
     * Returns a list of receivers for the clicked transmitter.
     * @param loc location of the transmitter torch.
     * @return 
     */
    public List<String> listReceivers(Location loc){
        List<String> result = new ArrayList<String>();
        ArrayList<TorchReceiver> receivers = new ArrayList();
        
        if(mtArray.containsKey(loc)){
            receivers = mtArray.get(loc).getReceiverArray();
            if(!receivers.isEmpty()){
                ListIterator<TorchReceiver> it = receivers.listIterator();
                while(it.hasNext()){
                    result.add(getReceiverInfo(it.next()));
                }
            }
        }
        return result;
    }
    
    /**
     * Prunes database of unloaded MTs.
     */
    public synchronized void prune(){
        for(String name: mb_database.getIndices().keySet()) {
            if(!mtNameArray.containsValue(name)){
                MagicTorches.spam(pl.g + "Could not find " + pl.b + name + pl.g 
                        + " in active torch arrays.");
                MagicTorches.spam(pl.g + "Pruning it from DB.");
                mb_database.removeIndex(name);
            }
        }
        mb_database.update();
    }
    
    /**
     * Reloads the MagicTorches from file.
     */
    public void reload(){
        clearCache();
        //force a reload of the minidb.
        mb_database = null;
        mb_database = new Mini(miniDB.getParent(), miniDB.getName());
        loadFromDB();
        transmitAll(); //initial transmit to set all the receivers.
    }
    
    /**
     * Sets edit mode for a player to true. Defaults receiver type to <code>DIRECT</code>.
     * @param player 
     */
    public void setEditMode(Player player) {
        setEditMode(player, true, TorchArray.DIRECT);
    }
    
    /**
     * Sets edit mode for a player to true. Sets the next receiver torch type to 
     * <code>nextType</code>. Defaults receiver type to <code>DIRECT</code>.
     * @param player
     * @param nextType  the type for the next selected receivers.
     */
    public void setEditMode(Player player, byte nextType) {
        setEditMode(player, true, nextType);
    }
    
    /**
     * Sets edit mode for player to <code>mode</code>. Defaults receiver type 
     * to <code>DIRECT</code>.
     * @param player
     * @param mode <code>true</code> for edit mode on, <code>false</code> for edit 
     * mode off.
     */
    public void setEditMode(Player player, boolean mode) {
        setEditMode(player, mode, TorchArray.DIRECT);
    }
    
    /**
     * Sets edit mode for player to <code>mode</code>. Sets the receiver type
     * to <code>nextType</code>.
     * @param player
     * @param mode  <code>true</code> for edit mode on, <code>false</code> for edit 
     * mode off.
     * @param nextType the type for the next selected receivers.
     */
    public synchronized void setEditMode(Player player, boolean mode, byte nextType) {
        if(mode) {
            plEditMode.put(player, mode);
            plTArray.put(player, new TorchArray(player.getName()));
            plNextLinkType.put(player, nextType);
        } else {
            removePLVars(player);
        }
    }
    
    /**
     * Sets the next selected receiver type to <code>type</code>.
     * @param player
     * @param type receiver type.
     */
    public void setNextType(Player player, byte type) {
        synchronized(plNextLinkType){
            plNextLinkType.put(player, type);
        }
    }
    
    /**
     * Sets a torch to be a receiver in an array.
     * @param player player editing an array.
     * @param block the torch to be set.
     * @return <code>true</code> if the torch was set as a receiver.
     * <code>false</code> if the torch was not set as a receiver.
     */
    public boolean setReceiver(Player player, Block block) {
        return setReceiver(player, block.getLocation());
    }
    
    /**
     * Sets a torch to be a receiver in an array.
     * @param player  player editing an array.
     * @param loc  the location of the torch to be set.
     * @return <code>true</code> if the torch was set as a receiver.
     * <code>false</code> if the torch was not set as a receiver.
     */
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
    
    /**
     * Sets a torch to be a transmitter in an array.
     * @param player  player editing an array.
     * @param block  the torch to be set.
     * @return <code>true</code> if the torch was set as a transmitter.
     * <code>false</code> if the torch was not set as a transmitter.
     */
    public boolean setTransmitter(Player player, Block block) {
        return setTransmitter(player, block.getLocation());
    }
    
    /**
     * Sets a torch to be a transmitter in an array.
     * @param player  player editing an array.
     * @param loc  the location of the torch to be set.
     * @return <code>true</code> if the torch was set as a transmitter.
     * <code>false</code> if the torch was not set as a transmitter.
     */
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
    
    /**
     * Displays info about a torch Array to the command sender.
     * @param name name of torch array
     * @param sender the player or entity that asked.
     * @param isAdmin if the entity is an admin.
     * @return
     */
    public void showInfo(String name, CommandSender sender, boolean isAdmin){
        if(mb_database.hasIndex(name.toLowerCase())){
            Arguments entry = mb_database.getArguments(name.toLowerCase());
            if(!isAdmin && !sender.getName().equalsIgnoreCase(entry.getValue("owner"))){
                sender.sendMessage("That is not your array.");
                return;
            }
            Iterator it = mtNameArray.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Location,String> ta = (Map.Entry<Location,String>)it.next();
                if(ta.getValue().equals(name.toLowerCase())){
                    MagicTorches.listMessage(sender, getInfo(ta.getKey().getBlock()));
                    break;
                }
                //it.remove(); // avoids a ConcurrentModificationException
            }
        } else {
            sender.sendMessage(pl.r + "No array by that name is in the db.");
        }
    }
    
    /**
     * Sends a transmit signal to the transmitter of an Array.
     * @param loc the location of the transmitter torch.
     * @param current whether current is on or off for the transmit signal.
     * @return <code>true</code> if signal could be transmitted.
     * <code>false</code> if signal could not be transmitted.
     */
    public boolean transmit(Location loc, boolean current){
        return (isMT(loc)) ? mtArray.get(loc).transmit(current) : false;
    }
    
    /**
     * Sends a transmit signal to the transmitter of an Array.
     * @param loc the location of the transmitter torch.
     * @return <code>true</code> if signal could be transmitted.
     * <code>false</code> if signal could not be transmitted.
     */
    public boolean transmit(Location loc) {
        return (isMT(loc)) ? mtArray.get(loc).transmit() : false;
    }
    
    /**
     * Iterates through all TorchArrays, sending a transmit signal.
     */
    public void transmitAll(){
        for (Entry<Location, TorchArray> entry : mtArray.entrySet()) {
            transmit(entry.getKey());
        }
    }
    
    /****************************** PRIVATE ************************************/
    
    private synchronized void clearCache() {
        mtArray.clear();
        mtNameArray.clear();
        allReceiverArray.clear();
        this.message = "";
    }
    
    private String getOwner(Location loc) {
        if(mtArray.containsKey(loc))
            return mtArray.get(loc).getOwner();
        else
            return "";
    }
    
    private String getReceiverInfo(TorchReceiver tr){
        StringBuilder sb = new StringBuilder();
        
        if(tr.getType() == TorchArray.DIRECT)
            sb.append("Direct");
        else if(tr.getType() == TorchArray.INVERSE)
            sb.append("Inverse");
        else if(tr.getType() == TorchArray.DELAY)
            sb.append("Delay");
        else
            sb.append("Unknown");
        
        sb.append(" receiver at ");
        sb.append("[").append(tr.getLocation().getWorld().getName()).append(": ");
        sb.append(tr.getLocation().getBlockX()).append(", ");
        sb.append(tr.getLocation().getBlockY()).append(", ");
        sb.append(tr.getLocation().getBlockZ()).append("]");
        
        return sb.toString();
    }
    
    private String getTransmitterInfo(TorchArray ta){
        StringBuilder sb = new StringBuilder();

        sb.append("Transmitter at ");
        sb.append("[").append(ta.getLocation().getWorld().getName()).append(": ");
        sb.append(ta.getLocation().getBlockX()).append(", ");
        sb.append(ta.getLocation().getBlockY()).append(", ");
        sb.append(ta.getLocation().getBlockZ()).append("]");
        
        return sb.toString();
    }
    
    private boolean isOwner(Player player, Location loc) {
        if(mtArray.containsKey(loc))
            return (mtArray.get(loc).getOwner().equals(player.getName()));
        else
            return false;
    }
    
    private synchronized void loadFromDB(){
        String data = "";
        String owner = "";
        Location loc;
        TorchArray ta;
        
        for(String name: mb_database.getIndices().keySet()) {
            Arguments entry = mb_database.getArguments(name);
            owner = entry.getValue("owner");
            data = entry.getValue("data");
            //pl.spam("LoadDB data: " + data);
            try {
                loc = trLocationFromData(data);
                ta = torchArrayFromData(data, name, owner);
                if(loc != null && ta != null) {
                    mtArray.put(loc, ta);
                    mtNameArray.put(loc, name);
                    allReceiverArray.addAll(ta.getReceiverArray());
                    MagicTorches.spam("Loaded torch: " + name);
                } else {
                    this.message = name + "'s entry was malformed, or the transmitting"
                            + "torch is missing. Deleting entry from DB.";
                    MagicTorches.spam(this.message);
                    delete(name);
                }
            } catch (NullPointerException npe) {
                MagicTorches.spam("NPE on torch: " + name);
            } // just ignore for now
        }
    }
    
    private Location locationFromString(String data) throws NullPointerException {
        //World: (?<=name=)\w+
        //Coords: (?<==)-?\d+\.\d+  (returns 5 matches (x, y, z, yaw, pitch).
        
        //NPE if the world is NULL i.e., if MV or other multiverse plugin not loaded.
        
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
    
    private synchronized void removePLVars(Player player) {
        plTArray.remove(player);
        plNextLinkType.remove(player);
        plEditMode.remove(player);
        this.message = "";
    }
    
    private synchronized boolean saveToDB(Player player, TorchArray t){
        if(!t.isValid())
            return false;
        
        String name = t.getName();
        String data = t.toString();
        //pl.spam("Saving to DB:" + t.getName() + ", " + t.toString());
        Arguments entry = new Arguments(name.toLowerCase());
        entry.setValue("owner", player.getName());
        entry.setValue("data", data);
        mb_database.addIndex(entry.getKey(), entry);
        mb_database.update();
        //Now push onto working cache:
        mtArray.put(t.getLocation(), t);
        mtNameArray.put(t.getLocation(),t.getName());
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
    
    private TorchArray torchArrayFromData(String data, String name, String owner){
        
        if(!data.contains(";"))
            return null; //can't split, this is malformed string
        
        String[] sub = data.split(";");
        if(sub.length < 3)
            return null; //Less than three segments is malformed.
                
        TorchArray ta = new TorchArray(owner);
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
