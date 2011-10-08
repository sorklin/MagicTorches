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
        
        boolean rst = (mat.equals(Material.REDSTONE_TORCH_ON) || mat.equals(Material.REDSTONE_TORCH_OFF));
        
        if(rst) {
            if(pl.mt.isInEditMode(player)) { //has to have perm_create, or wouldn't be in edit mode.
                if(act.equals(Action.LEFT_CLICK_BLOCK)) {
                    if(!pl.mt.isSetTransmitter(player, block)) {
                        if(pl.mt.setTransmitter(player, block)){
                            player.sendMessage(pl.g + "Selected transmitter torch.");
                        } else {
                            player.sendMessage(pl.r + pl.mt.message);
                        }
                    }
                    event.setCancelled(true);
                } else if(act.equals(Action.RIGHT_CLICK_BLOCK)) {
                    if(pl.mt.setReceiver(player, block)) {
                        player.sendMessage(pl.g + pl.mt.message);
                    } else {
                        player.sendMessage(pl.r + pl.mt.message);
                    }
                }
            }
        }
        
        //Lets check for a switch in the hand (which indicates info request).
        if(rst || mat.equals(Material.TORCH)){
            //MagicTorches.spam("at rst || torch");
            if(act.equals(Action.RIGHT_CLICK_BLOCK) && 
                event.getItem().getType().equals(Material.LEVER)){
                //MagicTorches.spam("at lever click");
                if(MagicTorches.canCreate(player)){ //i have create, admin or op perms
                    //MagicTorches.spam("canCreate");
                    MagicTorches.listMessage(player, pl.mt.getInfo(block, player.getName(), 
                            MagicTorches.isAdmin(player), true));
                }
            } else 
            if(act.equals(Action.LEFT_CLICK_BLOCK) && 
                event.getItem().getType().equals(Material.LEVER)){
                event.setCancelled(true);//"Only you can prevent accidental breakages."
            }
        }
    }
}
