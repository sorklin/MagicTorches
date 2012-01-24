/*
 * Copyright (C) 2012 Sorklin <sorklin at gmail.com>
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
package sorklin.magictorches;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Sorklin <sorklin at gmail.com>
 */
public class OLDCRAP {
    /**
     * Sets edit mode for player to <code>mode</code>. Sets the receiver type
     * to <code>nextType</code>.
     * @param player
     * @param mode  <code>true</code> for edit mode on, <code>false</code> for edit 
     * mode off.
     * @param nextType the type for the next selected receivers.
     */
    public void setEditMode(Player player, boolean mode, byte nextType) {
        if(mode) {
            plEditMode.put(player, mode);
            plTArray.put(player, new TorchArray(player.getName()));
            plNextLinkType.put(player, nextType);
        } else {
            removePLVars(player);
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
     * Returns info about a MT (receiver or transmitter) in a List form.
     * @param block
     * @return ArrayList<String> to be sent to listMessage() proc.
      */
    public List<String> getInfo(Block block){
        return getInfo(block, "", true, false);
    }
    
    
    
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
     * List the loaded MT arrays.
     * @param sender entity requesting the list.
     * @param isAdmin if the entity has admin permissions.
     * @return String containing all appropriate MT arrays by name.
     */
    public String list(CommandSender sender, boolean isAdmin) {
        Fix.  Import the PTM(?) page listing routines.
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
        return null;
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
        
        ListIterator<? extends DirectReceiver> it = allReceiverArray.listIterator();
        
        while(it.hasNext()){
            result += (comma + it.next().getLocation().toString());
            comma = ", ";
        }
        return result;
    }
    
    
    
    /**
     * Adds an entry into the player/Torchcreate Hashmap.
     * @param player Player
     * @param tc TorchCreator object
     */
    public void setPlayerCreate(Player player, TorchCreator tc){
        mtCreate.put(player, tc);
    }
    
    /**
     * Retrieves the TorchCreator object for a player.
     * @param player
     * @return Object or null if none found.
     */
    public TorchCreator getPlayerCreate(Player player){
        return (mtCreate.containsKey(player)) ? mtCreate.get(player) : null;
    }
    
    /**
     * Checks if a player has an entry in the Creation Hashmap.
     * @param player
     */
    public boolean isPlayerCreating(Player player){
        return mtCreate.containsKey(player);
    }
    
    /**
     * Removes a player's entry in the Creation hashmap, if possible.
     * @param player 
     */
    public void removePlayerCreate(Player player){
        mtCreate.remove(player);
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
        if(MagicTorches.getMiniDB().exists(name))
            if(player.equalsIgnoreCase(MagicTorches.getMiniDB().getOwner(name)) || isAdmin)
                if(MagicTorches.getMiniDB().remove(name)){
                    reload();
                }
        return false;
    }
    
    private String getOwner(Location loc) {
        if(mtArray.containsKey(loc))
            return mtArray.get(loc).getOwner();
        else
            return null;
    }
    
        private boolean isOwner(Player player, Location loc) {
        if(mtArray.containsKey(loc))
            return (mtArray.get(loc).getOwner().equals(player.getName()));
        else
            return false;
    }
                /**
     * Prunes database of unloaded MTs.
     */
    public void prune(){
        for(String name: mb_database.getIndices().keySet()) {
            if(!mtNameArray.containsValue(name)){
                MagicTorches.log(Level.FINE, pl.g + "Could not find " + pl.b + name + pl.g 
                        + " in active torch arrays.");
                MagicTorches.log(Level.FINE, pl.g + "Pruning it from DB.");
                mb_database.removeIndex(name);
            }
        }
        mb_database.update();
    }


    
    private boolean saveToDB(Player player, TorchArray t){
        if(!t.isValid())
            return false;
        
        String name = t.getName();
        String data = t.toString();
        MagicTorches.log(Level.FINER, "Saving to DB:" + t.getName() + ", " + t.toString());
        Arguments entry = new Arguments(name.toLowerCase());
        entry.setValue("owner", player.getName());
        entry.setValue("data", data);
        mb_database.addIndex(entry.getKey(), entry);
        mb_database.update();
        //Now push onto working cache:
        mtArray.put(t.getLocation(), t);
        mtNameArray.put(t.getLocation(),t.getName());
        transmit(t.getLocation());
        return true;
    }
}
