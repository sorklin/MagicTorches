package sorklin.magictorches.listeners;

import org.bukkit.Material;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import sorklin.magictorches.MagicTorches;

public class MTPhysicsListener extends BlockListener {
    private final MagicTorches pl;

    public MTPhysicsListener(MagicTorches mt) {
        this.pl = mt;
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if(event.isCancelled())
            return;
        
        if(event.getChangedType().equals(Material.REDSTONE_TORCH_OFF) ||
                event.getChangedType().equals(Material.REDSTONE_TORCH_ON)) {
            if(pl.mt.isReciever(event.getBlock())){
                event.setCancelled(true);
            }
        }
    }
}
