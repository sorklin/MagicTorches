package sorklin.magictorches.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.MTUtil;
import sorklin.magictorches.internals.Messaging;
import sorklin.magictorches.internals.Properties;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.TorchEditor;


public class MTPlayerListener implements Listener {
    private final MagicTorches pl;

    public MTPlayerListener(MagicTorches mt) {
        this.pl = mt;
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        
        Player player = event.getPlayer();
        Action act = event.getAction();
        Location loc = event.getClickedBlock().getLocation();
        Material mat = event.getClickedBlock().getType();
        Material item = Material.AIR;
        
        String msg = "";
        
        try {
            item = event.getItem().getType();
        } catch (NullPointerException npe) {
            //Do nothing because it means we have nothing in our hands, and we've
            //already defined it as air. 
        }
        
        boolean rst = (mat.equals(Material.REDSTONE_TORCH_ON) || mat.equals(Material.REDSTONE_TORCH_OFF));
        
        //TODO: Fix this -- lever should provide info no matter what.
        
        //Handle the Information action:
        //Lets check for a switch in the hand (which indicates info request).
        if(item.equals(Material.LEVER)){
            if(rst || mat.equals(Material.TORCH)){
                if(MTUtil.hasPermission(player, Properties.permAccess)){ //i have create, admin or op perms
                    Messaging.mlSend(player, pl.mtHandler.getInfo(
                            event.getClickedBlock(), 
                            player.getName(), 
                            MTUtil.hasPermission(player, Properties.permAdmin), 
                            true));
                    event.setCancelled(true);
                    return;
                }
            }
        }
        
        //Handle torch creation/editing.
        if(!pl.editQueue.containsKey(player))
            return;
        TorchEditor te = pl.editQueue.get(player);
        
        if(rst){
            if(act.equals(Action.LEFT_CLICK_BLOCK)) {
                te.setTransmitter(loc);
                msg =  "`gSelected transmitter torch.";
                if(Properties.useEconomy)
                    msg += "`Y%cr%Current array price: " 
                            + MagicTorches.econ.format(te.priceArray());
                event.setCancelled(true);
            } else if(act.equals(Action.RIGHT_CLICK_BLOCK)) {
                if(te.isReceiver(loc)){
                    te.remove(loc);
                    msg = "`gRemoved receiver from array.";
                }
                else if(te.getNextType() == MtType.DELAY ||
                        te.getNextType() == MtType.TIMER ||
                        te.getNextType() == MtType.TOGGLE) {
                    te.add(loc, te.getNextType(), te.getTimeOut());
                    msg = "`gAdded a " + te.getNextType().toString() + " torch with a ";
                    msg += (te.getTimeOut() == -1) ? "default" : te.getTimeOut();
                    msg += " delay.";
                } else {
                    te.add(loc, te.getNextType());
                    msg = "`gAdded a " + te.getNextType().toString() + " torch";
                }
                
                if(Properties.useEconomy)
                    msg += "%cr%`YCurrent array price: " 
                            + MagicTorches.econ.format(te.priceArray());
                event.setCancelled(true);
            }
            Messaging.send(player, msg);
        }
    }
}
