package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.losandes.utils.OperatingSystem;

import monitoring.exceptions.MonitoringException;

public class LoaderDll {
	
	private final String lib = "/monitoring/sigar/";
	private OperatingSystem os;
	private String path;
		
	public LoaderDll(String p) {
		if(p==null)new Exception("DATA_PATH can't be null");
		os = new OperatingSystem();
		path = p;
	}
	public void loadLibrary() throws MonitoringException {
		System.out.println("Reload sigar library");
		if(os.isWindows())
			if(os.isAMD64())load("sigar-amd64-winnt.dll");
			else load("sigar-x86-winnt.dll");			
		else if(os.isUnix())
			if(os.isAMD64())load("libsigar-amd64-linux.so");
			else if(os.isX86())load("libsigar-x86-linux.so");	
			else if(os.isIA())load("libsigar-ia64-linux.so");
			else if(os.isPpc64())load("libsigar-ppc64-linux.so");
			else if(os.isPpc())load("libsigar-ppc-linux.so");
		else if(os.isSolaris())
			if(os.isX86())load("libsigar-x86-solaris.so");
			else if(os.isAMD64())load("libsigar-amd64-solaris.so");
			else if(os.isSparc64())load("libsigar-sparc64-solaris.so");
			else if(os.isSparc())load("libsigar-sparc-solaris.so");
	    else if(os.isMac())
			if(os.isAMD64())load("libsigar-universal64-macosx.dylib");
			else load("libsigar-universal-macosx.dylib");	
	    else if(os.isFreeBSD())
	    	if(os.isAMD64())load("libsigar-amd64-freebsd-6.so");
	    	else if(os.isX86())load("libsigar-x86-freebsd-6.so");
	    else if(os.isHpUx())
	    	if(os.isIA())load("libsigar-ia64-hpux-11.sl");
	    	else if(os.isPaRisc())load("libsigar-pa-hpux-11.sl");
	    else if(os.isAix())
	    	if(os.isPpc64())load("libsigar-ppc64-aix-5.so");
	    	else if(os.isPpc())load("libsigar-ppc-aix-5.so");			
		//System.setProperty("java.library.path", System.getProperty("java.io.tmpdir")+";"+System.getProperty("java.library.path"));
		if(!System.getProperty("java.library.path").contains(path))
			System.setProperty("java.library.path", path+";"+System.getProperty("java.library.path"));
	}
	
	private void load(String file){
		try {
			InputStream in = getClass().getResourceAsStream(lib+file);
		    byte[] buffer = new byte[1024];
		    int read = -1;
		    File fileToLoad = new File(path+file);
		   // File temp = File.createTempFile(file, "");
		    FileOutputStream fos = new FileOutputStream(fileToLoad);
		    while((read = in.read(buffer)) != -1) {
		        fos.write(buffer, 0, read);
		    }
		    fos.close();
		    in.close();
		    System.load(fileToLoad.getAbsolutePath());	
		} catch (Exception e) {
		}		
	}
	
}
