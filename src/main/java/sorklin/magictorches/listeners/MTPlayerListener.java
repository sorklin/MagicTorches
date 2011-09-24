package sorklin.magictorches.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import sorklin.magictorches.MagicTorches;


public class MTPlayerListener extends PlayerListener {
    private final MagicTorches pl;

    public MTPlayerListener(MagicTorches mt) {
        this.pl = mt;
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.isCancelled()) {
            return;
        }
        
        Player player = event.getPlayer();
        Action act = event.getAction();
        Block block = event.getClickedBlock();
        Material mat = block.getType();
        
        if(mat == Material.REDSTONE_TORCH_ON || mat == Material.REDSTONE_TORCH_OFF) {
            if(pl.mt.isInEditMode(player)) { //has to have perm_create, or wouldn't be in edit mode.
                if(act == Action.LEFT_CLICK_BLOCK) {
                    if(!pl.mt.isSetTransmitter(player, block)) {
                        if(pl.mt.setTransmitter(player, block)){
                            player.sendMessage(pl.g + "Selected transmitter torch.");
                        } else {
                            player.sendMessage(pl.r + "Could not set transmitter torch.");
                        }
                    }
                    event.setCancelled(true);
                } else if(act == Action.RIGHT_CLICK_BLOCK) {
                    if(pl.mt.setReceiver(player, block)) {
                        player.sendMessage(pl.g + pl.mt.message);
                    } else {
                        player.sendMessage(pl.r + "Could not add receiver torch.");
                    }
                }
            }
        }
    }
}
