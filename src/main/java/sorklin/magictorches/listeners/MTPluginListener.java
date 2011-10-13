package sorklin.magictorches.listeners;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import sorklin.magictorches.MagicTorches;

public class MTPluginListener extends ServerListener {
    private final MagicTorches pl;

    public MTPluginListener(MagicTorches mt) {
        this.pl = mt;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        String name = event.getPlugin().getDescription().getName();
        if(name.equals("Multiverse-Core") || name.equals("Multiverse")) {
            pl.spam("Connected to Multiverse.  Loading db.");
            pl.mt.reload();
            pl.mt.prune();
        }
    }
    
}
