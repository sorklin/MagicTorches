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

import com.mini.Arguments;
import com.mini.Mini;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.Material;
import sorklin.magictorches.MagicTorches;
import sorklin.magictorches.internals.Properties.MtType;
import sorklin.magictorches.internals.interfaces.MTStorage;

public class MiniStorage implements MTStorage {
    
    private Mini mb_database;
    private MagicTorches mt;
    private File mini_file;

    public MiniStorage(MagicTorches mt){
        this.mt = mt;
        initFile();
    }
    
    private void initFile() {
        try {
            mini_file = new File(MagicTorches.get().getDataFolder(), Properties.dbFileName);
            if(!mini_file.exists())
                mini_file.createNewFile();
        
            mb_database = new Mini(mini_file.getParent(), mini_file.getName());
        } catch (IOException ioe) {
            MagicTorches.log(Level.WARNING, "Could not find/create/connect to " + Properties.dbFileName);
        }
    }
    
    public TorchArray load(String name) {
        String data;
        String owner;
        Location loc;
        TorchArray ta = null;
        
        if(!mb_database.hasIndex(name))
            return null;
        
        Arguments entry = mb_database.getArguments(name);
        owner = entry.getValue("owner");
        data = entry.getValue("data");
        MagicTorches.log(Level.FINER, "LoadDB data: " + data);
        try {
            loc = trLocationFromData(data);
            ta = torchArrayFromData(data, name, owner);
            if(loc != null && ta != null) {
                MagicTorches.log(Level.FINE, "Loaded torch: " + name);
            } else {
                MagicTorches.log(Level.INFO, name + "'s entry was malformed, or the transmitting" +
                        "torch is missing. Deleting entry from DB.");
            }
        } catch (NullPointerException npe) {
            MagicTorches.log(Level.WARNING, "NPE on torch: " + name);
        } catch (NumberFormatException nfe) {
            MagicTorches.log(Level.WARNING, "Number Format entry exception on torch " + name);
            MagicTorches.log(Level.WARNING, "Debug info:");
            nfe.printStackTrace();
        }
    
        return ta;
    }

    public boolean save(TorchArray ta) {
        //This saves the array, whether its valid or not.  Not the job 
        //here to determine good v. bad arrays.
        String name = ta.getName();
        String data = ta.toString();
        MagicTorches.log(Level.FINER, "Saving to DB:" + ta.getName() + ", " + ta.toString());
        Arguments entry = new Arguments(name.toLowerCase());
        entry.setValue("owner", ta.getOwner());
        entry.setValue("data", data);
        mb_database.addIndex(entry.getKey(), entry);
        mb_database.update();
        return true;
    }

    public HashMap<Location, TorchArray> loadAll() {
        int torches = 0;
        TorchArray ta;
        HashMap<Location,TorchArray> arrays = new HashMap<Location, TorchArray>();
        
        for(String name: mb_database.getIndices().keySet()) {
            ta = load(name);
            if(ta != null){
                torches++;
                MagicTorches.log(Level.FINE, "Loaded torch: " + ta.getName());
                arrays.put(ta.getLocation(), ta);
            }
        }
        
        MagicTorches.log(Level.INFO, "Loading " + torches + " magictorch arrays.");
        return arrays;
    }

    public boolean saveAll(HashMap<Location, TorchArray> ta) {
        for(Entry<Location, TorchArray> e : ta.entrySet()){
            save(e.getValue());
        }
        return true;
    }

    public boolean exists(String name) {
        return mb_database.hasIndex(name);
    }

    public boolean remove(String name) {
        boolean result = false;
        Arguments entry;
        if(mb_database.hasIndex(name)){
            entry = mb_database.getArguments(name);
            if(entry != null){
                mb_database.removeIndex(name);
                mb_database.update();
                result = true;
            }
        }
        return result;
    }

    public String getOwner(String name) {
        return (mb_database.hasIndex(name)) 
                ? mb_database.getArguments(name).getValue("owner")
                : "None";
    }
    
    public void removeAll() {
        for(Entry<String, Arguments> e : mb_database.getIndices().entrySet()){
            mb_database.removeIndex(e.getKey());
        }
        mb_database.update();
    }
    
    /**************
     *  PRIVATE   *
     **************/
    
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
            result = new Location(mt.getServer().getWorld(world), 
                    Double.valueOf(coords.get(0)), 
                    Double.valueOf(coords.get(1)), 
                    Double.valueOf(coords.get(2)));
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
        
        double delay;
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
            } 
            
            else if(sub[i].contains("Receiver")){
                delay = delayFromString(sub[i]);
                if(delay >= 0)
                    ta.add(locationFromString(sub[i]), typeFromString(sub[i]), delay);
                else 
                    ta.add(locationFromString(sub[i]), typeFromString(sub[i]));
            }
        }
        return ((ta.isValid()) ? ta : null);
    } 
    
    private MtType typeFromString(String data){

        //type: (?<=Type{)\d{1,2}
        MtType result = MtType.NONE; 

        Pattern p = Pattern.compile("(?<=Type\\{)\\d{1,2}");
        Matcher m = p.matcher(data);
        if(m.find())
            result = MtType.get(Integer.parseInt(m.group()));
        
        return result;
    }
    
    private double delayFromString(String data){
        
        //delay: (?<=Delay{)-{0,1}\d{1,3}\.{0,1}\d{0,5}
        double result = -1;

        Pattern p = Pattern.compile("(?<=Delay\\{)-{0,1}\\d{1,3}\\.{0,1}\\d{0,5}");
        Matcher m = p.matcher(data);
        if(m.find()){
            result = (double)Double.parseDouble(m.group());
        }
        
        return result;
    }
}
