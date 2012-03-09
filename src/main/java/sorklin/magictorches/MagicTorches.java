package sorklin.magictorches;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import sorklin.magictorches.commands.MTCommandExecutor;
import sorklin.magictorches.internals.MiniStorage;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.SimpleTorchHandler;
import sorklin.magictorches.internals.TorchEditor;
import sorklin.magictorches.listeners.MTPlayerListener;
import sorklin.magictorches.listeners.MTTorchSignalListener;
import sorklin.magictorches.listeners.MTWorldListener;

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

public class MagicTorches extends JavaPlugin {
    
    private final MTPlayerListener playerListener = new MTPlayerListener(this);
    private final MTWorldListener blockListener = new MTWorldListener(this);
    private final MTTorchSignalListener signalListener = new MTTorchSignalListener();
    
    private static MiniStorage miniDB;
    private static MagicTorches instance;
    
    public static Economy econ = null;
    
    //Torch creation 
    public final Map<Player, TorchEditor> editQueue = new HashMap<Player, TorchEditor>();
    public SimpleTorchHandler mtHandler;
    
    @Override
    public void onDisable() {
        //Cancel all outstanding tasks
        this.getServer().getScheduler().cancelTasks(this);
        log(Level.INFO, "Plugin disabled");
    }

    @Override
    public void onEnable() {
        MagicTorches.instance = this;
        
        log(Level.INFO, "Initializing MagicTorches");
        MagicTorches.miniDB = new MiniStorage(this);
        
        log(Level.INFO, "Loading config");
        loadConfig(getConfig());
        saveConfig();
        
        log(Level.INFO, "MiniDB found or created. Loading DB");
        mtHandler = new SimpleTorchHandler(this);
        
        log(Level.INFO, "Initializing commands and events");
        getCommand("mt").setExecutor(new MTCommandExecutor(this));

        getServer().getPluginManager().registerEvents(blockListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(signalListener, this);
        
        if(Properties.useEconomy){
            log(Level.INFO, "Attaching to economy (vault)");
            if (!setupEconomy()) {
                log(Level.WARNING, "Vault failed to hook into server economy plugin");
                Properties.useEconomy = false;
            } else
                log(Level.INFO, "Attached to vault");
        }
        
        log(Level.INFO, "Plugin initialized");
    }
    
    /**
     * Send message to log/players
     * @param msg 
     */
    public static void spam(String msg) {
        log(Level.INFO, msg);
        Bukkit.getServer().broadcastMessage("[MT] " + msg);
    }
    
    public static void log(Level l, String msg){
        instance.getLogger().log(l, msg);
    }
    
    public static MagicTorches get(){
        return instance;
    }
    
    public static MiniStorage getMiniDB(){
        return miniDB;
    }
    
    public void configReload(){
        log(Level.INFO, "Reloading config");
        reloadConfig();
        loadConfig(getConfig());
        saveConfig();
    }
    
    public MTTorchSignalListener getSignalListener(){
        return this.signalListener;
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private void loadConfig(FileConfiguration config) {
        config.options().copyDefaults(true);
        
        //General
        Properties.forceChunkLoad = config.getBoolean("Limits.ChunkLoadOnReceive");
        Properties.toggleDelay = config.getDouble("DefaultTimes.Toggle");
        Properties.delayDelay = config.getDouble("DefaultTimes.Delay");
        Properties.timerDelay = config.getDouble("DefaultTimes.Timer");
        Properties.useDistance = config.getBoolean("Limits.Distance");
        Properties.maxDistance = config.getDouble("Limits.MaxDistance");
        
        //Economy
        Properties.useEconomy = config.getBoolean("Economy.UseEconomy");
        Properties.priceArrayCreate = config.getDouble("Economy.Price.CreateArray");
        Properties.priceArrayEdit = config.getDouble("Economy.Price.EditArray");
        Properties.priceDirect = config.getDouble("Economy.Price.PerTorch.Direct");
        Properties.priceInverse = config.getDouble("Economy.Price.PerTorch.Inverse");
        Properties.priceDelay = config.getDouble("Economy.Price.PerTorch.Delay");
        Properties.priceTimer = config.getDouble("Economy.Price.PerTorch.Timer");
        Properties.priceToggle = config.getDouble("Economy.Price.PerTorch.Toggle");
    }
}
