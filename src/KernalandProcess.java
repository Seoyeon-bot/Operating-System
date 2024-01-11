import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class KernalandProcess extends TimerTask implements Runnable {
   
	//declare variables 
	static int nextpid; 
	int processID;  // holds process id 
	private boolean isStarted = false; // boolean that indicates if the Thread has been started yet 
	// private volatile boolean exit = false; 
	

	public static KernalandProcess kprocess;
	UserlandProcess process;
	Scheduler schduler;  // we initialized thread in scheduler. 
	private Thread thread; 

	static VFS vfs = new VFS();
	
	// init 
	long LeftedTime = 0; //for sleep lefted time to wake up 
	long sleepingTime; 
	int milliseconds; 
	PriorityEnum priority;
	int runTimeCount = 0 ; // increment count when you run the process thread, over 5 times will demoted in scheduler swicthprocess()
	
	//Add an array of 10 integers to your kerneland process.
	int val = -1;
	static int[] intArray = new int [10]; // 0 -- 9 
	
	// add name member for getPidByName(string) 
	String processName ; // store userland process's name 
    Queue<KernelMessage> messageQueue  = null;  
	HashMap <Integer, Queue<KernelMessage>> MessageQueueForProcesses = new HashMap<Integer, Queue<KernelMessage>> ();  // store processId , its process's message queue as key and value. 

	// Pagging - create page table in KLP size 100 index = virtaulpage, content = physical page 
	//public static ArrayList<Integer> pageTable = new ArrayList<Integer>(100); 
	public static int[] pageTable = new int[100]; 
	
	
	public static KernalandProcess getCurrentlyRunning() {
			//accessor to the scheduler 
		   return Scheduler.currentlyRunningProcess; 
	}

	/**
	 * constructor 
	 */
	public KernalandProcess() {
		// TODO Auto-generated constructor stub
		Arrays.fill(intArray, val);   // fill all array index with -1 
		Arrays.fill(pageTable, val); // fill array of integer with -1 
		thread = new Thread();  // for object type devices 
	}

	
	/**
	 * Get userlandProcess 
	 * @return userlandprocess 
	 */
	public UserlandProcess getUserlandProcess() {
		return this.process; // return userland process 
	}
	
	/**
	 * create Thread with userlandprocess 
	 * @param up
	 */
	
	public KernalandProcess( UserlandProcess up) {
		thread = new Thread(up); 
	//processID++;  
	
	}
	
	/**
	 * Crate thread with userland process and priority type. 
	 * @param up
	 * @param priority
	 */
	public KernalandProcess(UserlandProcess up ,PriorityEnum priority) {
		this.priority = priority;  // set priority 
		this.sleepingTime = 0;   // sleeping time as 0 
		thread = new Thread(up); // create thread with userland process 
	}
	
	
	public KernalandProcess(int deviceID ) {
		// TODO Auto-generated constructor stub
		for(int i=0; i < vfs.dId.length; i++) {
			if(vfs.dId[i] == deviceID) {
				// we found the device's ID that we need to run from vfs 
				Device rundevice = vfs.TypeOfDevice[i]; 
				thread = new Thread((Runnable) rundevice); 
			}
		}
		// thread = new Thread((Runnable) object);
	}

	/**
	 * This method does not return any value. 
	 * This method will stop current thread. 
	 * @param currentThread 
	 */
	public void stop() {
		//this.thread.stop();
		
	    if(isStarted == true) {
	          thread.suspend();
	          this.thread.stop();
	         // Thread.currentThread().interrupt();
	          thread.interrupt();
	        //  kprocess.
	          runTimeCount++;  // if count is over 5, demote process. 
	    }else {
	    	// do nothing.
	    }
	}
	
	/**
	 * Method to downgrad the process if it runs more than 5 times in a row 
	 * count : number of time that process run in a  row. 
	 */
	public static void downGrad(KernalandProcess kprocess) {
	 //ex) downGrad(kprocess, Realtime) then see if realtime run's 5times in a row and downgrade it to interactive process.
		 
		 //check how many times they run in a row???
		
		PriorityEnum realT = PriorityEnum.Realtime; 
		PriorityEnum inter = PriorityEnum.Interactive; 
		PriorityEnum back = PriorityEnum.Background; 
		
		PriorityEnum kprocessP = kprocess.getPriority(); // kprocess's priority ex) realtime, interactive, background 
		if(kprocessP == realT) {
			kprocess.setPriority(inter);  
			Scheduler.interactiveQueue.add(kprocess);

		}else if(kprocessP == inter) {
			kprocess.setPriority(back);  // reset priority 
			Scheduler.backgroundQueue.add(kprocess);
			
		}else {
			
		}
	}	
	/*
	 * Check whether thread is done.
	 * If thread started and not isAlive() then true 
	 * else false (not done case) 
	 */
	// If process is done -> close all open devices and fee the memories
	public boolean isDone() {
		// true if the thread started and not isAlive()
		//Thread.State  tState = thread.getState(); 
		if(isStarted && !thread.isAlive()) {
			Kernel.TLBCleared(); 
			return true; 
		}
		return false; 
	}
	/*
	 * method to resume() or restart() method 
	 * 
	 */
	public void resume(Thread thread ) {
		
		
		thread.start(); // resume 
		isStarted = true;  // update isStarted as true. 
		runTimeCount++;
	}
	
	/**
	 * run thread 
	 * Sheduler handle process -> KernelandProcess handle thread 
	 */
	public void run() {
		
		if(isStarted == false) {  // thread was sleeping then resume()
			resume(thread);
		//	runTimeCount++;
			System.out.println("KernelandProcess run() - resume()");
			//thread.start();
		}
		else { //thread is started
			isStarted = true; 
			thread.start(); 
		//	runTimeCount++;
			System.out.println("KernelandProcess run()");
			
		}
	}
	
	/**
	 * Getter and Setter for process ID 
	 * @return
	 */
	public int getProcessId() {
			return this.processID; 
	}
	/**
	 * Set process id 
	 * @param processID
	 */
	public void setProcessID(int processID) {
			this.processID = processID; 
	}
	
	/**
	 * Getter and Setter for lefted time for sleep
	 * @param milliseconds
	 */
	public void setLeftedTime(long milliseconds) {
		// TODO Auto-generated method stub
		this.LeftedTime =  milliseconds; 
		
	}
	
	/**
	 * return lefted time. to wake up.
	 * @return
	 */
	
	public long getLeftedTime() {
		return this.LeftedTime; 
	}
	
	
	/**
	 * Getter and Setter for priority 
	 */
	public PriorityEnum  getPriority() {
		return this.priority;  // return realtime, interactive, background 
	}
	
	/**
	 * setter for priority 
	 * @param priority
	 */
	public void setPriority(PriorityEnum priority) {
		this.priority = priority; 
	}
	
	/**
	 * Sleep method 
	 * @param milliseconds
	 */
	public void sleep(int milliseconds) {
		// get userlandprocess and put sleep mode for milliseconds.
		 LeftedTime = milliseconds;
	}
	
	
	/**
	 * Userland open() send to kerneland open() send to VFS open() 
	 * @param string
	 * @return 
	 * @return
	 */
	public static int Open(String string) {
		// TODO Auto-generated method stub
		 // we will find empty spot in intArray and store the intvalue (which is 
		// index number of where you store specific device in VFS array. 
		// store that in empty spot of intArray
		// return index of intArray to kernel.Open() this will go to OS , Userland 
		//int idFromVFS = -1;
		int index;
		KernalandProcess process = getCurrentlyRunning(); // get scheduler's currently running process 
		 
		for (index = 0 ; index < intArray.length; index++) {
			if(intArray[index] == -1) { // null is -1 
				// empty spot founded. 
				int idFromVFS =vfs.Open(string);  // get index where you store in vfs array 
				System.out.println("vfs array index : " + idFromVFS);
				 intArray[index] = idFromVFS;
				 System.out.println("intArray[index] " + intArray[index]);
				 Scheduler.deviceQueue.add(idFromVFS);  // store VFS array index into Schedulr device queue.  
				 process.setProcessID(index);  // set currently running process id as index (stored vfs index where device stored) 
				 Scheduler.deviceQueue.add(process.getProcessId()); 
				 return index;
			}else {
				continue; // search for empty index 
			}
		}
		// there are not empty spot in intArray 
		return -1; // return where we stored in intArray, so return intArray index.  
	}
	
	/**
	 * OS call Close() -> kernel call Close() -> KernelandProcess call Close()
	 *  -> VFS close device with given id .
	 * @param id
	 */
	public static void Close(int id) { 
		// find vfs array index 
		
		vfs.Close(intArray[id]);
	//\	System.out.println("vfs array index: "+ intArray[id]);
		// call VFS close 
		 intArray[id] = -1; // free  kernalandprocess array index 
		 System.out.println("kerneland int array index : "+ id + " is closed");
		
		
	}
/**
 * Reading from OS and pass necessary informations ex) id and size to the VFS -> specific device 
 */
public  byte[] Read(int id, int size) {
		// TODO Auto-generated method stub
	byte ReadSize[];
	int index;
	for (index =0 ; index < intArray.length; index++) {
		if(index == id) { // id is kland int array index 
			// empty spot founded. 
			ReadSize  =vfs.Read(intArray[index], size);  // get index where you store in vfs array 
		
			return ReadSize; // store how much you read from VFS.
		}else {
			continue; // search for empty index 
		}
	}
	return null; // fail case
}

/**
 * OS call Seek() -> Kernel call Seek() -> KernelandProcess call Seek()
 * -> VFS Seek() for specific id. 
 * 
 * @param id
 * @param to
 */
	public  void Seek(int id, int to) {
		// TODO Auto-generated method stub
		int idForVFS = intArray[id]; 
		vfs.Seek(idForVFS, to); 
	}
/**
 * Write() calls VFS write() 
 * @param id
 * @param value
 * @return 
 */
public int Write(int id, byte[] value) {
	// TODO Auto-generated method stub
	int idForVFS = intArray[id]; 
	return vfs.Write(idForVFS, value);
}

}