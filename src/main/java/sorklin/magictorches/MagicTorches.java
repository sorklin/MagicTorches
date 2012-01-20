package sorklin.magictorches;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import sorklin.magictorches.commands.MTMainCommand;
import sorklin.magictorches.internals.MTorchHandler;
import sorklin.magictorches.internals.TorchCreator;
import sorklin.magictorches.listeners.MTBlockListener;
import sorklin.magictorches.listeners.MTPlayerListener;
import sorklin.magictorches.listeners.MTPluginListener;

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

//TODO: new torch receiver: delay (true delay) waits some amount of time then acts upon signal.
//TODO: new torch receiver: toggle (got to get a better name).  acts like a timed torch -- 
//      toggles itself, then after a timed period, toggles back. 
//TODO: support Vault (and the economy systems)
//TODO: add YAML config for settable options.
//TODO: config for last used default time.
//TODO: distance in config setting.

public class MagicTorches extends JavaPlugin {
    
    private final MTPlayerListener playerListener = new MTPlayerListener(this);
    private final MTPluginListener pluginListener = new MTPluginListener(this);
    private final MTBlockListener blockListener = new MTBlockListener(this);
    private PluginDescriptionFile pluginInfo;
    
    private static final Logger logr = Logger.getLogger("Minecraft");
    private static String plugName;
    private static MagicTorches instance;
    
    //Torch creation 
    public final Map<Player, TorchCreator> todo = new HashMap<Player, TorchCreator>();
    
    public MTorchHandler mt;
    
    public void onDisable() {
        log(Level.INFO, "Plugin disabled.");
    }

    public void onEnable() {
        MagicTorches.instance = this;
        pluginInfo = getDescription();
        plugName = "[" + pluginInfo.getName().toString() + "] ";
        
        log(Level.INFO, "Initializing MagicTorches.");
        /* Load MINI and config here */
        File dbFile = new File(getDataFolder(), "mt.mini");
        if(!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException ex) {
                log(Level.SEVERE, plugName + "Error: " + ex.getMessage());
            }
        }
        
        log(Level.INFO, "MiniDB found or created. Loading DB.");
        mt = new MTorchHandler(dbFile, this);
        
        getCommand("mt").setExecutor(new MTMainCommand(this));
        
        //Attempts to load and prune if MV is on.
        PluginManager pm = this.getServer().getPluginManager();
        if(pm.isPluginEnabled("Multiverse-Core") || pm.isPluginEnabled("Multiverse")) {
//            mt.reload();
//            mt.prune();
        } else {
            pm.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
        }
        
        pm.registerEvent(Type.PLAYER_INTERACT , playerListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
        pm.registerEvent(Type.REDSTONE_CHANGE, blockListener, Priority.Monitor, this);
        log(Level.INFO, "Plugin initialized.");
    }
    
    /**
     * Send message to log/players
     * @param msg 
     */
    public static void spam(String msg) {
        log(Level.INFO, msg);
        //Bukkit.getServer().broadcastMessage("[MT] " + msg);
    }
    
    public static void log(Level l, String msg){
        logr.log(l, plugName + msg);
    }
    
    public static MagicTorches get(){
        return instance;
    }
}
