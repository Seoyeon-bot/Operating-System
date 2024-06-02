import java.util.Random;

/*
 * The part of the kernel that handles devices is called the VFS or Virtual File System. 
 * The VFS is responsible for associating each id with a device and id pair. 
 * VFS also implements the Device interface
 */
public class VFS implements Device {

	// Initialize variables 
	int Id; // store device's Id
	
	// creates parelle array to store device and id as pair 

    Device[] TypeOfDevice = new Device[10];  // Device type. ex) falefilesyste, random device ,,, 
    int[] dId = new int[10];  // device's Id
   
    Device currentDevice;  // I will use this to track what is currently using device. 
    int currentDeviceID; // track current Device's ID
    
    // call other class
    public static RandomDevice randomDevice = new RandomDevice();
    public static FakeFileSystem fakeFileSystem = new FakeFileSystem(); 
    
    /**
     * Constructor 
     */
    public VFS() {
    	
    }
    /**
     * get device index in TypeOfDevice array 
     */
    public int getDeviceIndex() {
    	for(int Id=0; Id<TypeOfDevice.length; Id++) {
    		if(TypeOfDevice[Id] != null) {
    			return Id;  // not empty so retun the id index 
    		}
    		else {
    			// explore more 
    			continue;
    		}
    	}
    	return Id;  // id 
    }
	/**
	 *  VFS looks at the incoming string -> this will determine type of device
	 * rest of input should go to that specific device's open 
	 * ex) open(“random 100”) – opens the random device and uses 100 for the seed
	 * 
	 * open(“file data.dat”) – opens a file called data.dat
	 */
	public int Open(String s) {
		// TODO Auto-generated method stub
		// check whether given string s is registered in TypeOfDevice before open it. 
		int specificID = 0 ; 
		System.out.println("Passed String : " + s);   // random 100 
		 //create size of 2
		String[] str_array = s.split(",",2); 
		
		//set 
		String first_str = str_array[0]; // "random"
		System.out.println("fist input string(Device name) : " + first_str); 
		String rest = str_array[1];  // "100" seed number
		System.out.println("second input (info) : " + rest);
		
		// find id for that device in typeofDevice array. 
		for(int i =0; i < TypeOfDevice.length; i++) {
			if(TypeOfDevice[i] == null) {	
				
					specificID = i;				
			}else {
					continue; // we haven't found id.
		  }
		}
					
		// compare with founded id and string device name 
		if ("random".equalsIgnoreCase(first_str)) {
		
			// open RandomDevice 
			TypeOfDevice[specificID] = randomDevice; 
			TypeOfDevice[specificID] = RandomDevice.getRandomDevice(); // get random device and store in TypeOfDevice array 
			//dId[specificID] = randomDevice.Open(rest);  // store random device array index into dId array 
			dId[specificID] =  TypeOfDevice[specificID].Open(rest); // ex) 100 store in dId array. 
			System.out.println("dId[specificID] : " + dId[specificID]); 
		}
		else if("file".equalsIgnoreCase(first_str)) {
			
			// open fakefile system
			TypeOfDevice[specificID] = fakeFileSystem; 
			TypeOfDevice[specificID] = FakeFileSystem.getFakeFileSystem(); // get random device 
		//	dId[specificID] = fakeFileSystem.Open(rest); // store fakefile system array index into dId array 
			dId[specificID] =  TypeOfDevice[specificID].Open(rest); // ex) 100 
		
		}
		
		return specificID; 
	}

	/**
	 * Close will remove the Device and Id entries. 
	 * make array entry to b e null. 
	 * 
	 * Scheduler DeleteProcess() method call this. 
	 */
	public void Close(int id) {
		// TODO Auto-generated method stub
		// go to TypeOfDevice array  where I stored all the device's name and 
	    // match the given id -> close it. 
		// set that device's id as 0 in dId array 
		int closeId  = dId[id]; // we have to fee index that is stored in dId array index position at id. 
		System.out.println("vfs id : "+ id + " was closed "); 
		System.out.println("closeID : "+ closeId); 
		
		Device device = TypeOfDevice[id]; 
		//TypeOfDevice[id].Close(closeId); 

		
		dId[id] = 0;   // make array that holds this id as empty spot.
		TypeOfDevice[id] = null;
		
	}

	/**
	 * 
	 * Read/Write/Seek will just pass 
	 * through to the appropriate device.
	 */
	public byte[] Read(int id, int size) {
		// TODO Auto-generated method stub
		// find given id device and open for given size 
		Device readDevice = TypeOfDevice[id];  // device name 
		int rid = dId[id]; // readDevice's id 
		System.out.println("In VFS read device : " + readDevice.toString() + " With id: " + rid + " Size: " + size ); 
		return readDevice.Read(rid, size); // open device and read. 
	}

	/**
	 * Search for this appropriate device 
	 * to : indicates pointer to the specific device 
	 * id : indicate specific device's 
	 */
	public void Seek(int id, int to) {
		// TODO Auto-generated method stub
		Device seekDevice = TypeOfDevice[id]; 
		System.out.println("Index for VFS seek id : " + id  + " SeekDevice : " + seekDevice);
		if(TypeOfDevice[id] != null) {
			seekDevice.Seek(dId[id], to);
		}else {
			System.out.println("We went to VFS to seek for specific device, but it is not registered\n"); 
		}
	    
	}
/**
 * Write data in given id device. 
 */

	public int Write(int id, byte[] data) {
		// TODO Auto-generated method stub
		Device writeDevice = TypeOfDevice[id]; //find device for writing
		int wid = dId[id]; // find writeDevice's pair id 
		System.out.println("Index for VFS write id : " + id + " TypeOfDevice[id] : " + TypeOfDevice[id]); 
		
		if(TypeOfDevice[id] != null) {
			return writeDevice.Write(wid, data); // write data. 
		}else {
			// 
			System.out.println("We went to VFS to write on specific device. ");
		}
		return -1;
	}


/**
 * Getter for specific device's name 
 */
public String getDeviceName() {
	// TODO Auto-generated method stub
	for(int i=0; i < TypeOfDevice.length; i++) {
		// iterate over names of devices array 
		String deviceName = TypeOfDevice[i].toString();
		if(deviceName.equalsIgnoreCase(this.currentDevice.toString())) {
			return currentDevice.toString();
		}
	}
	return currentDevice.toString();
}




}
