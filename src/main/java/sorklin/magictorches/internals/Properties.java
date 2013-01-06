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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Properties {
    
    //types of torch relationships.
    public enum MtType {
        NONE (0),
        DIRECT (1),
        INVERSE (2),
        DELAY (8), //this is new torch.
        TIMER (16),
        TOGGLE (4);//this is the old delay torch
        
        private final int type;
        MtType(int type){ this.type = type; }
        public int getType(){ 
            return this.type; 
        }
        
        private static final Map<Integer,MtType> lookup = new HashMap<Integer,MtType>();

        static {
            for(MtType s : EnumSet.allOf(MtType.class))
                lookup.put(s.getType(), s);
        }

        public static MtType get(Integer type) { 
            return lookup.get(type); 
        }
    }
    
    public static final String dbFileName = "mt.mini";
    
    //From config.yml
    public static double toggleDelay;
    public static double delayDelay;
    public static double timerDelay;
    public static boolean useDistance;
    public static double maxDistance;
    public static boolean forceChunkLoad;
    public static boolean useEconomy;
    public static double priceArrayCreate;
    public static double priceArrayEdit;
    public static double priceDirect;
    public static double priceInverse;
    public static double priceDelay;
    public static double priceTimer;
    public static double priceToggle;
    
    public static int linesPerPage = 9;
    public static long stutterDelay = 500L; //Blocks repeat info events if under this amoutn of time (half second)
    
    //current state
    public static boolean disableTransmit = false;
    
    //Permissions
    public static final String permAccess = "magictorches.access";
    public static final String permCreateDelay = "magictorches.create.delay";
    public static final String permCreateDirect = "magictorches.create.direct";
    public static final String permCreateInverse = "magictorches.create.inverse";
    public static final String permCreateTimer = "magictorches.create.timer";
    public static final String permCreateToggle = "magictorches.create.toggle";
    public static final String permAdmin = "magictorches.admin";
}
