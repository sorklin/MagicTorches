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

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class MTUtil {
    
    /**
     * Does player have admin privileges, or is it from the console?
     * @param sender
     * @return <code>true</code> Yes, <code>false</code> No.
     */
    public static boolean isAdmin(CommandSender sender){
        return (sender.hasPermission(Properties.permAdmin) 
                || (sender instanceof ConsoleCommandSender) 
                || sender.isOp());
    }
    
    /**
     * Does player have specified permission?
     * @param sender
     * @return <code>true</code> Yes, <code>false</code> No.
     */
    public static boolean hasPermission(CommandSender sender, String perm){
        return (sender.hasPermission(perm) || sender.isOp());
    }
    
    /**
     * Returns the number of ticks a timer torch (and perhaps delay torch) should wait
     * before reverting.  Not using system clock, because lag will throw off calculations.
     * @param seconds number of seconds in 0.0
     * @return the number of ticks in the given number of seconds.
     */
    public static long secondsToTicks(double seconds){
        double t = seconds * 20; //20 ticks per second, in an ideal world.
        return Math.round(t);
    }
}
