package sorklin.magictorches.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import org.bukkit.event.block.BlockRedstoneEvent;
import sorklin.magictorches.MagicTorches;

public class MTBlockListener extends BlockListener {
    private final MagicTorches pl;

    public MTBlockListener(MagicTorches mt) {
        this.pl = mt;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()){
            return;
        }
        
        Block block = event.getBlock();
        Material mat = block.getType();
        
        if(mat.equals(Material.REDSTONE_TORCH_OFF) || mat.equals(Material.REDSTONE_TORCH_ON)) {
            if(pl.mt.isMT(block)){
                if(pl.mt.delete(block)) {
                    event.getPlayer().sendMessage
                        (pl.r + "MagicTorch transmitter " + pl.b + pl.mt.message + pl.r + " was deleted.");
                }
            }
        }
    }

    @Override
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if(pl.mt.isMT(event.getBlock())){
            MagicTorches.spamt("new current: " + event.getNewCurrent() + ", old current: " + event.getOldCurrent());
            pl.mt.transmit(event.getBlock().getLocation(), (event.getNewCurrent() == 0));
        }
    }   
}
