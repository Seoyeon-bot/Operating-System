/*
 * Create Device interface. 
 */
public interface Device {

	    int Open(String s);  // ex) int id = open("report.txt") and when we want to close this device use id to close it. 
	    void Close(int id);  // id is device id 
	    byte[] Read(int id,int size);  // read given id device with given amount of size 
	    void Seek(int id,int to);   // find given id device, to : pointer to device 
	    int Write(int id, byte[] data);  // write to given id device with given amount of data in byte size. 
	
}
