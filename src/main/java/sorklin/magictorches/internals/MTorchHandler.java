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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.torches.DelayReceiver;
import sorklin.magictorches.internals.torches.InverseReceiver;
import sorklin.magictorches.internals.torches.Receiver;
import sorklin.magictorches.internals.torches.TimerReceiver;


public final class MTorchHandler {
    
    //Locate a torcharray by Location
    private Map<Location, TorchArray> mtArray = new HashMap<Location, TorchArray>();
    //Locate a receiver by location
    private final List<? extends Receiver> allReceiverArray = new ArrayList();
    
    public String message = "";
    
    /**
     * Instantiates the MTorchHandler class, without a distance parameter.  Defaults to 
     * a distance radius of 100 blocks (not yet implemented).
     * @param db File object to the MiniDB.
     * @param instance  MagicTorches instance.
     */
    public MTorchHandler (File db, MagicTorches instance) {
        //Move to main routine after initializing storage.
//        miniDB = db;
//        mb_database = new Mini(miniDB.getParent(), miniDB.getName());
//        pl = instance;
//        reload();
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
     * @param player the name of the person issuing the delete command.
     * @param isAdmin if the person has admin perms.
     * @return <code>true</code> if deleted, <code>false</code> if unable to delete.
     */
    public boolean delete(String name, String player, boolean isAdmin){        
        if(Properties.db.exists(name))
            if(player.equalsIgnoreCase(Properties.db.getOwner(name)) || isAdmin)
                if(Properties.db.remove(name)){
                    mtArray = Properties.db.loadAll();
                }
        return false;
    }
    
    /**
     * Returns info about a MT (receiver or transmitter) in a List form.
     * @param block
     * @return ArrayList<String> to be sent to listMessage() proc.
      */
//    public List<String> getInfo(Block block){
//        return getInfo(block, "", true, false);
//    }
    
    /**
     * Returns info about a MT (receiver or transmitter) in a List form.
     * @param block the torch in question
     * @param player the player requesting info
     * @param isAdmin if the player has admin perms
     * @param clicked if the question comes from a click event (versus a command event)
     * @return 
     */
//    public List<String> getInfo(Block block, String player, boolean isAdmin, boolean clicked){
//        List<String> result = new ArrayList<String>();
//        String sb = "";
//        Location loc = block.getLocation();
//        
//        if(isMT(loc)){
//            if(getOwner(loc).equalsIgnoreCase(player) || isAdmin){ //gets around NPE
//                sb = (clicked) ? 
//                        (pl.g + "Transmitter for the " + pl.b + getName(block) 
//                        + pl.g + " array. ") :
//                        (pl.g + getTransmitterInfo(mtArray.get(loc)));
//                result.add(sb);
//                result.add("Its receivers are: ");
//                result.addAll(listReceivers(loc));
//            }
//        } else
//
//        if(isReceiver(loc)){
//            ListIterator<? extends Receiver> it = allReceiverArray.listIterator();
//            //Not sure why this doesn't find the second instance.
//            while(it.hasNext()){
//                Receiver tr = it.next();
//                if(tr.getLocation().equals(loc))
//                    result.add(pl.g + "Receiver: " + pl.w + getReceiverInfo(tr) + ".");
//            }
//            
//        } else {
//            result.add(pl.g + "This is not a MagicTorch.");
//        }
//        return result;
//    }
    
    /**
     * Retrieves the name of the TorchArray at the specified block or location.
     * @param block the block to test.
     * @return the name of the Torch array, or <code>null</code> if no TorchArray
     * at the specified block.
     */
    public String getName(Block block){
        return (mtArray.containsKey(block.getLocation())) ? 
                mtArray.get(block.getLocation()).getName() :
                null;
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
     * @param loc location of the block to be tested.
     * @return <code>true</code> the block is a MT receiver. <code>false</code>
     * the block is not a MT receiver.
     */
    public boolean isReceiver(Location loc){
        ListIterator<? extends Receiver> it = allReceiverArray.listIterator();
        while(it.hasNext()){
            if(it.next().getLocation().equals(loc))
                return true;
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
        //Fix.  Import the PTM(?) page listing routines.
//        String result = "";
//        String comma = "";
//        
//        if(mtNameArray.isEmpty())
//            result = "No MagicTorch Arrays found.";
//        
//        for (Entry<Location, String> entry : mtNameArray.entrySet()) {
//            if(isAdmin){
//                result += comma + pl.b + entry.getValue()
//                        + " [" + getOwner(entry.getKey()) + "]";
//                comma = pl.w + ", ";
//            } else {
//                if(isOwner((Player)sender, entry.getKey())){
//                    result = result + comma + pl.b + entry.getValue();
//                    comma = pl.w + ", ";
//                }
//            }
//        }
//        return result;
        return null;
    }
    
    /**
     * List all the receivers loaded. DEV only.
     * @return String containing all receiver locations.
     */
//    public String listAllReceivers(){
//        String result = "";
//        String comma = "";
//        
//        if(allReceiverArray.isEmpty())
//            return "No Receivers found.";
//        
//        ListIterator<? extends Receiver> it = allReceiverArray.listIterator();
//        
//        while(it.hasNext()){
//            result += (comma + it.next().getLocation().toString());
//            comma = ", ";
//        }
//        return result;
//    }
    
    /**
     * Returns a list of receivers for the clicked transmitter.
     * @param loc location of the transmitter torch.
     * @return 
     */
    public List<String> listReceivers(Location loc){
        List<String> result = new ArrayList<String>();
        ArrayList<? extends Receiver> receivers = new ArrayList();
        
        if(mtArray.containsKey(loc)){
            receivers = mtArray.get(loc).getReceiverArray();
            if(!receivers.isEmpty()){
                ListIterator<? extends Receiver> it = receivers.listIterator();
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
//    public void prune(){
//        for(String name: mb_database.getIndices().keySet()) {
//            if(!mtNameArray.containsValue(name)){
//                MagicTorches.log(Level.FINE, pl.g + "Could not find " + pl.b + name + pl.g 
//                        + " in active torch arrays.");
//                MagicTorches.log(Level.FINE, pl.g + "Pruning it from DB.");
//                mb_database.removeIndex(name);
//            }
//        }
//        mb_database.update();
//    }
    
    /**
     * Reloads the MagicTorches from file.
     */
//    public void reload(){
//        clearCache();
//        //force a reload of the minidb.
//        mb_database = null;
//        mb_database = new Mini(miniDB.getParent(), miniDB.getName());
//        loadFromDB();
//        transmitAll(); //initial transmit to set all the receivers.
//    }
    
    /**
     * Sets edit mode for a player to true. Defaults receiver type to <code>DIRECT</code>.
     * @param player 
     */
//    public void setEditMode(Player player) {
//        setEditMode(player, true, Properties.DIRECT);
//    }
    
    /**
     * Sets edit mode for a player to true. Sets the next receiver torch type to 
     * <code>nextType</code>. Defaults receiver type to <code>DIRECT</code>.
     * @param player
     * @param nextType  the type for the next selected receivers.
     */
//    public void setEditMode(Player player, byte nextType) {
//        setEditMode(player, true, nextType);
//    }
    
    /**
     * Sets edit mode for player to <code>mode</code>. Defaults receiver type 
     * to <code>DIRECT</code>.
     * @param player
     * @param mode <code>true</code> for edit mode on, <code>false</code> for edit 
     * mode off.
     */
//    public void setEditMode(Player player, boolean mode) {
//        setEditMode(player, mode, Properties.DIRECT);
//    }
    
    /**
     * Sets edit mode for player to <code>mode</code>. Sets the receiver type
     * to <code>nextType</code>.
     * @param player
     * @param mode  <code>true</code> for edit mode on, <code>false</code> for edit 
     * mode off.
     * @param nextType the type for the next selected receivers.
     */
//    public void setEditMode(Player player, boolean mode, byte nextType) {
//        if(mode) {
//            plEditMode.put(player, mode);
//            plTArray.put(player, new TorchArray(player.getName()));
//            plNextLinkType.put(player, nextType);
//        } else {
//            removePLVars(player);
//        }
//    }
    
    /**
     * Sets the next selected receiver type to <code>type</code>.
     * @param player
     * @param type receiver type.
     */
//    public void setNextType(Player player, byte type) {
//            plNextLinkType.put(player, type);
//    }
    
    /**
     * Sets a torch to be a receiver in an array.
     * @param player player editing an array.
     * @param block the torch to be set.
     * @return <code>true</code> if the torch was set as a receiver.
     * <code>false</code> if the torch was not set as a receiver.
     */
//    public boolean setReceiver(Player player, Block block) {
//        return setReceiver(player, block.getLocation());
//    }
    
    /**
     * Sets a torch to be a receiver in an array.
     * @param player  player editing an array.
     * @param loc  the location of the torch to be set.
     * @return <code>true</code> if the torch was set as a receiver.
     * <code>false</code> if the torch was not set as a receiver.
     */
//    public boolean setReceiver(Player player, Location loc) {
//        if(plTArray.containsKey(player)) {
//            if(!plTArray.get(player).isReceiver(loc)){
//                //TODO: distance check.  Requires Transmitter to be set.
//                plTArray.get(player).add(loc, plNextLinkType.get(player));
//                this.message = "Added receiver torch.";
//                return true;
//            } else {
//                plTArray.get(player).remove(loc);
//                this.message = "Removed receiver torch.";
//                return true;
//            }
//        }
//        this.message = "Cannot set receiver. Not in edit mode.";
//        return false;
//    }
    
    /**
     * Sets a torch to be a transmitter in an array.
     * @param player  player editing an array.
     * @param block  the torch to be set.
     * @return <code>true</code> if the torch was set as a transmitter.
     * <code>false</code> if the torch was not set as a transmitter.
     */
//    public boolean setTransmitter(Player player, Block block) {
//        return setTransmitter(player, block.getLocation());
//    }
    
    /**
     * Sets a torch to be a transmitter in an array.
     * @param player  player editing an array.
     * @param loc  the location of the torch to be set.
     * @return <code>true</code> if the torch was set as a transmitter.
     * <code>false</code> if the torch was not set as a transmitter.
     */
//    public boolean setTransmitter(Player player, Location loc) {
//        if(plTArray.containsKey(player)) {
//            if(mtArray.containsKey(loc)) {
//                this.message = "This torch is already an existing transmitter.";
//                return false;
//            }
//            plTArray.get(player).setTransmitter(loc);
//            return true;
//        }
//        this.message = "Cannot set transmitter. Not in edit mode.";
//        return false;
//    }
    
    /**
     * Displays info about a torch Array to the command sender.
     * @param name name of torch array
     * @param sender the player or entity that asked.
     * @param isAdmin if the entity is an admin.
     * @return
     */
//    public void showInfo(String name, CommandSender sender, boolean isAdmin){
//        if(mb_database.hasIndex(name.toLowerCase())){
//            Arguments entry = mb_database.getArguments(name.toLowerCase());
//            if(!isAdmin && !sender.getName().equalsIgnoreCase(entry.getValue("owner"))){
//                sender.sendMessage("That is not your array.");
//                return;
//            }
//            Iterator it = mtNameArray.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<Location,String> ta = (Map.Entry<Location,String>)it.next();
//                if(ta.getValue().equals(name.toLowerCase())){
//                    MagicTorches.listMessage(sender, getInfo(ta.getKey().getBlock()));
//                    break;
//                }
//                //it.remove(); // avoids a ConcurrentModificationException
//            }
//        } else {
//            sender.sendMessage(pl.r + "No array by that name is in the db.");
//        }
//    }
    
    /**
     * Sends a transmit signal to the transmitter of an Array.
     * @param loc the location of the transmitter torch.
     * @param current whether current is on or off for the transmit signal.
     * @return <code>true</code> if signal could be transmitted.
     * <code>false</code> if signal could not be transmitted.
     */
//    public boolean transmit(Location loc, boolean current){
//        return (isMT(loc)) ? mtArray.get(loc).transmit(current) : false;
//    }
    
    /**
     * Sends a transmit signal to the transmitter of an Array.
     * @param loc the location of the transmitter torch.
     * @return <code>true</code> if signal could be transmitted.
     * <code>false</code> if signal could not be transmitted.
     */
//    public boolean transmit(Location loc) {
//        return (isMT(loc)) ? mtArray.get(loc).transmit() : false;
//    }
    
    /**
     * Iterates through all TorchArrays, sending a transmit signal.
     */
    public void transmitAll(){
        for (Entry<Location, TorchArray> entry : mtArray.entrySet()) {
            entry.getValue().transmit();
        }
    }
    
    /****************************** PRIVATE ************************************/
    
    private void clearCache() {
        mtArray.clear();
//        mtNameArray.clear();
        allReceiverArray.clear();
        this.message = "";
    }
    
    private String getOwner(Location loc) {
        if(mtArray.containsKey(loc))
            return mtArray.get(loc).getOwner();
        else
            return null;
    }
    
    private String getReceiverInfo(Receiver tr){
        StringBuilder sb = new StringBuilder();
        
        if(tr instanceof InverseReceiver)
            sb.append("Inverse");
        else if(tr instanceof DelayReceiver)
            sb.append("Delay");
        else if(tr instanceof TimerReceiver)
            sb.append("Delay");
        else 
            sb.append("Direct");
        
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
}
