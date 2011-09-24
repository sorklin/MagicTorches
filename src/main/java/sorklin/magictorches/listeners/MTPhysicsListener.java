package sorklin.magictorches.listeners;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import sorklin.magictorches.MagicTorches;

public class MTPhysicsListener extends BlockListener {
    private final MagicTorches mt;

    public MTPhysicsListener(MagicTorches mt) {
        this.mt = mt;
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        super.onBlockPhysics(event);
    }
}
