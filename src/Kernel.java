import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/*
 * this should be member type of Scheduler 
 */


public class Kernel extends Scheduler {

	// init
	public UserlandProcess up;
	PriorityEnum priority; 
	OS os = new OS(); 
	static ArrayList<KernalandProcess> waitingQueue = new ArrayList<KernalandProcess>(); 
	
	
	static KernalandProcess kland = new KernalandProcess();  // for open 
	static HashMap<String, Integer> nameMap = new HashMap<String, Integer >(); // store name and pid. 

	// create global boolean array FreeTable
	//public static ArrayList<Boolean> FreeTable = new ArrayList<Boolean>(1024);  
	static int MaxLength = 100; 
	public static boolean[] FreeTable = new boolean[MaxLength]; 
	
	public static boolean isUsed; // isDirty in lecture slide. 
	int physicalPageNumber;
	int virtualPageNumer; 
	
	
	/**
	 * initialize boolean type FreeTable content as false 
	 * @param up
	 */
	public static void initFreeTable() {
		for(int i=0; i < FreeTable.length; i++) {
			FreeTable[i] = false; 
		}
	}
	/*
	 * Constructor with UserlandProcess parameter
	 */
	public Kernel(UserlandProcess up) {
		this.up = up; 
		
	}
	/**
	 * constructor
	 */
	public Kernel() {
		// TODO Auto-generated constructor stub
		initFreeTable(); 
	}
	/**
	 * boolean method to set as boolean value as isUsed
	 */
	public static void setIsUsed( boolean value) {
		isUsed = value;  // if value is true then used. 
	}
	
	/**
	 * getter for physical page number 
	 */
	public int getPhysicalPageNumber() {
		return this.physicalPageNumber; 
	}
	/**
	 * setter for physical page number 
	 */
	public void setPhysicalPageNumber(int pnumber) {
		this.physicalPageNumber = pnumber; 
	}
	/**
	 * getter for virtual page number
	 */
	public int getVirtualPageNumber() {
		return this.virtualPageNumer; 
	}
	/**
	 * setter for virtual page number 
	 */
	public void setVirtualpageNumber(int vnumber) {
		this.virtualPageNumer = vnumber; 
	}
	
	
	/**
	 * This method is from Kernel class that creates process with UserlandProcess parameter.
	 * this method calls Scheduler's CreateProcess(). 
	 * @param init
	 * @return
	 */
	public static int CreateProcess(UserlandProcess up, int memorySize) {
		// call scheduler.CreateProcess(up); 
		//set userland process
			return Scheduler.Createprocess(up, memorySize);
		
	}
	/**
	 * CreateProces with UserlandProcess up and PriorityEnum priority 
	 */
	public static int CreatProcess(UserlandProcess up, PriorityEnum priority, int memorySize) {
		return Scheduler.Createprocess(up, priority, memorySize);
	}
	/**
	 * CreateProcess for Object type 
	 */
	public static Object CreateProcess(Object object, int memorySize) {
		return Scheduler.Createprocess(object, memorySize);
	}
/*
 * this is from Runnable
 */
	public void run() {
		// TODO Auto-generated method stub
		kland.run();
	}
/**
 * Connect between OS sleep() -> Kernel Sleep() Scheduler Sleep()
 * @param milliseconds
 */
public static void Sleep( int milliseconds) {
	Scheduler.Sleep(milliseconds);
}

// new methods for assignment 3 
//implemented method from the Device class. 

/**
*  For Open(), use getCurrentlyRunning() and find an empty (-1) entry in the kerneland process’ array. (intArray size = 10) 
*   If there isn’t one, return -1 (fail).
*    Then call vfs.open.
*     If the result is -1, fail. 
*     Otherwise, put the id from vfs into the kerneland process’ array and return that array index. 
*/

public int Open(String s) {
	// TODO Auto-generated method stub
//	KernalandProcess process = KernalandProcess.getCurrentlyRunning(); 
	Scheduler scheduler = new Scheduler(); // instance 
	scheduler.setCurrentDevice(s); 
	
	return KernalandProcess.Open(s);
}


public void Close(int id) {
	// TODO Auto-generated method stub
	// when you closed the device then make it null , make empty spot in array. 
	
	// we need to use array to  convert to vfs. 
	// set kerneland array intArray entry to the -1 when we close the device 
	
	// search given id index in intArray 
	for(int i = 0; i < KernalandProcess.intArray.length; i++) {
		if(KernalandProcess.intArray[i] == id) {
			// we found device id that we have to close 
			VFS vfs = new VFS(); 
		     vfs.Close(id);   // close id=id device in VFS 
		     KernalandProcess.intArray[i] = -1; // -1 indicates null, so empty the spot  
		}else {
			// we couldn't find the id in kernerland array 
			continue; // false 
		}
	}
	KernalandProcess.Close(id);
	
}

/**
* For the other methods (read, write, seek), 
* we get an id from userland which is OS 
*  use the array in kerneland to convert 
*  that to the id that vfs expects 
*  and then pass the call through to the vfs. 
*/

public byte[] Read(int id, int size) {
	// TODO Auto-generated method stub
	// 1. get id from userland - OS  - I create Read(id, size) in userland and it will call Kernel's read() - linked!
	  
	// there are not empty spot in intArray 
	System.out.println("read in kernel");
	return  kland.Read(id, size);
}
/**
 * sent to kerneland Seek()
 * @param id
 * @param to
 */
public void Seek(int id, int to) {
	// TODO Auto-generated method stub
	kland.Seek(id, to);
}
/**
 * send to Kerneland Write() 
 * @param id
 * @param value
 * @return
 */
public static int Write(int id, byte[] value) {
	// TODO Auto-generated method stub
	return kland.Write(id, value); 
	
}


/**
 * int getPid() 
 */
public int GetPid() {
	// return currently running process's id. 
	return currentlyRunningProcess.getProcessId(); 
}


/**
 * OS GetPidByName() call Kernel GetPidByName() 
 * Kernel GetPidByName() will get process id for the specific name 
 * @param name
 * @return
 */
public static int GetPidByName(String name) {
	// TODO Auto-generated method stub
	// int pid = 0 ; 
	// In KLP, I stored userlandprocess's name in String processName; 
	for(Map.Entry<String, Integer> map : nameMap.entrySet()) { // key value 
		if(map.getKey() == name) {
			int pid = map.getValue(); 
			System.out.println("Kernel GetPidByName(" + name + ")" + "pid : " + pid); 
			// create message queue for this process 
			// process's message queue does not exist then create one 
			Queue<KernelMessage> queue = new LinkedList<>(); 
			kland.MessageQueueForProcesses.put(pid, queue); 
			
			return pid; 
		}else {
			continue; 
		}
	}
	return -1;
}

/**
 * OS.SendMessage(km) -> Kernel.SendMessage(km) 
 * 
 */
public static void SendMessage(KernelMessage km) {
	System.out.println("\n--------------------In Kernel-SendMessage()--------------------------------"); 
	System.out.println("What passed in Kernel SendMEssage's km parameter  : " + km.toString()); 
	
	KernalandProcess targetProcess = null; // destination kernelandprocess 
	KernelMessage kMessage = new KernelMessage(km);  // copy constructur copy original message 
	
	
	//populate sender's pid 
	// int senderPid = Scheduler.currentlyRunningProcess.getProcessId();
	System.out.println("Currently running process pid : " + Scheduler.currentlyRunningProcess.getProcessId()); 
	System.out.println("Check sender pid : " + kMessage.getSenderPid()); 
	
	int receiverPid = kMessage.getReceiverPid();
	System.out.println("Check Receiver pid  : " + receiverPid); 
	
	
	// find target process and its message queue exist -> then add message. 
	targetProcess = Scheduler.getProcess(receiverPid); 
	
	// create receiver process's message queue 
	if (kland.MessageQueueForProcesses.containsKey(receiverPid)) {
		// if find targetProcess's  message queue then - add km 
				Queue<KernelMessage> queue = kland.MessageQueueForProcesses.get(receiverPid); 
				queue.add(kMessage); 
	}else {
		// targetProcess's messageQueue is not existed -> create one 
		Queue<KernelMessage> mqueue = new LinkedList<>(); 
		mqueue.add(kMessage); 
		kland.MessageQueueForProcesses.put(receiverPid, mqueue); 
	}
	// targetProcess = kland.map.get(km.receiverPid); // find kernalandprocess that has receiver's pid. 
	System.out.println("Target pid  : " + kMessage.getReceiverPid() + " Targetprocess: " + targetProcess); 

}

/**
 * Os calls Kernel.WaitForMessage() 
 * 
 * @return
 */
public static KernelMessage WaitForMessage() {
	
	System.out.println("\n-------------------------- In WaitForMessage() --------------------------------"); 
	KernalandProcess currentP = Scheduler.currentlyRunningProcess; 
	//System.out.println("CurrentP pid : " + currentP.getProcessId());
	
	
	// check if current process has message 
	if(kland.MessageQueueForProcesses.containsKey(currentP.getProcessId())) {
		// if currentp's process id is in hashmap's key value -> check its message queue 
		Queue<KernelMessage> MessageQueue = kland.MessageQueueForProcesses.get(currentP.getProcessId());  // MessageQueue is currentProcess's message queue 
		if(!MessageQueue.isEmpty()) {
			// messageQueue is not empty -> return message 
			// iterate over queue 
			System.out.println("message queue is not empty -> run process and print msg");
			Iterator<KernelMessage> iter = MessageQueue.iterator(); 
			while(iter.hasNext()) {
				KernelMessage km  = iter.next(); 
				System.out.println("wait for message : " +  km.getMsg()); 
			}
			Scheduler.currentlyRunningProcess = currentP; 
			currentP.run(); 	// maybe Scheduler.currentlyRunningProcess.run() 
			//pop this process from  MessageQueueForProcesses, and map 
		
		}else {
			// else if (kland.MessageQueueForProcesses.getkey(i) == null ) {  
			currentP.sleep(250);  //de-schedule  so wait for message. 
		}
    }else {
		// iterate over MessageQueueForProcesses and it there is process that has msg then print and run 
		// HashMap <Integer, Queue<KernelMessage>> 
		for(Map.Entry<Integer, Queue<KernelMessage>> set : kland.MessageQueueForProcesses.entrySet()) {
		if(set.getValue()!= null) {
			// so this message queue is not empty 
			Queue<KernelMessage> mQueue = set.getValue(); 
			Iterator<KernelMessage> iter2 = mQueue.iterator();  // create iterator for mQueue
			while(iter2.hasNext()) {
				KernelMessage km = iter2.next(); 
				System.out.println("Wait for message : "  + km.getMsg()); 
			}
			// find process that has msg 
			int processId = set.getKey(); 
			KernalandProcess newProcess = Scheduler.getProcess(processId); 
			newProcess.run(); 
			
		}else {
			// case that set.getValue(), so process's message queue is null or empty -> then wait more 
			continue; 
			
		}
	}
		// case the process we put it sleep, finally got message -> then we can run this process 
		for(int i =0; i < Scheduler.sleepingQueue.size(); i++) {
			 // iterate over sleeping queue 
		  KernalandProcess Sprocess = Scheduler.sleepingQueue.get(i); 
		  
		  long currentTime = System.currentTimeMillis();  // get current time. 
		  long currentlyLeftedTime = Sprocess.getLeftedTime(); // return how many mins Sprocess need to sleep 
		  if(currentlyLeftedTime <=currentTime) {
			 Queue<KernelMessage> SMessageQueue = kland.MessageQueueForProcesses.get(Sprocess.getProcessId()); // this return Sprocess's message queue 
			 if(!SMessageQueue.isEmpty()) {
				 // is this message queue is not empty we can run 
				Sprocess.run(); 
			 }
		  }

		}
	}
	
	return null;
}

/**
 * allocate block of memory of specified size 
 * use Kernel's FreeTable to keep track free and allocated pages
 * KLP's pageTable index's value = FreeTable index 
 * @return returns the start virtual address
 */
public static int AllocateMemory(int size) {
	
	int StartingVAddress = 0;  // staring virtual address 
	int LastVirtualAddress  = 0; // last virtaul address 
	System.out.println("Kernel. AllocateMemory requested size : " + size);
	
	if((size % 1024) != 0 ) {   // size and pointer should me multiple of 1024 
		return -1;  // fail case 
		
	}else {
		// size is multiply of 1024. Good 
		int numberOfPages = size/1024; 
		System.out.println("Kernel. numberOfpages : " + numberOfPages); 
		
		// Find "contiguous" free spots in the KernelandProcess's pageTable
        int[] continuousFreeSpots = getContigousFreeSpot(numberOfPages);
       
        if (continuousFreeSpots != null) {
            // Update the FreeTable
        	int count = FindUpdateFreeSpot(numberOfPages); 
        	if(count!= -1) {
        		System.out.println("We succesfully found number of : " + numberOfPages + " FreeSpot."); 
        	}else {
        		System.out.println("We don't have memory space in physical memory- FAILED. "); 
        		
        	}

            // Update the pageTable and TLB mappings
            int firstFreeSpot = continuousFreeSpots[0];
            System.out.println("Check founded first free spot in pageTable : " + firstFreeSpot ); 
            
            for (int i = 0; i < numberOfPages; i++) {
                int virtualPageNumber = firstFreeSpot + i;
                int physicalPageNumber = continuousFreeSpots[i];
                KernalandProcess.pageTable[virtualPageNumber] = physicalPageNumber; 
               //  KernalandProcess.pageTable.add(virtualPageNumber, physicalPageNumber); // index , content in pageTable
                System.out.println("\n" + "[In PageTable] Index[ " + virtualPageNumber + "] PhysicalPageNumber : " + physicalPageNumber ); 

                // Store in TLB
                int index = getEmptySpotInTLB();  // iterate over UserlandProcess's virtualAddressList and return empty spot index.
                UserlandProcess.virtualAddressList[index] = virtualPageNumber ;
                UserlandProcess.physicalAddressList[index] =  physicalPageNumber;
                System.out.println("Store in TLB index : " + index + " virtualPageNumber : " + virtualPageNumber + " PhysicalPageNumber : " + physicalPageNumber); 
               
                
            			}
            StartingVAddress = firstFreeSpot * 1024;  
            System.out.println("\n[Kernel] Starting virtual address : " + StartingVAddress); 
            LastVirtualAddress = numberOfPages  * 1024; 
            System.out.println("[Kernel] Last Virtual Address : " + LastVirtualAddress); 
            
        }
        else {
            System.out.println("Kernel: Failed to allocate memory -> call Kernel.FreeMemory()");
         // Call FreeMemory to free up the memory space
            Kernel.FreeMemory(StartingVAddress, size);
            return -1;
        }
    }
	System.out.println("Allocating Memory from :" + StartingVAddress + " to " + LastVirtualAddress); 
	System.out.println("------------------------------------------------------------------\n");
    
    return  StartingVAddress;
}

/**
 * Iterate over UserlandProcess's virtualAddressList and return empty spot index 
 * @return index 
 */
private static int getEmptySpotInTLB() {
	// TODO Auto-generated method stub
	int index = 0; // init 
	for(int i =0; i < UserlandProcess.virtualAddressList.length; i++) {
		if(UserlandProcess.virtualAddressList[i] == -1) {
			return i; 
		}
	}
	
	return index; 
}
/**
 * we have to find "contiguous" free pages to allocate given size in memory. 
 * This method will find contiguous free 
 */
private static int[] getContigousFreeSpot(int numberOfPages) {
	int[]  continousFreeSpots = new int[numberOfPages]; // ex) if I need 3 pages to allocate 
	int count = 0; // increment this count until you reach to numberOfPages 
	System.out.println("Kernel.getContigousFreeSpot with numberOfPages : " + numberOfPages); 
	
	for(int i =0; i < KernalandProcess.pageTable.length; i++) {
		if(KernalandProcess.pageTable[i] == -1) {
				// found free spot in pageTable
			continousFreeSpots[count] = i; //in index = count, I stored FreeTable index that I used 
			System.out.println("[+]Kernel.getContigousFreeSpot counting free spot in pageTable index : " + i +  " count : " + count); 
			count++; 
			
				if(count == numberOfPages) {
					// we don't need to explore anymore
					System.out.println("Kernel.getContigousFreeSpot() - found all free spot. total count : " +count + " \n"); 
					return continousFreeSpots;  // return list of free spot array. 
				}
		}else {
			count = 0;  // we need continuous spot so reset count as 0 
		}
	}
	return null; // failed case 
}

/**
 * boolean FreeMemory 
 *  frees a block of memory specified by given virtual address and size 
 */

public static boolean FreeMemory(int pointer, int size) {
	
	if((pointer % 1024 )!= 0){
		System.out.println("Unable to FreeMemory pointer is not multiple of 1024"); 
		return false; 
	}else if((size % 1024) != 0 ) {
		System.out.println("Unable to FreeMemory size is not multiple of 1024");
		return false; 
	}else {
		// case : we can free memory address 
		int fvirtualPageNumber = pointer/ 1024;  // first/starting virtual page to free. 
		int numberOfPage = size / 1024;   // shows how many pages we have to free 
		int outputSize  = numberOfPage * 1024; 
		
		// mark FreeTable isUsed = false 
		for(int i = 0; i < numberOfPage; i++) {
			int virtualPageNumber = fvirtualPageNumber + i; // so we can get continuous virtual page number to free
			//int physicalPageNumber = KernalandProcess.pageTable[virtualPageNumber]; 
			
			// free FreeTable for amount of number of pages 
			for(int k =0; k  < FreeTable.length; k++) {
				if(FreeTable[k] == true) {
					FreeTable[k] = false; 
					System.out.println("[FreeMemory] : FreeTable[" + k + "] as False"); 
				}
			}
			
			// find virtualPageNumber and physicalPageNumber's index position ( stored location in TLB) 
			for(int j =0; j < UserlandProcess.virtualAddressList.length ; j++) {
				if(UserlandProcess.virtualAddressList[j] == fvirtualPageNumber) { // found index. 
					
					// set virtual page number and physical page number in TLB as default value -1 
					UserlandProcess.virtualAddressList[j] = -1 ; 
					UserlandProcess.physicalAddressList[j] = -1 ; 
				}
			}
			
			// clear KernalandProcess's pageTable entry, index is virtualPageNumber 
			KernalandProcess.pageTable[virtualPageNumber] = -1; 
			
		}  // repeat as much as number of page we have to remove/free 
		
		System.out.println("[Freeing Memory] from : " + pointer + " to " + outputSize); 
		return true; 
	} 
}
/**
 * Find Free spot in FreeTable , it doesn't have to be continous spot. for request number of virtual address page 
 * @return 
 */
public static  int  FindUpdateFreeSpot(int vPage) {
	int count = 0;   // default failed case 
	for(int i=0 ; i < FreeTable.length; i++) {
		if(FreeTable[i] == false) {  // case empty spot founded 
			if(count == vPage) { // case we found all needed spot
				System.out.println("Finishing updating FreeSpot ... "); 
				return count; 
			}else {
				FreeTable[i]= true; 
				System.out.println("[Kernel] : Update FreeTable[" + i + "] as true"); 
				count+=1; 
			}
			
		}
	}
	return -1;
}

/**
 * clear all TLB values, when process dies. 
 */
public static void TLBCleared() {
    int val = -1; 
	
    Arrays.fill(UserlandProcess.virtualAddressList,val );   // Clear TLB set -1 as default value. 
    Arrays.fill(UserlandProcess.physicalAddressList, val);

	
	// clear KernalandProcss's pageTable 
	//KernalandProcess.pageTable.remove(pageTableIndex);  
	//int PhysicalPageNumber = KernalandProcess.pageTable.get(pageTableIndex); 
	//Kernel.FreeTable.remove(PhysicalPageNumber);  // use physical page number to clear freetable. 
}


}

