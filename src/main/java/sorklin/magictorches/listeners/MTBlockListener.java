package sorklin.magictorches.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.TransmitEvent;

public class MTBlockListener implements Listener {
    private final MagicTorches pl;

    public MTBlockListener(MagicTorches mt) {
        this.pl = mt;
    }

    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        Material mat = event.getBlock().getType();
        
        if(mat.equals(Material.REDSTONE_TORCH_OFF) || mat.equals(Material.REDSTONE_TORCH_ON)) {
            if(pl.mtHandler.isMT(loc)){
                if(pl.mtHandler.removeArray(loc)) {
                    Messaging.send(event.getPlayer(), "`rMagicTorch transmitter `w" + pl.mtHandler.getMessage() + "`r was deleted.");
                }
            }
        }
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        //MagicTorches.spam("RS: " + event.getBlock().getLocation().toString());
        if(pl.mtHandler.isMT(event.getBlock().getLocation())){
            //MagicTorches.spam("is mt.");
            Bukkit.getServer().getPluginManager()
                    .callEvent(new TransmitEvent(pl.mtHandler.getArray(event.getBlock().getLocation())));
        }
    }   
}
