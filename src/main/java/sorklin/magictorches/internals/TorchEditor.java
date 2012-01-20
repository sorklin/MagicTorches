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

public class TorchEditor implements MTInterface {
    private Player player = null;
    private TorchArray ta = null;
    private MtType nextLinkType = MtType.DIRECT;
    private double timerValue = 0;
    
    public String message = null;
    
    
    public TorchEditor(Player player, TorchArray ta){
        this.ta = ta;
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
}
