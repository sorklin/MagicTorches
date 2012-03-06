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
        
        StringBuilder msg = new StringBuilder();
        
        try {
            item = event.getItem().getType();
        } catch (NullPointerException npe) {
            //Do nothing because it means we have nothing in our hands, and we've
            //already defined it as air. 
        }
        
        boolean rst = (mat.equals(Material.REDSTONE_TORCH_ON) || mat.equals(Material.REDSTONE_TORCH_OFF));
        
        //FIRST, if we're not editing, handle the info event.
        if(!pl.editQueue.containsKey(player)){
            //Handle the Information action:
            //Lets check for a switch in the hand (which indicates info request).
            if(item.equals(Material.LEVER))
                if(rst || mat.equals(Material.TORCH)){
                    if(MTUtil.hasPermission(player, Properties.permAccess)){ //i have create, admin or op perms
                        Messaging.send(player, "`Y+--------------------------------------------------+");
                        Messaging.mlSend(player, pl.mtHandler.getInfo(
                            event.getClickedBlock(), 
                            player.getName(), 
                            MTUtil.hasPermission(player, Properties.permAdmin), 
                            true));
                    }
                    event.setCancelled(true);
                }
            return;
        }
        
        //We're editing, so lets load it up.
        TorchEditor te = pl.editQueue.get(player);
        
        //Now lets see if we're doing an info event.
        if(item.equals(Material.LEVER)){
            if(rst){ //editing changes all torches to redstone, so I don't screen for regular torches.
                Messaging.mlSend(player, te.getInfo(event.getClickedBlock().getLocation()));
                event.setCancelled(true);
            }
            return;
        }
        
        boolean added = false;
        
        //Process regular interaction.
        if(rst){
            if(act.equals(Action.LEFT_CLICK_BLOCK)) {
                event.setCancelled(true);
                if(te.isTransmitter(loc)) //Avoids double selection message.
                    return;
                
                te.setTransmitter(loc);
                msg.append ("`gSelected transmitter torch.");
                if(Properties.useEconomy && !player.hasPermission(Properties.permAdmin))
                    msg.append("%cr%`YCurrent subtotal: `a")
                        .append(MagicTorches.econ.format(te.priceArray()));
                
                added = true;
                
                
                
            } else if(act.equals(Action.RIGHT_CLICK_BLOCK)) {
                if(te.isReceiver(loc)){
                    te.remove(loc);
                    msg.append("`gYou `wREMOVED `gthe receiver from the array.");
                    added = true;
                }
                else if(te.getNextType() == MtType.DELAY || te.getNextType() == MtType.TIMER || te.getNextType() == MtType.TOGGLE) {
                    if(player.hasPermission(Properties.permAdmin))
                        added = te.add(loc, te.getNextType(), te.getTimeOut());
                    else 
                        added = te.addCheck(loc, te.getNextType(), te.getTimeOut());
                    
                    if(added){
                        msg.append("`gAdded a `w").append(te.getNextType().toString()).append("`g torch with a ");
                        msg.append((te.getTimeOut() == -1) ? "default" : te.getTimeOut() + "s");
                        msg.append(" delay ");
                        if(Properties.useEconomy && !player.hasPermission(Properties.permAdmin))
                            msg.append("(").append(priceOfReceiver(te.getNextType())).append(")");
                        msg.append(".");
                        msg.append("%cr%`yTo remove a receiver from the array, right click it again.");
                    }
                } else {
                    if(player.hasPermission(Properties.permAdmin))
                        added = te.add(loc, te.getNextType());
                    else
                        added = te.addCheck(loc, te.getNextType());
                        
                    if(added){
                        msg.append("`gAdded a `w").append(te.getNextType().toString()).append("`g torch");
                        if(Properties.useEconomy && !player.hasPermission(Properties.permAdmin))
                            msg.append(" (").append(priceOfReceiver(te.getNextType())).append(")");
                        msg.append(".");
                        msg.append("%cr%`yTo remove a receiver from the array, right click it again.");
                    }
                }
                if(added && Properties.useEconomy && !player.hasPermission(Properties.permAdmin))
                    msg.append("%cr%`YCurrent subtotal: `a") 
                        .append(MagicTorches.econ.format(te.priceArray()));
                
                event.setCancelled(true);
            }
            if(added)
                Messaging.send(player, msg.toString());
            else
                Messaging.send(player, te.getMessage());
        }
    }
    
    private String priceOfReceiver(MtType type){
        String result;
        
        if(MagicTorches.econ == null)
            return "";
        
        switch (type){
            case DELAY:
                result = MagicTorches.econ.format(Properties.priceDelay);
                break;
            case DIRECT:
                result = MagicTorches.econ.format(Properties.priceDirect);
                break;
            case INVERSE:
                result = MagicTorches.econ.format(Properties.priceInverse);
                break;
            case NONE:
                result = MagicTorches.econ.format(0);
                break;
            case TIMER:
                result = MagicTorches.econ.format(Properties.priceTimer);
                break;
            case TOGGLE:
                result = MagicTorches.econ.format(Properties.priceToggle);
                break;
            default:
                result = MagicTorches.econ.format(0);
                break;
        }
        
        return result;
    }
}
