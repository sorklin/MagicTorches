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
        boolean handled = false;
        if(event.isCancelled())
            return;
        if(pl.mt.isReciever(event.getBlock())){
            event.setCancelled(true);
            handled = true;
        }
        if(!handled && event.getChangedType().equals(Material.REDSTONE_TORCH_ON)) {
            pl.spam("Unhandled RT_ON");
        }
        
        if(!handled && event.getChangedType().equals(Material.REDSTONE_TORCH_OFF)) {
            pl.spam("Unhandled RT_OFF");
        }
    }
}
