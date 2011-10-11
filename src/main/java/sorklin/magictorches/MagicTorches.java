package sorklin.magictorches;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import sorklin.magictorches.commands.MTMainCommand;
import sorklin.magictorches.internals.MTorch;
import sorklin.magictorches.listeners.MTBlockListener;
import sorklin.magictorches.listeners.MTPhysicsListener;
import sorklin.magictorches.listeners.MTPlayerListener;
import sorklin.magictorches.listeners.MTPluginListener;



public class MagicTorches extends JavaPlugin {
    
    private final MTPhysicsListener physicsListener = new MTPhysicsListener(this);
    private final MTPlayerListener playerListener = new MTPlayerListener(this);
    private final MTPluginListener pluginListener = new MTPluginListener(this);
    private final MTBlockListener blockListener = new MTBlockListener(this);
    private PluginDescriptionFile pluginInfo;
    
    static final Logger log = Logger.getLogger("Minecraft");
    static String plugName;

    
    public MTorch mt;
    
    static final String perm_create = "magictorches.create";
    static final String perm_admin = "magictorches.admin";
    
    public static long delayTime = 1500;  //TODO: drive this by config file.
    
    public final ChatColor g = ChatColor.GOLD;
    public final ChatColor r = ChatColor.DARK_RED;
    public final ChatColor b = ChatColor.AQUA;
    public final ChatColor w = ChatColor.WHITE;
    
    public void onDisable() {
        spam("Plugin disabled.");
    }

    public void onEnable() {
        pluginInfo = getDescription();
        plugName = "[" + pluginInfo.getName().toString() + "] ";
        
        spam("Initializing MagicTorches.");
        /* Load MINI and config here */
        File dbFile = new File(getDataFolder(), "mt.mini");
        if(!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException ex) {
                log.severe(plugName + "Error: " + ex.getMessage());
            }
        }
        
        //TODO: config for last used default time.
        //TODO: distance in config setting.
        
        spam("MiniDB found or created. Loading DB.");
        mt = new MTorch(dbFile, this);
        
        getCommand("mt").setExecutor(new MTMainCommand(this));
        
        //Attempts to load and prune if MV is on.
        PluginManager pm = this.getServer().getPluginManager();
        if(pm.isPluginEnabled("Multiverse-Core")) {
            mt.reload();
            mt.prune();
        } else {
            pm.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
        }
        
        pm.registerEvent(Type.PLAYER_INTERACT , playerListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
        pm.registerEvent(Type.REDSTONE_CHANGE, blockListener, Priority.Monitor, this);
        spam("Plugin initialized.");
    }
    
    /**
     * Send message to log/players
     * @param msg 
     */
    public static void spam(String msg) {
        log.info(plugName + msg);
        //Bukkit.getServer().broadcastMessage("[MT]" + msg);
    }
    
    /**
     * Returns if the player has permission to create a TorchArray (or is admin)
     * @param player
     * @return <code>true</code> player has permission, <code>false</code> player 
     * does not have permission.
     */
    public static boolean canCreate(Player player){
        return (player.hasPermission(perm_create) || player.hasPermission(perm_admin));
    }
    
    /**
     * Returns if the player has permission to create a TorchArray (or is admin)
     * @param player
     * @return <code>true</code> player has permission, <code>false</code> player 
     * does not have permission.
     */
    public static boolean canCreate(CommandSender player){
        if(!(player instanceof Player))
            return false;
        return (player.hasPermission(perm_create) || player.hasPermission(perm_admin) 
                || player.isOp());
    }
    
    /**
     * Does player have admin privileges, or is it from the console?
     * @param sender
     * @return <code>true</code> Yes, <code>false</code> No.
     */
    public static boolean isAdmin(CommandSender sender){
        return (sender.hasPermission(perm_admin) || (sender instanceof ConsoleCommandSender) ||
                sender.isOp());
    }
    
    public static void listMessage(CommandSender sender, List<String> lines){
        for(String li : lines){
            sender.sendMessage(li);
        }
    }
}
