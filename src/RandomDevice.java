import java.io.RandomAccessFile;
import java.util.Random;
import java.util.*;
import java.security.*;

public class RandomDevice  implements Device{

	
	// keep an array (10 items) of java.util.Random. O
	Random[] rDeviceArray= new Random[10];  // array : store randome devices 
    int Id; // device unique id 
	private VFS vfs = new VFS();
	// consructor 
	public RandomDevice() {
		
	}
	static RandomDevice randomDevice = new RandomDevice(); 
	/**
	 * Getter for randomDevice 
	 * @return
	 */
	public static RandomDevice getRandomDevice() {
		return randomDevice;
	}
	/**
	 * Open() will create a new Random device and
	 *  put it in an empty spot in the array.
	 *  
	 *   If the supplied string for Open is not null or empty,
	 *   assume that it is the seed for the Random class 
	 *   (convert the string to an integer).
	 */
	public int Open(String string) {
		System.out.println("\nIn RandomDevice, we are opening random device with given info : " + string); 
		// TODO Auto-generated method stub
		int sd = 0; // seed number  //fix this. 
		
		if(string.isBlank() == true) {
			// string is null or empty -> so let sd = 0 
			sd = 0; 	
		}else if(!string.isEmpty()){
			//input string is not null or empty 
			// assume this is seed -> conver string to intger 
			sd = Integer.parseInt(string);
			System.out.println("Check sd : " + sd);
			
			
		}
		//find empty spot in rDeviceArray and store newly created randome device 
		for(int id = 0 ; id < rDeviceArray.length; id++) {
			// if index = id in array is not null , something is there then open it. 
			 if (checkEmpty(rDeviceArray, id) ==  false) {   // find empty spot to store this device
				Random random = new Random(sd); 
				rDeviceArray[id] = random;
						//new Random(sd); 
				System.out.println("rDeviceArray index : " + id ) ;
				return id; 
			}else {
				//do nothing
			}
		}
        return -1; // give this id to the kernleand process and store in there array
	}

	/**
	 * This function will check whether specific index in array is empty(null) or not. 
	 */
	public  boolean checkEmpty(Random[] rDeviceArray, int id ) {
		// in rDeviceArray index = id, it that position is null return true, 
		// if that position is not null then return false. 
		if(rDeviceArray[id] != null) {
			return true; 
		}else {
			return false; 
		}
	}
	
	
/**
 * Close will null the device entry.
 */
	public void Close(int id) {
		// TODO Auto-generated method stub
		
				// close vfs dId array index at id material in here. 
				System.out.println("Random device index id : " + id + " was closed."); 
				rDeviceArray[id] = null; 
	}
/**
 * create byte array with given size. 
 */
private byte[] createByteSize(int size) {
	// create byte size array for the Read method 
	byte[] ByteArray = new byte[size]; // create array for read with given size; 
	return ByteArray; 
}
/**
 * Read will create/fill an array with random values.
 */
	public byte[] Read(int id, int size) {
		// TODO Auto-generated method stub
		System.out.println("rDeviceArray id : "+ id + " rDeviceArray[id] : "+ rDeviceArray[id]);
		byte  ByteArrayForRead[] = createByteSize(size);  // this function will create bytesize array for read
		rDeviceArray[id].nextBytes(ByteArrayForRead);  // set nextbyte size of this specific id position. 
		return ByteArrayForRead;  // return byte[] array. 
		
	}

/**
 * Seek will read random bytes but not return them.
 */
	public void Seek(int id, int to) {
		// TODO Auto-generated method stub
		byte[] bArray = createByteSize(to); // bytes of array to read. 
	     rDeviceArray[id].nextBytes(bArray); 
	     System.out.println("Seek Successed!"); 
	}

/**
 * Write will return 0 length and do nothing (since it doesn’t make sense). 
 */
	public int Write(int id, byte[] data) {
		// TODO Auto-generated method stub
		// to test out whether write functions are working, I set return value as 100. 
		// If program works successfully, Testuserland class will printout 0 bytes. 
		return 0;
		
	}

	
}
