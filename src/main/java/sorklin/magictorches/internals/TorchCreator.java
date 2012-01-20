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

import org.bukkit.entity.Player;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.interfaces.MTInterface;

public class TorchCreator implements MTInterface {
    
    private Player player = null;
    private TorchArray ta = null;
    private MtType nextLinkType = MtType.DIRECT;
    private double timerValue = 0;
    
    private String message = null;
    
    
    public TorchCreator(Player player){
        ta = new TorchArray(player.getName());
        this.player = player;
    }
    
    public void setPlayer(Player player){
        this.player = player;
        ta.setOwner(player.getName());
    }
    
    public void setNextType(MtType type){
        this.nextLinkType = type;
    }
    
    public void setTimeOut(double timeOut){
        this.timerValue = timeOut;
    }
    
    public MtType getNextType(){
        return this.nextLinkType;
    }
    
    /**
     * Creates a MagicTorch array from the previously input Transmitter and Receiver
     * selections.
     * @param player Player object of the person creating the array (the owner).
     * @param name The name of the array.
     * @return <code>true</code> if the Array was created, <code>false</code> if it was invalid.
     */
    public boolean create(){
//        if(Properties.db.isTorchArray(ta.getName().toLowerCase())){
//            this.message = "A MagicTorch array of that name already exists.";
//            return false;
//        }
//        
//        if(player != null){
//            if(ta.isValid()) {
//                if(Properties.db.save(ta)) {
//                    return true;
//                } else {
//                    this.message = "Failed to save MagicTorch array.";
//                }
//            } else {
//                this.message = "MagicTorch array not valid.";
//                if(!ta.transmitterSet())
//                    this.message = this.message + " [transmitter not selected]";
//                if(!ta.receiverSet())
//                    this.message = this.message + " [receivers not selected]";
//            }
//        }
        return false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public TorchArray getTorchArray() {
        return this.ta;
    }

    public void setTorchArray(TorchArray ta) {
        this.ta = ta;
    }

    public double getTimeOut() {
        return this.timerValue;
    }

    public String getMessage() {
        return this.message;
    }
    
        /**
     * Is the transmitter selected already set?
     * @param player Player doing the editing.
     * @param block the transmitter.
     * @return <code>true</code> if the block is already set as a transmitter.
     * <code>false</code> if the block is not a transmitter.
     */
//    public boolean isSetTransmitter(Player player, Block block) {
//        //This is a work around to the double event when the event it cancelled.
//        if(plTArray.containsKey(player)){
//            return plTArray.get(player).isTransmitter(block.getLocation());
//        }
//        return false;
//    }
    
}
