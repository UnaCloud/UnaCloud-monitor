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

import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.NfsFileSystem;

/**
 * Responsible for obtaining local Hard disk information
 */
public class HardDisk extends SigarWrapper {
	
	/**
	 * disk properties
	 */
    private long hardDiskSpace;
    private long hardDiskUsedSpace;
    private long hardDiskFreeSpace;
    private String hardDiskFileSystem="";
    
    /**
     * Class constructor
     * calculates and initializes disk variables 
     */
    public HardDisk() {
        try {
            FileSystem[] fslist = sigar.getFileSystemList();
            for (FileSystem fs : fslist)if(fs.getType()==FileSystem.TYPE_LOCAL_DISK){
                if (fs instanceof NfsFileSystem) {
                    NfsFileSystem nfs = (NfsFileSystem) fs;
                    if (!nfs.ping())continue;
                }
                long used,avail,total;
                FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
                used = usage.getTotal() - usage.getFree();
                avail = usage.getAvail();
                total = usage.getTotal();
                hardDiskSpace += total / 1024 / 1024;
                hardDiskUsedSpace += used / 1024 / 1024;
                hardDiskFreeSpace += avail / 1024 / 1024;
                hardDiskFileSystem += fs.getDirName()+" "+fs.getSysTypeName()+";";
            }
        } catch (SigarException ex) {
        }
    }

    /**
     * @return the HardDiskSpace
     */
    public long getHardDiskSpace() {
        return hardDiskSpace;
    }

    /**
     * @return the HardDiskUsedSpace
     */
    public long getHardDiskUsedSpace() {
        return hardDiskUsedSpace;
    }

    /**
     * @return the HardDiskFreeSpace
     */
    public long getHardDiskFreeSpace() {
        return hardDiskFreeSpace;
    }

    /**
     * @return the HardDiskFileSystem
     */
    public String getHardDiskFileSystem() {
        return hardDiskFileSystem;
    }
}//end of HardDisk

