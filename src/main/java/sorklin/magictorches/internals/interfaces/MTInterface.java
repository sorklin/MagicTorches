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
package sorklin.magictorches.internals.interfaces;

import org.bukkit.entity.Player;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.TorchArray;

public interface MTInterface {
    
    public Player getPlayer();
    public void setPlayer(Player player);
    
    public TorchArray getTorchArray();
    public void setTorchArray(TorchArray ta);
    
    public double getTimeOut();
    public void setTimeOut(double timeOut);
    
    public MtType getNextType();
    public void setNextType(MtType type);
    
    public String getMessage();
    
}
