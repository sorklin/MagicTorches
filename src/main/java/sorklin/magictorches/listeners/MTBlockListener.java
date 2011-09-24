package sorklin.magictorches.listeners;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import sorklin.magictorches.MagicTorches;

public class MTBlockListener extends BlockListener {
    private final MagicTorches mt;

    public MTBlockListener(MagicTorches mt) {
        this.mt = mt;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        super.onBlockBreak(event);
    }

    
}
