/**
* Copyright (C) 2011 Sorklin <sorklin@gmail.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package sorklin.magictorches.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import sorklin.magictorches.Events.TransmitEvent;
import sorklin.magictorches.internals.Properties;

/**
 *
 * @author Sork
 */
public class MTTorchSignalListener implements Listener {
    
    @EventHandler
    public void onMTTransmit(TransmitEvent event){
        //MagicTorches.spam("Event received for " + event.getTorchArray().toString());
        if(Properties.disableTransmit){
            //MagicTorches.spam("Disabled transmit.");
            return;
        }
        if(event.isInit())
            event.getTorchArray().init();
        else
            event.getTorchArray().transmit();
    }
}
