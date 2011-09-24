package sorklin.magictorches;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
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


public class MagicTorches extends JavaPlugin {
    
    private final MTPhysicsListener physicsListener = new MTPhysicsListener(this);
    private final MTPlayerListener playerListener = new MTPlayerListener(this);
    //private final mtPluginListener pluginListener = new mtPluginListener(this);
    private final MTBlockListener blockListener = new MTBlockListener(this);
    private PluginDescriptionFile pluginInfo;
    
    static final Logger log = Logger.getLogger("Minecraft");
    private String plugName;
    
    public MTorch mt;
    
    protected static final String perm_create = "magictorches.create";
    protected static final String perm_admin = "magictorches.admin";
    
    public final ChatColor g = ChatColor.GOLD;
    public final ChatColor r = ChatColor.DARK_RED;
    public final ChatColor b = ChatColor.BLUE;
    public final ChatColor w = ChatColor.WHITE;
    
    public void onDisable() {
        mt.close();
        spam("Plugin disabled.");
    }

    public void onEnable() {
        pluginInfo = getDescription();
        plugName = "[" + pluginInfo.getName().toString() + "] ";
        
        /* Load MINI and config here */
        
        mt = new MTorch(this);
        
        getCommand("mtcreate").setExecutor(new MTCreateCommand(this));
        getCommand("mtfinish").setExecutor(new MTFinishCommand(this));
        getCommand("mt").setExecutor(new MTMainCommand(this));
        
        PluginManager pm = this.getServer().getPluginManager();
        /*
        if(pm.getPlugin("Multiverse-Core").isEnabled()) {
            //mb.reload();
        } else {
            pm.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Low, this);
        }
         * 
         */
        pm.registerEvent(Type.PLAYER_INTERACT , playerListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_PHYSICS, physicsListener, Priority.High, this); //cancel redstone physics for the torches.
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        spam("Plugin initialized.");
    }
    
    public void spam(String msg) {
        log.info(plugName + msg);
        Bukkit.getServer().broadcastMessage(plugName + msg);
    } 
    
    public static void spamt(String msg) {
        log.info(msg);
        Bukkit.getServer().broadcastMessage(msg);
    } 
}
