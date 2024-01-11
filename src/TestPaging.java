/**
 * UserlandProcess class to test out paging using TLB. 
Test processes that show all functionality working - multiple processes reading
and writing to memory and proving that they are not overwriting each other. (10)
 * @author choeseoyeon
 *
 */
public class TestPaging extends UserlandProcess{
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	    System.out.println("----------------------------- Test TestPaging - UserlandProcess -----------------------------------------" + "\n"); 
		int size = 1024; 
	    int virtualAddress = OS.getOS().AllocateMemory(size); 
		int DataSize = 2; // amount of bytes to write on physical memory 
		
		if(virtualAddress == -1) {
			System.out.println("Fail to allocate in Physical Memory"); 
		}
		else { // case when allocateMemory() was successed
			// write data to memory 
			System.out.println("\n-------------------------Test TestPaging Write()-----------------------------\n"); 
			for(int i=0; i < DataSize; i++) {
				byte value = (byte)(i +1);  // write value from 0 tp 16
				UserlandProcess.Write(virtualAddress + i, value);  // write values in physical memory
			}
			System.out.println("\n-----------------Test TestPaging Read()-------------------\n"); 
			
			
			// read data from physical memory 
			byte physicalAddressToRead = UserlandProcess.Read(virtualAddress);
			System.out.println("We can read data from : " + physicalAddressToRead); 
			
			System.out.println("\n--------------------------Test Read() ----------------------------------\n");
			byte[] readData = new byte[DataSize]; 
			// read data using userlandprocess's read() 
			for(int i=0; i < readData.length; i++) {
				readData[i] = UserlandProcess.Read(virtualAddress + i); 
				System.out.println("Reading : "+ readData[i]); 
			}
			System.out.println("\n--------------------------Test FreeMemory() ----------------------------------\n");
			OS.getOS().FreeMemory(virtualAddress , size);
			System.out.println("\n Done\n"); 
			
			OS.Sleep(250);
			
		}
		
		
		
	}
	

}
