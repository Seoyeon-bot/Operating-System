import java.lang.Thread.State;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Timer; 
import java.util.TimerTask; 
/**
 * This class holds list of process
 * @author choeseoyeon
 *
 */

public class Scheduler {

	//  extends TimerTask
	
	// Queue : list of process for kernelandProcess class 
    static ArrayList<KernalandProcess>  processQueue = new ArrayList<KernalandProcess>();  // collect kernaland process 
    
	//static int  new_processID = -1 ;
	public static KernalandProcess kprocess; // use it in OS.java
    public static UserlandProcess up; 
	private volatile static boolean exit = false;   // I will use this method instead of stop(),since my eclipse deosn't let me use stop() method. 
	
	// reference to KernelandProces that is currently running 
	 public static KernalandProcess currentlyRunningProcess ;

	// constructors for classes
	static KernalandProcess klp = new KernalandProcess(); // constructor for kernalandprocess calss.
	Interrupt interrupt = new Interrupt(); 
	static Scheduler scheduler  = new Scheduler(); 

   // Create 3 different process Queue for Realtime process, Interactive process, and background process
	static ArrayList<KernalandProcess>  realTimeQueue = new ArrayList<KernalandProcess>();  
	static ArrayList<KernalandProcess> interactiveQueue = new ArrayList<KernalandProcess>();  
	static ArrayList<KernalandProcess> backgroundQueue = new ArrayList<KernalandProcess>();  
	static ArrayList<KernalandProcess> sleepingQueue = new ArrayList<KernalandProcess>();  
	static ArrayList<Integer> deviceQueue = new ArrayList<>();  // queue for the device or make if for kland
	
	static int curLeftedTime ; // holds current process's remaining/lefted sleeping time 
	int currentRunTime; // holds current process's current run time. 
	static int number; // generated by Java Random() 
	int number2; // use this in run() 
	
	//Java clock 
	Timer timer = new Timer();
    static long  clock = System.currentTimeMillis(); // get current time 
	
	// get instance object of clock object in milliseconds. 
	
	 // count will be used to track how many time each process ran. 
	
    VFS vfs = new VFS();  // use this when we track device. 
	String currentDeviceName; // device name 
	int id; // device id 
	
	
	// Hashmap< pid, kernelandprocess> 
	static HashMap<Integer, KernalandProcess> map = new HashMap<Integer, KernalandProcess>();
	
	// I store those value when allocate memory and use those to FreeMemory() 
	static HashMap<Integer, MemoryInfo > PidAndMemory = new HashMap<Integer, MemoryInfo>(); // store pId, starting address in memory , size 
	
	/**
	 * Constructor with timer 
	 */
	public Scheduler() {
		
	   // timer.schedule(interrupt, 100000, 600000);
	}
	/**
	 * return kernalandprocess from its process id 
	 * @param processId
	 * @return
	 */
	public static KernalandProcess getProcess(int processId ) {
		for(Map.Entry<Integer, KernalandProcess> iter : map.entrySet()) {
			if(iter.getKey() == processId) {
				return iter.getValue(); 
			}
		}
		return null;
	}
	
	/*
	* Private class inside of Scheduler called "interrupt" 
	*/
	private class Interrupt extends TimerTask{
		// require a run method 
		@Override
		public void run() {
			 // call Scheduler's SwitchProcess() 
			System.out.println("timer task invoked!");
			Switchprocess();
		}
		
	}
	 
    
	
	/**
	 * CreateProcess method : this will add process to kernalandprocess queue and if nothing is running 
	 * it will call Switchprocess() 
	 * returns ID
	 */
	public static int Createprocess(UserlandProcess up, int memorySize) {
		System.out.println("create kprocess. "); 
		//construct new KernelandProcess 
		klp.processID +=1; 
		kprocess = new KernalandProcess(up);  // create kernalandprocess 
		 
		
		if(kprocess != null) {
			kprocess.setProcessID(klp.processID);  // set process ID 
			// add to processQueue 
			//processQueue.add(kprocess);
			// get name of kprocess 
			klp.processName = up.getClass().getSimpleName();
			
			String name = up.toString(); 
			if (name.contains("o")) {
				name = "Pong"; 
			}else if(name.contains("i")) {
				name = "Ping"; 
			}
			//System.out.println("up : " + name);
			Kernel.nameMap.put(name, kprocess.getProcessId()); 
			map.put(kprocess.getProcessId(), kprocess); 
			
			// put process, starting address in memory and size in pidAndMemory hashmap 
			int startingAddress = Kernel.AllocateMemory(memorySize);  // returns physical Memory's starting Address 
			System.out.println("Scheudler. CreateProcess(), startingAddress : " + startingAddress); 
			
			if (startingAddress != -1) { // check
				PidAndMemory.put(kprocess.getProcessId() , new MemoryInfo(startingAddress, memorySize));  // store 
				System.out.println("[ Put in PidAndMemory HashMap ] processId : " + kprocess.getProcessId() + ", starting address : "+ startingAddress + ", its memory size : "+ memorySize+ "\n"); 
		  }	
		}
		
			 // if nothing is running 
		 if (currentlyRunningProcess  == null ) {   // Fitem is currently running top process. 
			 Switchprocess();
		 }else {
			 //something is running 
			 System.out.println("somthing is running - >return new_processID");
			 return kprocess.getProcessId();
		 }
		 
		return  kprocess.getProcessId();
	}
	
/**
 * CretaProcess for the device process
 * I setted device, deviceID when I call OS.open(), so I will pop the pop device id/device 
 * and use that for the process that I have to run for this device. 
 */
	public static Object Createprocess(Object object, int memorySize) {
		// TODO Auto-generated method stub
		System.out.println("create kprocess. "); 
		//construct new KernelandProcess 
	//	String deviceName = scheduler.getCurrentDevice();
		int deviceId = scheduler.getCurrentDeviceID(); 
		
		// set deviceID as kernalandprocess ID so later we can track of devices easily
		klp.processID = deviceId; 
		// klp.processID +=1; 
		kprocess = new KernalandProcess(deviceId);  // create kernalandprocess
				
		if(kprocess != null) {
			kprocess.setProcessID(klp.processID);  // set process ID 
			// add to processQueue 
		//	processQueue.add(kprocess);
			klp.processName = up.getClass().getSimpleName(); 
			

			String name = up.toString(); 
			if (name.contains("o")) {
				name = "Pong"; 
			}else if(name.contains("i")) {
				name = "Ping"; 
			}
			Kernel.nameMap.put(name, kprocess.getProcessId()); 
			map.put(kprocess.getProcessId(), kprocess); 
			
			// put process, starting address in memory and size in pidAndMemory hashmap 
			int startingAddress = Kernel.AllocateMemory(memorySize);  // returns physical Memory's starting Address 
			if (startingAddress != -1) { // check
					PidAndMemory.put(kprocess.getProcessId() , new MemoryInfo(startingAddress, memorySize));  // store 
			  }			
		}
		
		 if (currentlyRunningProcess == null) {   // Fitem is currently running top process. 
			 Switchprocess();
		 }
		 
		
		return  kprocess.getProcessId();
	}
	/**
	 * CrateProcess new kernalandprocess using userlandprocess from user land. 
	 * Mange process in queue and linked lsit of KernalandProcess 
	 * 
	 * @param up
	 * @param priority
	 * @return processID 
	 */
	public static int Createprocess(UserlandProcess up, PriorityEnum priority, int memorySize) {
		// TODO Auto-generated method stub
		//increment processId when you create new process. 
		System.out.println("create kprocess with priority."); 
		klp.processID +=1;  
		kprocess  = new KernalandProcess(up, priority);  // create new kernaland process 
		
		if(kprocess != null) {
			kprocess.setProcessID(klp.processID);  // set process ID 
			kprocess.setPriority(priority); //set process pririty
			// add to processQueue 
		//	processQueue.add(kprocess);
			klp.processName = up.getClass().getSimpleName();
			// add to klp hashmap< pid, kernelandprocess> use this for sendmessage() 
			Kernel.nameMap.put(klp.processName, kprocess.getProcessId()); 
			map.put(kprocess.getProcessId(), kprocess); 

			String name = up.toString(); 
			if (name.contains("o")) {
				name = "Pong"; 
			}else if(name.contains("i")) {
				name = "Ping"; 
			}
			
			Kernel.nameMap.put(name, kprocess.getProcessId()); 
			map.put(kprocess.getProcessId(), kprocess); 
			
			// put process, starting address in memory and size in pidAndMemory hashmap 
			int startingAddress = Kernel.AllocateMemory(memorySize);  // returns physical Memory's starting Address 
			if (startingAddress != -1) { // check
					PidAndMemory.put(kprocess.getProcessId() , new MemoryInfo(startingAddress, memorySize));  // store 
			}		
		}
	
		PriorityEnum kprocess_priority = kprocess.getPriority(); // get priority 
		
		if(kprocess_priority == PriorityEnum.Realtime) {
		//	System.out.println("put this in realtime queue."); 
			// DO i need to set in the queue? ex) kprocess.setRightQueue(1)? 
			realTimeQueue.add(kprocess);  // add to realtime queue 
			
		}else if(kprocess_priority == PriorityEnum.Interactive) {
			//System.out.println("put this in interactive queue."); 
			interactiveQueue.add(kprocess);  // add to realtime queue 
			 
		}else if(kprocess_priority == PriorityEnum.Background) {
			//System.out.println("put this in background queue."); 
			backgroundQueue.add(kprocess);  // add to realtime queue 
		}
	
		// if nothing is running ex) kprocess is not stared or current thread is not avlive 
		if( currentlyRunningProcess  == null ) {
						 Switchprocess(); 
				     
		}else {
				 System.out.println("somthing is running - >return new_processID");
				return kprocess.getProcessId();
		}
		
		return kprocess.getProcessId();
	}
	
	/**
	 * Sleep()
	 * when process call sleep, add requested millisecond time sleep to current clock value 
	 * That is going to be minimum wakeup time. 
	 * When process call Sleep(), 
	 * switch processes, put process in sleepingQueue ( order maters) 
	 * stop the process. 
	 * Sleep() in Kernel calls Scheduler Sleep() 
	 */
	
	public static void Sleep(int milliseconds) {
		if(currentlyRunningProcess != null ) { // || kprocess != null
			System.out.println( currentlyRunningProcess + " is started  but I will put this in Sleeping Queue"); 
			System.out.println("Process id : " +  currentlyRunningProcess.getProcessId()); 
			
			long Sleeptime = clock + milliseconds; 
			currentlyRunningProcess.setLeftedTime(Sleeptime); // still need to sleep. 
			sleepingQueue.add(currentlyRunningProcess);  //  put the process in a separate list for sleeping processes.
			
		    // Switchprocess(); 
		}
		
	}

	/**
	 * Awake process that were sleeping () from sleepingQueue and add to right queue. 
	 * 
	 */
	public static void wakeUp(KernalandProcess wakeupProcess) {
		// wake up sleeping process for milliseconds 
		// On a task switch, we need to find processes 
		// wakeupProcess is sleepingQueue.get(0)
		
			PriorityEnum kpriority = wakeupProcess.getPriority(); 
			System.out.println("Wake up process :" + wakeupProcess + " id : "+ wakeupProcess.getProcessId());
			int kPid = wakeupProcess.getProcessId();
			
			if(kpriority == PriorityEnum.Realtime) {
				// add  Fitem to realtime queue 
				realTimeQueue.add(wakeupProcess); 
				map.put(kPid , wakeupProcess); 
			}else if(kpriority == PriorityEnum.Interactive) {
				interactiveQueue.add(wakeupProcess); 
				map.put(kPid , wakeupProcess); 
			}else if(kpriority == PriorityEnum.Background) {
				backgroundQueue.add(wakeupProcess);
				map.put(kPid , wakeupProcess);  // put it into map 
			}else {
				// add to processQueue 
				processQueue.add(wakeupProcess);
				map.put(kPid , wakeupProcess);  // put it into map 
			}
			
			for(int i =0; i < sleepingQueue.size(); i++) {
				// find index of wakeupProcess 
				if(wakeupProcess == sleepingQueue.get(i)) {
					sleepingQueue.remove(i); // remove wake up process from sleeping queue.
				}
			}
	}
	
	


	/**
	 * SwitchProcess method 
	 * If something is running then stop and pop the top element from the queue and run it. 
	 * If something is not terminated then add to the end of queue and pop the top element from queue and run it. 
	 * @throws InterruptedException 
	 */
	
	public static void Switchprocess()  {
	//	int kPid; // store kprocess's process id 
		
	//	System.out.println("We are in Switchprocess()");
	   // check whether there are currently running kprocess 
		// Case : somthing is running 
		KernalandProcess new_process = null; 
		int StartingAddress = 0; // staring address in physical memory in ULP
		int size = 0; // memory size 
		
		System.out.println("Switch process"); 
		if( currentlyRunningProcess != null ){ 
			System.out.println("not null ");
			currentlyRunningProcess.stop();
			
			// when you stop current process, eliminate that < pid,KernalandProcess > from map 
			map.remove(currentlyRunningProcess.getProcessId());  // because map's key value is PID. 
			map.remove(kprocess.getProcessId()); 
			Kernel.nameMap.remove(kprocess.getProcessId()); // remove from nameMap 
			
			// Process deleted so delete process in phyiscal memory,TLB, pageTalbe, and FreeTalbel
			FreeMemory(currentlyRunningProcess.getProcessId()); 
			
			//pick top at processQueue and run it 
			Map.Entry<Integer, KernalandProcess> iter = map.entrySet().iterator().next();
			KernalandProcess firstProcess = iter.getValue(); 
			new_process =  firstProcess;  //processQueue.get(0);
			currentlyRunningProcess = new_process; 
			currentlyRunningProcess.run(); 
			
		}	else if(currentlyRunningProcess == null) {
			
			//pick top at processQueue and run it 
			Map.Entry<Integer, KernalandProcess> iter = map.entrySet().iterator().next();
			KernalandProcess firstProcess = iter.getValue(); 
			new_process =  firstProcess;  //processQueue.get(0);
			currentlyRunningProcess = new_process; 
			currentlyRunningProcess.run(); 
			
		}
		
          // check whether process ran more than 5 times, true then demotion. ex) realtime -> interactive
			
	
			   if(runTimeCountOver() == true) {
				   // case when ran more than 5 times
				   KernalandProcess.downGrad(currentlyRunningProcess);
			   }else {
				   // runtime is not over 5 times, don't down grade(demote) 
				   // add process into right queue 
				  PriorityEnum kpriority = currentlyRunningProcess.getPriority(); 
				  if(kpriority == PriorityEnum.Realtime) {
						realTimeQueue.add(currentlyRunningProcess);
				  }else if(kpriority == PriorityEnum.Interactive) {
						interactiveQueue.add(currentlyRunningProcess);	
				  }else if(kpriority == PriorityEnum.Background) {
						backgroundQueue.add(currentlyRunningProcess);
				  }else {
					  // Device 
					  deviceQueue.add(currentlyRunningProcess.getProcessId()); 
					  // when I create process, kprocess's processId was seted as scheduler.getDeviceID(), so we are adding device's id. 
				  }
			   }
	 
		// after if statements 
		// case : nothing is running  -> wake up if time is up. 
		 long currentTime = System.currentTimeMillis();
		 for(int i=0 ; i < sleepingQueue.size(); i++) {
			 System.out.println("check sleeping queue << switchprocess() ");
			 KernalandProcess wakeupProcess = sleepingQueue.get(i); 
			 long  curLeftedTime = wakeupProcess.getLeftedTime(); // check sleeping process queue element's left time to sleep 
			 
			 if (curLeftedTime<=currentTime) {
				 processQueue.add(wakeupProcess); 
	        	  wakeUp(wakeupProcess) ; // wake up sleeping queue and add to right queue. 
			 }
		 }
		CheckDiedProcessToFreeMemory();  // check if process died then free memory and clear TLB. 
		// pickNextProcess();

	}



/**
 * Remove process 
 * so this method will remove the process from each queue, if user want to delete process 
 * I will compare process based on processID and priority 
 * processID : process's id that you want to delete. 
 * 
 * Or if process ends, check with isDone(), if it is done then delete process in Scheduler class queue. 
 * if isDone() is true then process is done , close devices. 
 */
public  void DeleteProcess(PriorityEnum priority, KernalandProcess kprocess) {
	// check priority 
	// if prirority == realtime search realtime queue 
	int size; 
	int processID = kprocess.getProcessId(); 
	int deviceId; 
	if(priority == PriorityEnum.Realtime) {
		for(int i = 0; i < realTimeQueue.size(); i++){
			int Rpid = realTimeQueue.get(i).getProcessId(); // get realtime process pid 

			if(processID == Rpid) {
				// you found process that you want to delete -> delete it 
				realTimeQueue.remove(kprocess); 
				map.remove(kprocess.getProcessId());  // remove kprocess on map if you want to delete process. 
				size = realTimeQueue.size()-1;
				// remove process in VFS device arraays
				
				for (int j =0; j <deviceQueue.size(); j++) {
				if(deviceQueue.get(i) == vfs.getDeviceIndex()) {
					deviceId = deviceQueue.get(i); 
					vfs.Close(deviceId); // close vfs device  if process is done. 
					}
				}
				// process is deleted so FreeMemory. 
				FreeMemory(processID); 
				System.out.println("Deleted realtime process with ID : " + processID); 
			}
		}
		
	}// if priority ==  interactive search interactive queue 
	else if(priority == PriorityEnum.Interactive) {
		for(int i =0; i < interactiveQueue.size(); i++) {
			int Rpid = interactiveQueue.get(i).getProcessId(); // get interactive process pid 

			if(processID == Rpid) {
				// you found process that you want to delete -> delete it 
				interactiveQueue.remove(kprocess); 
				map.remove(kprocess.getProcessId());  // remove kprocess on map if you want to delete process. 
				size = interactiveQueue.size() - 1; 
				
				for (int j =0; j <deviceQueue.size(); j++) {
					if(deviceQueue.get(i) == vfs.getDeviceIndex()) {
					      deviceId = deviceQueue.get(i); 
						vfs.Close(deviceId); // close vfs device  if process is done. 
					}
					}
				// process is deleted so FreeMemory. 
				FreeMemory(processID); 
				System.out.println("Deleted interactive process with ID : " + processID); 
			}
		}
		
		
	}	// if priority == background  search background queue 
	else if(priority == PriorityEnum.Background) {
		for(int i =0; i < backgroundQueue.size(); i++){
			int Rpid = backgroundQueue.get(i).getProcessId(); // get interactive process pid 

			if(processID == Rpid) {
				// you found process that you want to delete -> delete it 
				backgroundQueue.remove(kprocess); 	
				map.remove(kprocess.getProcessId());  // remove kprocess on map if you want to delete process.  
				size = backgroundQueue.size() -1; 
				
				for (int j =0; j <deviceQueue.size(); j++) {
					if(deviceQueue.get(i) == vfs.getDeviceIndex()) {
					      deviceId = deviceQueue.get(i); 
						vfs.Close(deviceId);  // close vfs device  if process is done. 
					}
					}
				// process is deleted so FreeMemory. 
				FreeMemory(processID); 
				System.out.println("Deleted background process with ID : " + processID); 
			}
		}
		
	}// else search sleeping queue 
	else {
		for(int i =0 ; i <sleepingQueue.size(); i++) {
			int Rpid = sleepingQueue.get(i).getProcessId(); // get interactive process pid 

			if(processID == Rpid) {
				// you found process that you want to delete -> delete it 
				sleepingQueue.remove(kprocess); 	
				map.remove(kprocess.getProcessId());  // remove kprocess on map if you want to delete process. 
				size = sleepingQueue.size() - 1; // decrement queue size 
				
				for (int j =0; j <deviceQueue.size(); j++) {
					if(deviceQueue.get(i) == vfs.getDeviceIndex()) {
					      deviceId = deviceQueue.get(i); 
						vfs.Close(deviceId); 
					}
					}
				// process is deleted so FreeMemory. 
				FreeMemory(processID); 
				System.out.println("Deleted sleeping process with ID : " + processID); 
			}
		}
	}
}

/**
 * Use Java random class to pick random number 
 * use picked random number to choose which process should be run. 
 * 
 */
public static void pickNextProcess() {
	
	Random random = new Random();
	// TODO Auto-generated method stub
	number = random.nextInt(9); //0 1 2 3 4 5 6 7 8 9
	int number2; // get 0 1 2 3 
	KernalandProcess new_process = null; 
	
	System.out.println("first random number :  " + number);
	// if realtime queue is not empty 
	if(!realTimeQueue.isEmpty()) {
		// if number is 6/10 then check whether realtime process is empty, if not, we can run realtime process. 
		if(number >=4) {
			// this mean 6/10 
			System.out.println("random number1 is 6/10 then we should run realtime process"); 
	
			//check whether this run more than 5 times in a row 
			 if(runTimeCountOver() == true) {  // update realitme process run time count if it is 5 then downgrade 
				 // downgrade relatime 
				 KernalandProcess.downGrad(currentlyRunningProcess); 
			 }
			 
				new_process = realTimeQueue.get(0);
				currentlyRunningProcess = new_process; 
				currentlyRunningProcess.run();   // run() is in KernalandProcess that creates thread. 
				//realTimeQueue.remove(0);
				
			}
		// case to run interactive queue 3/10 times
	}else if(number < 4 ){
			 number2 = random.nextInt(3); // get 0 1 2 3 
			 System.out.println("random number2 :  " + number2);
			 
			 if(number2 !=0) { // 1 2 3 so 3/4 
				System.out.println("random number1 is 3/10 and random number2 is 3/4 then we should run interactive process"); 
				if(!interactiveQueue.isEmpty()) {
					
					if(runTimeCountOver() == true) {  // update realitme process run time count if it is 5 then downgrade 
						 // downgrade relatime 
						 KernalandProcess.downGrad(currentlyRunningProcess); 
					 }
					
					new_process = interactiveQueue.get(0); 
					currentlyRunningProcess = new_process; 
					currentlyRunningProcess.run(); 
					//interactiveQueue.remove(0); // 0 			
				}
	
				}else if(number2 == 0) {
					if(!backgroundQueue.isEmpty()) {
						// run background 1/4 
						if(runTimeCountOver() == true) {  // update realitme process run time count if it is 5 then downgrade 
							 // downgrade relatime 
							 KernalandProcess.downGrad(currentlyRunningProcess); 
						 }
						
						new_process = backgroundQueue.get(0);
						currentlyRunningProcess = new_process; 
						currentlyRunningProcess.run(); 
					//	backgroundQueue.remove(0); // 0 
					
					}
				}
			
		}else {
			//run background 
			if(!backgroundQueue.isEmpty()) {
				if(runTimeCountOver() == true) {  // update realitme process run time count if it is 5 then downgrade 
					 // downgrade relatime 
					 KernalandProcess.downGrad(currentlyRunningProcess); 
				 }
				new_process = backgroundQueue.get(0);
				currentlyRunningProcess = new_process; 
				currentlyRunningProcess.run(); 
			//	backgroundQueue.remove(0); // 0 
			}
		}
	
}

/**
 * 
 * @param priority
 * @param count
 * @return
 */
private static boolean runTimeCountOver() {
	// TODO Auto-generated method stub
	
	if (klp.runTimeCount >= 5) {
		System.out.println("runtime count over 5 times!!! -> downgrade"); 
		return true; 
	}else {
		return false; // did not ran 5 times. 
	}
}

/**
 * Track what is currently open or using device 
 * @param string
 */
public void setCurrentDevice(String string) {
	// TODO Auto-generated method stub
	this.currentDeviceName = string; 
}
/**
 * set current device id 
 * @param id
 */
public void setCurrentDeviceId(int id) {
	this.id = id; 
}
/**
 * get  current device using VFS -> return string name 
 */
public String getCurrentDevice() {
	return this.vfs.getDeviceName(); 
}
/**
 * get  current device ID
 */
public int getCurrentDeviceID() {
	return this.vfs.getDeviceIndex(); // return id index in VFS's TypeOfDevice() array. 
}
/**
 * Get currently running process's pid 
 */
public int GetPid() { 
	return currentlyRunningProcess.getProcessId(); 
}

/**
 * Check if process isDone(), if it's true, FreeMemory(), TLBCleared() and remove from PidAndMemory HashMap
 * Iterates over processQueue where all process will be stored and if process's isDone() true then free memory and clear
 */
public static void CheckDiedProcessToFreeMemory() {
	KernalandProcess process = null ; // init
	for(int i =0; i < processQueue.size(); i++) {
		process  = processQueue.get(i); 
		if(process.isDone() == true) {  // case when process died. 
			int processId = process.getProcessId(); 
			MemoryInfo memoryInfo = PidAndMemory.get(processId);  // memoryInfo has starting address and memorySize 
			int startingAddress = memoryInfo.getStartingAddress(); 
			int size = memoryInfo.getSize(); 
			
			// FreeMemory since process died. 
			Kernel.FreeMemory(startingAddress, size); 
			Kernel.TLBCleared(); // remove from TLB 
			// remove from processQueue 
			processQueue.remove(process); 
			System.out.println("ProcessId : "+ processId + " died so we FreedMemory and TLB"); 
		}
	}
}

/**
 * Scheduler - FreeMemory Method. 
 * If process is delted then free memory, clear TLB, and remove process from PidAndMemory HashMap 
 */
public static void FreeMemory(int processId ) {
	
	// Process deleted so delete process in phyiscal memory,TLB, pageTalbe, and FreeTalbel
	MemoryInfo memoryInfo = PidAndMemory.get(processId);
	int StartingAddress = memoryInfo.getStartingAddress();
	int memorysize = memoryInfo.getSize(); 
	
	Kernel.FreeMemory(StartingAddress, memorysize); 
	Kernel.TLBCleared(); // clear TLB arrays.
	PidAndMemory.remove(currentlyRunningProcess.getProcessId());  // remove in Hashmap
	System.out.println("In Scheduler, processID : " + processId + " is Freed from : " + StartingAddress + " with size: " + memorysize); 
	
}
	}
	
		
	

	

	

