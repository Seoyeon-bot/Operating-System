
/**
 * UserlandProcess class to test out paging using TLB. 
Test processes that show all functionality working - multiple processes reading
and writing to memory and proving that they are not overwriting each other. (10)
 * @author choeseoyeon
 *
 */
public class TestPaging2 extends UserlandProcess{
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	  System.out.println("#######################-------Test TestPaging2 Class-----------####################"); 
		int virtualAddress = OS.getOS().AllocateMemory(7168); 
		int memorySize = 16; // amount of bytes to write on physical memory 
		
		if(virtualAddress == -1) {
			System.out.println("Fail to allocate in Physical Memory"); 
		}
		else { // case when allocateMemory() was successed
			
			
			// write data to memory 
			System.out.println("\n-------------------------Test TestPaging2 Write()-----------------------------\n"); 
			byte[] WriteData = "Hello, Paging!".getBytes(); 
			for(int i=0; i < memorySize; i++) {
				UserlandProcess.Write(virtualAddress + i, WriteData[i]);  // write values in physical memory
						}
			
			
			
			System.out.println("\n-----------------Test TestPaging2 Read()-------------------\n"); 
			// read data from physical memory 
			byte physicalAddressToRead = UserlandProcess.Read(virtualAddress);
			System.out.println("We can read data from : " + physicalAddressToRead); 
			
			
			
			
			System.out.println("\n--------------------------Test2 Read() ----------------------------------\n");
			byte[] readData = new byte[WriteData.length]; 
			// read data using userlandprocess's read() 
			for(int i=0; i < readData.length; i++) {
				readData[i] = UserlandProcess.Read(virtualAddress + i); 
				System.out.println("Reading : "+ readData[i]); 
					}
			
			
		}
		OS.Sleep(250);
		
		
	}
	

}
