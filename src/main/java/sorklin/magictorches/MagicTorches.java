package sorklin.magictorches;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import sorklin.magictorches.commands.MTCreateCommand;
import sorklin.magictorches.commands.MTFinishCommand;
import sorklin.magictorches.commands.MTMainCommand;
import sorklin.magictorches.internals.MTorch;
import sorklin.magictorches.listeners.MTBlockListener;
import sorklin.magictorches.listeners.MTPhysicsListener;
import sorklin.magictorches.listeners.MTPlayerListener;
import sorklin.magictorches.listeners.MTPluginListener;


//TODO: info tool -- the switch.  Hold it and right click on torch to get any 
//      MT info available to you.

public class MagicTorches extends JavaPlugin {
    
    private final MTPhysicsListener physicsListener = new MTPhysicsListener(this);
    private final MTPlayerListener playerListener = new MTPlayerListener(this);
    private final MTPluginListener pluginListener = new MTPluginListener(this);
    private final MTBlockListener blockListener = new MTBlockListener(this);
    private PluginDescriptionFile pluginInfo;
    
    static final Logger log = Logger.getLogger("Minecraft");
    private String plugName;
    private boolean MV = true;
    
    public MTorch mt;
    
    protected static final String perm_create = "magictorches.create";
    protected static final String perm_admin = "magictorches.admin";
    
    public final ChatColor g = ChatColor.GOLD;
    public final ChatColor r = ChatColor.DARK_RED;
    public final ChatColor b = ChatColor.BLUE;
    public final ChatColor w = ChatColor.WHITE;
    
    public void onDisable() {
        spam("Plugin disabled.");
    }

    public void onEnable() {
        pluginInfo = getDescription();
        plugName = "[" + pluginInfo.getName().toString() + "] ";
        
        /* Load MINI and config here */
        File dbFile = new File(getDataFolder(), "mt.mini");
        if(!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException ex) {
                log.severe(plugName + "Error: " + ex.getMessage());
            }
        }
        
        //TODO: config to contain Multiverse: true/false.  Then I test.
        spam("db found or created.  Initializing MagicTorches.");
        mt = new MTorch(dbFile, this);
        
        //TODO: Figure out how to do aliases.
        getCommand("mtcreate").setExecutor(new MTCreateCommand(this));
        getCommand("mtfinish").setExecutor(new MTFinishCommand(this));
        getCommand("mt").setExecutor(new MTMainCommand(this));
        
        PluginManager pm = this.getServer().getPluginManager();
        //If we're using Multiverse, late load the db, otherwise load now.
        if(MV) {
            if(pm.getPlugin("Multiverse-Core").isEnabled()) {
                mt.reload();
            } else {
                pm.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Low, this);
            }
        } else {
            mt.reload();
        }
        
        pm.registerEvent(Type.PLAYER_INTERACT , playerListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_PHYSICS, physicsListener, Priority.High, this); //cancel redstone physics for the torches.
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        spam("Plugin initialized.");
    }
    
    public void spam(String msg) {
        log.info(plugName + msg);
        //Bukkit.getServer().broadcastMessage(plugName + msg);
    }
}
