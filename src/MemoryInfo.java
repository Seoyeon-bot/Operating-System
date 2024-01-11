/**
 * This MemoryInfo class will be used to store process's startind address in memory and its size
 * So we can use those information when we have to FreeMemory() 
 * @author choeseoyeon
 *
 */
public class MemoryInfo {

	private int startingAddress; 
	private int size; 
	
	/**
	 * Create constructor with startingAddress and size 
	 */
	public MemoryInfo(int sAddress , int requestedSize) {
		this.startingAddress = sAddress; 
		this.size = requestedSize; 
		
	}
	/**
	 * Get starting address in UserlandProcess's PhysicalMemory. 
	 * @return
	 */
	public int getStartingAddress() {
		return startingAddress; 
	}
	/**
	 * Get size
	 */
	public int getSize() {
		 return size; 
	}
	
}
