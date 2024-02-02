import java.security.SecureRandom;
import java.util.Random;
import java.util.TimerTask;

public class OS extends TimerTask implements Runnable {
// Everything is this class should be static 
//private static reference to the only one instance of Kernel class 
	
   private static Kernel kernel;
  // KernalandProcess kland = new  KernalandProcess(); 
   static UserlandProcess init = null; // for OS class 
   private static OS osClassInstance; 
   Scheduler scheduelr = new Scheduler(); 
   VFS vfs = new VFS(); 
   int deviceID; // store device id 
   int kindex;   // for klp array int
   
   private static Random random = new SecureRandom(); 
   KernalandProcess klp = new KernalandProcess(); 
   
  
    
   /*
    * Constructor
    */
	public OS() {
		
	}
	
	/** 
	 * getter for OS
	 * @return
	 */
	public static OS getOS() {
		//if there is no osClassInstance -> we can create
		if(osClassInstance == null) {
			osClassInstance = new OS();
		}
		return osClassInstance; 

	}
/**
 * This method will populate the Kernel member with a new instance 
 * and call CreateProcess on “init”. 
 */

	public static void StarUp(UserlandProcess init, int memorySize) {
		// OS.getOS().CreateProcess();
		 // populate Kernel member 
		    kernel = new Kernel();
			//OS.CreateProcess(init);  // call CreateProcess with init
		    OS.CreateProcess(init, PriorityEnum.Interactive, memorySize);
		    
		}
	

/** 
 * OS class's CreateProcess method calls Kernel class's CreateProcess method 
 * @param up
 * @return
 */
	public static Object CreateProcess(Object object, int memorySize) {
		return Kernel.CreateProcess(object, memorySize);  //scheduler.CreateProcess(up); 
		
	}
	
	public static int CreateProcess(UserlandProcess up , int memorySize) {
		return Kernel.CreateProcess(up, memorySize);  //scheduler.CreateProcess(up); 
		
	}
/**
 *  CreateProcess method with UerlandProcesss and priorityEnum 
 */
public static int CreateProcess(UserlandProcess up, PriorityEnum priority, int memorySize ) {
	// return Scheduler.Createprocess(up, priority);
	 return Kernel.Createprocess(up, priority, memorySize);
}

/**
* run Userlandprocess
* @return 
*/
	public  void run() {
		Scheduler.Switchprocess();  // run scheduler. 
	
	}
	
	/*
	 * Crate Sleep() method (static type) 
	 * put process to sleep for specific number of millseconds.
	 */
	public static void Sleep(int milliseconds) {
		Kernel.Sleep(milliseconds);
		// call sleep in OS() call sleep in Kernel() that call sleep in Scheduler. 
	}
	
// new for assignment 3 
	
	/**
	 * Make OS, Userland to commmunicate with KernelandProcess -> 
	 * VFS for read write open seek methods 
	 * 
	 * open the device -> kernel open -> VFS open 
	 */
	
		public int open(String string ) {
			// TODO Auto-generated method stub
			System.out.println("\nOS.open call kernelandprocess open");
			deviceID = vfs.Open(string);  // this will return id from vfs device id array, and this will be stored in kernaland process int array. 
			// track of currenltly open device's id 
			scheduelr.setCurrentDevice(string);   // set device name 
			scheduelr.setCurrentDeviceId(deviceID);  // set device id 
			System.out.println("KernalandProcess array index :" + deviceID);
			return KernalandProcess.Open(string);  // kernalandprocess open returns index of intArray where device is stored. 
		}
	/**
	 * Close method 
	 * close given id device and make related all array's position to be null 
	 */
	public void Close(int id) {
		// go to scheduler -> vfs and close given id device.

		// free the space 
		KernalandProcess.Close(id);    // close intArray(), KernalandProcess int array. index = id 
		 
	}
	/**
	 * read the device -> kernel read -> VFS read 
	 * @return 
	 */
	
		public byte[] Read(int id, int size) {
			// TODO Auto-generated method stub
			return  kernel.Read(id, size); 
		}
		
	/**
	 * search device in OS -> kernel -> VFS  Seek()
	 * @param id
	 * @param to
	 */
		
		 public void Seek(int id,int to) {
			 kernel.Seek(id, to);
		 }
	/**
	 * Write on device id = id -> kernel -> VFS write()
	 * @param id
	 * @param value
	 * @return
	 */
		 public int Write(int id, byte[] value) {
			 
			 return Kernel.Write(id, value); 
		 }
		 
 /**
  * int getPid() 
  */
public int GetPid() { 
// return currently running process's id. 
   return Scheduler.currentlyRunningProcess.getProcessId(); 
}
/**
 * int GetPidByName(String)
 * GetPidByName("ping") = 2. 2 is process id of ping userlandprocess
 */
public int GetPidByName(String name) {
	// name will be userlandprocess name 
	//ex) OS.GetPidByName("pong") then return pong pid 
	return Kernel.GetPidByName(name); 
}

/**
 * void Sendmessage(KernelMessage km) 
 *
 * messages are in messageQueue in KLP 
 */
public void SendMessage(KernelMessage km ) {
	//call Kernel SendMessage 
	Kernel.SendMessage(km); 
	
}
/**
 * 
 */
public KernelMessage WaitForMessage() {
	// call Kernel
	return Kernel.WaitForMessage(); 
}

/**		 
 * Call Kernel AllocateMemory() 
 */
public int AllocateMemory(int size) {
	return Kernel.AllocateMemory(size); 
}

/**
 * Call Kernel FreeMemory() 
 */
		 
public boolean FreeMemory(int pointer, int size ) {
	// pointer : starting virtual address in pageTable in KLP and
	// size is how many index we should free 
	// Also free the booleanArray in Kernel. 
	
	return Kernel.FreeMemory(pointer, size); 
}

/**
 * Mapping virtual address to physical address mapping in TLB 
 * Fill KernalandProcess's pageTable 
 * Mark Kernel's boolean FreeTable as used. 
 * 
 * @param virtualPageNumber
 */
public static void GetMapping(Integer virtualPageNumber) {
	// TODO Auto-generated method stub
	// get virtualPageNumber's index position 
//	int ppNumber = -1; // default
	System.out.println("[OS.GetMapping] virtualPageNumber : " + virtualPageNumber); 
	
	int vIndex = getEmptyIndex(); 
	//System.out.println("vINdex : " + vIndex); 
	if(vIndex != -1) {  // success to find index 
		// create physical page number randomly 
		int randomIndex = random.nextInt(100); 
		// add to KLP's pageTable
		KernalandProcess.pageTable[virtualPageNumber]  = randomIndex; 
		// KernalandProcess.pageTable.set(virtualPageNumber, randomIndex);  // randomIndex = physical page number 
		// mark as isUsed = true in Kernel boolean array 
		// pageTable content, so physical page number == FreeTable index 
		int count = Kernel.FindUpdateFreeSpot(virtualPageNumber); //find free spot and update as true. 
		if(count != -1) {
			// case we succesfully found empty spot this indicate we can allocate more meory. 
			UserlandProcess.virtualAddressList[vIndex] = virtualPageNumber ; // TLB
			UserlandProcess.physicalAddressList[vIndex] = randomIndex ;   // TLB
			System.out.println("Successfully mapped virtual Page Number: " + virtualPageNumber + " into physical page number : " + randomIndex ); 
			
		}else if(count == -1) {
			System.out.println("NO space to allocate in physical memory. FAILED "); 
		}
		
		
		//return physical page number 
		//return randomIndex ; 
	} 
	else {
		System.out.println("virtualPageNumber : " +  virtualPageNumber + " is not in virtualAddressList ( TLB) "); 
	  //  return ppNumber; 
	}
	
	
}
/**
 * TLB has 2 arraylist, one is virtualAddressList and other is PhysicalAddressList. 
 * If virtualpageNumber is stored in TLB's UserlandProcess's virtualAddressList, 
 * return index postion, so I can store physicalPageNumber in same index position in physicalAddressList 
 * 
 * Get empty index in TLB virtualAddressList 
 * 
 */
public static int getEmptyIndex( ) {
	for(int i=0; i < UserlandProcess.virtualAddressList.length; i++) {  //iterate over list 
		if(UserlandProcess.virtualAddressList[i] == -1 ) {
			return i; 
		}
		
	}
	return -1; // fail to find index. 
}

}
