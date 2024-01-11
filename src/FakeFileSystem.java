import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.RandomAccess;

/**
 * Fake File System class.  - another device. 
 * @author choeseoyeon
 *
 */
public class FakeFileSystem implements Device {

	// crate size 10 array - RandomAccessFile; you will need an array (again, 10 is enough) of these.
	RandomAccessFile[] fileArray = new RandomAccessFile[10];
    int Id; // device id. 
    static FakeFileSystem fakefilesystem = new FakeFileSystem(); 
    private VFS vfs = new VFS();
    /**
     * constructor 
     */
    public FakeFileSystem() {
    	
    }
   
	/**
	 * Read a filename from parameter;
	 *  if the filename is empty or null -> throw an exception. 
	 *  
	 *  Open will create and record a RandomAccessFile in the array. 
	 */
	public int Open(String string) {
		// TODO Auto-generated method stub
		System.out.println("\nIn FakeFileSystem, we are opening fake file system with given info : " + string); 
		int Id = 0; 
		// if given string is empty -> throw exception. 
		try {
			if(string.isBlank() == true || string.isEmpty() == true ||
					string == null){
						throw new Exception("Filename is empty or null!!"); 
				}
		}catch(Exception e) {
			System.out.println(e.toString()); 
		}
		
		// case when given string is not null or empty case 
		for( Id = 0; Id < fileArray.length ; Id++) {
				// check whether index = Id is empty 
				// if fileArray index = is not empty then we can open it. because it is not empty. 
			//	if(fileArray[Id] == null) {
			 if (checkEmpty(fileArray, Id) ==  false) {   // find empty spot to store this devic
						try {
							RandomAccessFile randfile = new RandomAccessFile(string, "rw"); 
							fileArray[Id] = randfile;
									//new RandomAccessFile(string, "rw");
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}else {
					continue; 
				}
		}
        return Id; // device Id 
	
	}

	/**
	 * Make sure that you close the RandomAccessFile and 
	 * clear out your internal array 
	 * when close() is called for this device.
	 */
	public void Close(int id) {
		// TODO Auto-generated method stub
		
		//close RandomAccessFile 
	
				fileArray[id] = null; 
				System.out.println("Fake File System device id : " + id + " was closed."); 
	}

	/**
	 * create byte array with given size 
	 */
	private byte[] createByteSize(int size) {
		// create byte size array for the Read method 
		byte[] ByteArray = new byte[size]; // create array for read with given size; 
		return ByteArray; 
	}
	
	
/**	
 * read int id device with amount of size
 * Use createByteSize method to create byte type array for reading purpose
 */
	public byte[] Read(int id, int size) {
		// TODO Auto-generated method stub
		//byte fileByteArrayforRead[] = createByteSize(size); // this method will create byte type array for read 
		byte fileByteArrayforRead[] = new byte[size];
		System.out.println("fileByteArrayforRead id : "+ id + " FileByteArrayforRead[id] : "+ fileByteArrayforRead[id]);
		
		try {
			fileArray[Id].read(fileByteArrayforRead);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} 
	
		return fileByteArrayforRead;
	}

/**	
 * Seek, search for the device id and to ( pointer) 
 */
	public void Seek(int id, int to) {
		// TODO Auto-generated method stub
		try {
			fileArray[Id].seek(to);  // to is indicating pointer of this index =Id file. 
			System.out.println("Fake File System seek() was successful!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		 
	}
/**
 * This function will check whether specific index in array is empty(null) or not. 
 */
	public  boolean checkEmpty(RandomAccessFile[] fileArray, int id ) {
		// in rDeviceArray index = id, it that position is null return true, 
		// if that position is not null then return false. 
		if(fileArray[id] != null) {
			return true; 
		}else {
			return false; 
		}
	}

/**	
 * Write down byte size  data on device id 
 * @return how much size we write down. 
 */
	public int Write(int id, byte[] data) {
		// TODO Auto-generated method stub
		// what if file index is not null but want to write down? -> just overwrite it. 
	//	if(checkEmpty(fileArray, id) == false) {

				try {
					fileArray[Id].write(data);
					fileArray[Id].seek(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				} // write down data in fileArray index = Id
			
	   return data.length;
	}
/**
 * Getter for fake file system
 * this will be called in vfs open() to open this device. 
 * @return
 */
public static FakeFileSystem  getFakeFileSystem() {
	// TODO Auto-generated method stub
	return fakefilesystem;
}
}
