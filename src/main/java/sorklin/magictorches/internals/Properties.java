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

import sorklin.magictorches.internals.interfaces.MTStorage;

public class Properties {
    
    //types of torch relationships.
    public enum MtType {
        NONE,
        DIRECT,
        INVERSE,
        DELAY,
        TIMER
    }
    
    public static MTStorage db; //will be instantiated in main routine
    public static final String dbFileName = "mt.mini";
    
    //From config.yml, when implemented.
    public static boolean loadChunkOnReceive = false;
    public static double toggleDelay = 1.5; //in seconds
    public static double timerDelay = 5; //in seconds
    public static double maxDistance = 100.0; //Not implemented yet.  May not implement.
    public static boolean forceChunkLoad = false; 
    
    //Permissions
    public static final String permCreate = "magictorches.create";
    public static final String permCreateDelay = "magictorches.create.delay";
    public static final String permCreateDirect = "magictorches.create.direct";
    public static final String permCreateInverse = "magictorches.create.inverse";
    public static final String permCreateTimer = "magictorches.create.timer";
    public static final String permAdmin = "magictorches.admin";
}
