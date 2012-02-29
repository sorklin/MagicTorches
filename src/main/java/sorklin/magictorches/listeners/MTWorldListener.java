package sorklin.magictorches.listeners;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import sorklin.magictorches.Events.RecieveEvent;
import sorklin.magictorches.Events.TransmitEvent;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.Messaging;

public class MTWorldListener implements Listener {
    private final MagicTorches pl;

    public MTWorldListener(MagicTorches mt) {
        this.pl = mt;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        Material mat = event.getBlock().getType();
        
        if(mat.equals(Material.REDSTONE_TORCH_OFF) || mat.equals(Material.REDSTONE_TORCH_ON)) {
            if(pl.mtHandler.isMT(loc)){
                String mtName = pl.mtHandler.getArray(loc).getName();
                if(pl.mtHandler.removeArray(loc)) {
                    MagicTorches.getMiniDB().remove(mtName);
                    Messaging.send(event.getPlayer(), "`rMagicTorch transmitter `w" + pl.mtHandler.getMessage() + "`r was deleted.");
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        //MagicTorches.spam("RS: " + event.getBlock().getLocation().toString());
        if(pl.mtHandler.isMT(event.getBlock().getLocation())){
            //MagicTorches.spam("is mt.");
            Bukkit.getServer().getPluginManager()
                    .callEvent(new TransmitEvent(pl.mtHandler.getArray(event.getBlock().getLocation())));
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onMTReceive(RecieveEvent event){
        //MagicTorches.spam("RS: " + event.getBlock().getLocation().toString());
        if(pl.mtHandler.isMT(event.getLocation())){
            //MagicTorches.spam("is mt.");
            Bukkit.getServer().getPluginManager()
                    .callEvent(new TransmitEvent(pl.mtHandler.getArray(event.getLocation())));
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Block block;
        Iterator<Block> itr = event.blockList().iterator();
        while(itr.hasNext()){
            block = itr.next();
            if(pl.mtHandler.isMT(block.getLocation())){
                String mtName = pl.mtHandler.getArray(block.getLocation()).getName();
                String owner = pl.mtHandler.getArray(block.getLocation()).getOwner();
                if(pl.mtHandler.removeArray(block.getLocation())) {
                    MagicTorches.getMiniDB().remove(mtName);
                    Player p = Bukkit.getServer().getPlayer(owner);
                    if(p != null)
                        Messaging.send(p, "`rMagicTorch transmitter `w" + pl.mtHandler.getMessage() + "`r exploded.");
                }
            }
        }
    }    
}
