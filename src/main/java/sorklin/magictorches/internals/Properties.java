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
    
    /*
     * We'll store other properties here, including those we load from config.yml.
     */
    
    //types of torch relationships.
    public static final byte NONE = 0x0;
    public static final byte DIRECT = 0x1;
    public static final byte INVERSE = 0x2;
    public static final byte DELAY  = 0x4;
    public static final byte TIMER = 0x8;
    
    public static MTStorage db; //will be instantiated in main routine
    
    //From config.yml, when implemented.
    public static boolean loadChunkOnReceive = false;
    public static double toggleDelay = 1.5; //in seconds
    public static double timerDelay = 5; //in seconds
    private double maxDistance = 100.0; //TODO: drive this with a config setting.
    
    //Utility
    public static long toMillis(double seconds){
        return (long) seconds * 1000;
    }
}
