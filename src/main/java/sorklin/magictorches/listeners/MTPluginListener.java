package sorklin.magictorches.listeners;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import sorklin.magictorches.MagicTorches;

public class MTPluginListener extends ServerListener {
    private final MagicTorches mt;

    public MTPluginListener(MagicTorches mt) {
        this.mt = mt;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if("Multiverse-Core".equals(event.getPlugin().getDescription().getName())) {
            //mButton.log.info ("[MagicButtons] Found Multiverse.  Reloading db.");
            //pl.mb.reload();
        }
    }
    
}
