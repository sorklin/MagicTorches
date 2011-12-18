/*
 * Copyright (C) 2011 Sorklin <sorklin at gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sorklin.magictorches.internals;

import com.mini.Mini;
import java.io.File;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.interfaces.MTStorage;

/**
 *
 * @author Sorklin <sorklin at gmail.com>
 */
public class MiniStorage implements MTStorage {
    
    private Mini mb_database;
    private MagicTorches pl;
    private File miniDB;
    
    private void loadFromDB(){
        int torches = 0;
        String data = "";
        String owner = "";
        Location loc;
        TorchArray ta;
        
        for(String name: mb_database.getIndices().keySet()) {
            Arguments entry = mb_database.getArguments(name);
            owner = entry.getValue("owner");
            data = entry.getValue("data");
            MagicTorches.log(Level.FINER, "LoadDB data: " + data);
            try {
                loc = trLocationFromData(data);
                ta = torchArrayFromData(data, name, owner);
                if(loc != null && ta != null) {
                    mtArray.put(loc, ta);
                    mtNameArray.put(loc, name);
                    allReceiverArray.addAll(ta.getReceiverArray());
                    torches++;
                    MagicTorches.log(Level.FINE, "Loaded torch: " + name);
                } else {
                    this.message = name + "'s entry was malformed, or the transmitting"
                            + "torch is missing. Deleting entry from DB.";
                    MagicTorches.log(Level.INFO, this.message);
                    delete(name);
                }
            } catch (NullPointerException npe) {
                MagicTorches.log(Level.WARNING, "NPE on torch: " + name);
            } // just ignore for now
        }
        MagicTorches.log(Level.INFO, "Loaded " + torches + " magictorch arrays.");
    }
    
    private Location locationFromString(String data) throws NullPointerException {
        //World: (?<=name=)\w+
        //Coords: (?<==)-?\d+\.\d+  (returns 5 matches (x, y, z, yaw, pitch).
        
        //NPE if the world is NULL i.e., if MV or other multiverse plugin not loaded.
        
        String world = "";
        List<String> coords = new ArrayList<String>();
        Location result = null;
        
        Pattern p = Pattern.compile("(?<=name=)\\w+");
        Matcher m = p.matcher(data);
        if(m.find()){
            world = m.group();
        }

        p = Pattern.compile("(?<==)-?\\d+\\.\\d+");
        m = p.matcher(data);
        if(m != null) {
            while(m.find()) {
                coords.add(m.group());
            }
        }
        
        if(!world.isEmpty() && coords.size() == 5) {
            //Valid pull data.
            result = new Location(pl.getServer().getWorld(world), 
                    Double.valueOf(coords.get(0)), 
                    Double.valueOf(coords.get(1)), 
                    Double.valueOf(coords.get(2)));
        }
        return result;
    }
    
    private boolean saveToDB(Player player, TorchArray t){
        if(!t.isValid())
            return false;
        
        String name = t.getName();
        String data = t.toString();
        MagicTorches.log(Level.FINER, "Saving to DB:" + t.getName() + ", " + t.toString());
        Arguments entry = new Arguments(name.toLowerCase());
        entry.setValue("owner", player.getName());
        entry.setValue("data", data);
        mb_database.addIndex(entry.getKey(), entry);
        mb_database.update();
        //Now push onto working cache:
        mtArray.put(t.getLocation(), t);
        mtNameArray.put(t.getLocation(),t.getName());
        transmit(t.getLocation());
        return true;
    }
    
    private Location trLocationFromData(String data){
        
        if(!data.contains(";")){
            return null;
        }
        
        Location result = null;
        
        String[] sub = data.split(";");
        
        for(int i = 0, length = sub.length; i < length; i++){
            if(sub[i].contains("Transmitter")){
                result = locationFromString(sub[i]);
            }
        }
        return result;
    }
    
    private TorchArray torchArrayFromData(String data, String name, String owner){
        
        if(!data.contains(";"))
            return null; //can't split, this is malformed string
        
        String[] sub = data.split(";");
        if(sub.length < 3)
            return null; //Less than three segments is malformed.
                
        TorchArray ta = new TorchArray(owner);
        ta.setName(name);
        
        for(int i=0, length = sub.length; i < length; i++) {
            if(sub[i].contains("Transmitter")){
                //Only set the transmitter location if the location is not null,
                //AND there is a redstone torch already there.
                //IF transmitter left unset, the TorchArray will not be valid, causing the 
                //TA to be deleted from the db. (a good thing)
                Location loc = locationFromString(sub[i]);
                if(loc != null){
                    Material m = loc.getBlock().getType();
                    if(m.equals(Material.REDSTONE_TORCH_OFF) 
                            || m.equals(Material.REDSTONE_TORCH_ON)) {
                        ta.setTransmitter(loc);
                    }
                }
            } else 
            
            if(sub[i].contains("Receiver")){
                ta.add(locationFromString(sub[i]), typeFromString(sub[i]));
            }
        }
        return ((ta.isValid()) ? ta : null);
    }
    
    private byte typeFromString(String data){
        //type: (?<=Type{)\d{1,2}
        byte result = Properties.DIRECT; //Default to direct.

        Pattern p = Pattern.compile("(?<=Type\\{)\\d{1,2}");
        Matcher m = p.matcher(data);
        if(m.find()){
            result = Byte.parseByte(m.group());
        }
        return result;
    }
    
        /**
     * Prunes database of unloaded MTs.
     */
//    public void prune(){
//        for(String name: mb_database.getIndices().keySet()) {
//            if(!mtNameArray.containsValue(name)){
//                MagicTorches.log(Level.FINE, pl.g + "Could not find " + pl.b + name + pl.g 
//                        + " in active torch arrays.");
//                MagicTorches.log(Level.FINE, pl.g + "Pruning it from DB.");
//                mb_database.removeIndex(name);
//            }
//        }
//        mb_database.update();
//    }
}
