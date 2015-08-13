/*
 * Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
 * This file is part of SIGAR.
 * 
 * SIGAR is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package physicalmachine;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

/**
 * Responsible for obtaining local RAM Memory information
 */
public class Memory extends SigarWrapper {
    
	/**
	 * Memory properties
	 */
    private float rAMMemorySize;
    private float rAMMemoryFree;
    private float rAMMemoryUsed;
    private float swapMemorySize;
    private float swapMemoryFree;
    private float swapMemoryPageIn;
    private float swapMemoryPageOut;
    private float swapMemoryUsed;

    /**
     * sigar memory representation
     */
    private Mem mem;
    /**
     * sigar swap representation
     */
    private Swap swap;

    /**
     * Class constructor
     * Calculates and initializes memory properties
     */
    public Memory() {
        try {
            mem = sigar.getMem();
            swap = sigar.getSwap();
            rAMMemorySize= mem.getRam();
            rAMMemoryFree= mem.getFree()/1024/1024;
            rAMMemoryUsed = mem.getUsed()/1024/1024;
            swapMemorySize = swap.getTotal()/1024/1024;
            swapMemoryFree = swap.getFree()/1024/1024;
            swapMemoryPageIn =swap.getPageIn()/1024/1024;
            swapMemoryPageOut = swap.getPageOut()/1024/1024;
            swapMemoryUsed = swap.getUsed()/1024/1024;
        } catch (SigarException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * getter methods
     */
     public double getRAMMemorySize() {
        return rAMMemorySize;
    }

    public double getRAMMemoryFree() {
        return rAMMemoryFree;
    }

    public double getRAMMemoryUsed() {
        return rAMMemoryUsed;
    }

    public double getSwapMemorySize() {
        return swapMemorySize;
    }
    
    public double getSwapMemoryFree() {
        return swapMemoryFree;
    }
    
    public double getSwapMemoryPageIn() {
        return swapMemoryPageIn;
    }
    
    public double getSwapMemoryPageOut() {
        return swapMemoryPageOut;
    }
    
    public double getSwapMemoryUsed() {
        return swapMemoryUsed;
    }

}//end of RAMMemory
