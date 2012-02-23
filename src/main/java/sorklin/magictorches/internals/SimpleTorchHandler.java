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

import java.util.*;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.interfaces.MTReceiver;
import sorklin.magictorches.internals.torches.*;


public final class SimpleTorchHandler {
    
    private MagicTorches mt;
    //Locate a torcharray by Location
    private HashMap<Location, TorchArray> mtArray = new HashMap<Location, TorchArray>();
    //Locate a receiver by location
    private final List<MTReceiver> allReceiverArray = new ArrayList();
    //Passback messages.
    private String message = "";
    
    
    public SimpleTorchHandler (MagicTorches instance) {
        mt = instance;
        reload();
        transmitAll(true);//Transmit an init signal to get the torches in the right state.
    }
    
    /**
     * Returns any message stored in this object.
     */
    public String getMessage(){
        return this.message;
    }
    
    /**
     * Adds or sets an array at a specific location into the active map.
     * @param ta TorchArray to add.
     */
    public void addArray(TorchArray ta){
        mtArray.put(ta.getLocation(), ta);
        allReceiverArray.addAll(ta.getReceiverArray());
    }
    
    /**
     * Removes the torchArray from the active map.
     * @param loc 
     */
    public boolean removeArray(Location loc){
        if(!mtArray.containsKey(loc))
            return false;
        TorchArray ta = mtArray.get(loc);
        message = ta.getName(); //for the deletion message.
        allReceiverArray.removeAll(ta.getReceiverArray());
        mtArray.remove(loc);
        return true;
    }
    
    public void removeAllArrays(){
        mtArray.clear();
    }
    
    public HashMap<Location,TorchArray> getAllArrays(){
        return mtArray;
    }
    
    /**
     * Returns the torch array for a given location.
     * @param loc Location in question.
     * @return The torchArray or <code>null</code>, if none found.
     */
    public TorchArray getArray(Location loc){
        return (mtArray.containsKey(loc)) ? mtArray.get(loc) : null;
    }
    
    /**
     * Returns the torch array for a given name.
     * @param name
     * @return 
     */
    public TorchArray getArray(String name) {
        for (Iterator<Entry<Location, TorchArray>> it = mtArray.entrySet().iterator(); it.hasNext();) {
            Entry<Location, TorchArray> ta = it.next();
            if(name.equalsIgnoreCase(ta.getValue().getName()))
                return ta.getValue();
        }
        return null;
    }
    
    /**
     * Reloads the MagicTorches from file.
     */
    public void reload(){
        clearCache();
        this.mtArray = MagicTorches.getMiniDB().loadAll();
        for (Entry<Location, TorchArray> ent : this.mtArray.entrySet()){
            allReceiverArray.addAll(ent.getValue().getReceiverArray());
        }
    }
    
    /**
     * Iterates through all TorchArrays, sending a transmit signal.
     */
    public void transmitAll(boolean initTorches){
        for (Iterator<Entry<Location, TorchArray>> it = mtArray.entrySet().iterator(); it.hasNext();) {
            Entry<Location, TorchArray> entry = it.next();
            Bukkit.getServer().getPluginManager().callEvent(new TransmitEvent(entry.getValue(), initTorches));
        }
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
        String sb;
        Location loc = block.getLocation();
        
        if(isMT(loc)){
            if(mtArray.get(loc).getOwner().equalsIgnoreCase(player)
                    || isAdmin){ //gets around NPE
                sb = (clicked) ? 
                        ("`YTransmitter for the `a" + mtArray.get(loc).getName() +" `Yarray. ") :
                        ("`Y" + getTransmitterInfo(mtArray.get(loc)));
                result.add(sb);
                result.add("Its receivers are: ");
                result.addAll(listReceivers(loc));
            }
        }
        
        else if(isReceiver(loc)){
            ListIterator<MTReceiver> it = allReceiverArray.listIterator();
            //Not sure why this doesn't find the second instance.
            while(it.hasNext()){
                MTReceiver tr = it.next();
                if(tr.getLocation().equals(loc)){
                    result.add("`YDirectReceiver: `a" + getReceiverInfo(tr) + "`Y.");
                    result.add("`YIt is part of the `a" + mtArray.get(tr.getParent()) + " `Yarray.");
                    result.add("`YThe transmitter is at `a" + tr.getParent().getX() + "," +
                            tr.getParent().getY() + "," + tr.getParent().getZ() + "`a .");
                }
            }
            
        } else {
            result.add("`RThis is not a MagicTorch.");
        }
        return result;
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
        ListIterator<MTReceiver> it = allReceiverArray.listIterator();
        while(it.hasNext()){
            if(loc.equals(it.next().getLocation()))
                return true;
        }
        return false;
    }
    
    /**
     * Returns a list of receivers for the clicked transmitter.
     * @param loc location of the transmitter torch.
     * @return 
     */
    public List<String> listReceivers(Location loc){
        List<String> result = new ArrayList<String>();
        ArrayList<MTReceiver> receivers;
        
        if(mtArray.containsKey(loc)){
            receivers = mtArray.get(loc).getReceiverArray();
            if(!receivers.isEmpty()){
                ListIterator<MTReceiver> it = receivers.listIterator();
                while(it.hasNext()){
                    result.add(getReceiverInfo(it.next()));
                }
            }
        }
        return result;
    }
    
    
    /***************************************************************************
    /****************************** PRIVATE ************************************
    /***************************************************************************/
    
    private void clearCache() {
        mtArray.clear();
        allReceiverArray.clear();
        this.message = "";
    }
    
    private String getReceiverInfo(MTReceiver tr){
        StringBuilder sb = new StringBuilder();
        
        if(tr instanceof DirectReceiver)
            sb.append("Direct");
        else if(tr instanceof DelayReceiver)
            sb.append("Delay");
        else if(tr instanceof InverseReceiver)
            sb.append("Inverse");
        else if(tr instanceof ToggleReceiver)
            sb.append("Toggle");
        else if(tr instanceof TimerReceiver)
            sb.append("Timer");
        else 
            sb.append("Unknown");
        
        sb.append(" receiver at ");
        sb.append("[").append(tr.getLocation().getWorld().getName()).append(": ");
        sb.append(tr.getLocation().getBlockX()).append(", ");
        sb.append(tr.getLocation().getBlockY()).append(", ");
        sb.append(tr.getLocation().getBlockZ()).append("]");
        
        sb.append(" It is a member of the ").append(mtArray.get(tr.getParent()).getName());
        sb.append(" array at ");
        sb.append("[").append(tr.getParent().getWorld().getName()).append(": ");
        sb.append(tr.getParent().getBlockX()).append(", ");
        sb.append(tr.getParent().getBlockY()).append(", ");
        sb.append(tr.getParent().getBlockZ()).append("]");
        
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
}
