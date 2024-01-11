import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserlandProcess implements Runnable{
	
	// add Read and Write for CPU memory access 
	
	// creates static  TLB[2][2]
	// TLB caches mappings between virtual page numbers and physical page frame numbers
	public static int[] virtualAddressList = new int[2]; // TLB virtual address 
	public static int[] physicalAddressList = new int[2]; // TLB's physical address array part 
	
	// byte array for offset 
	byte[] offset = new byte[1024]; 
	
	// physical memory static 1M size 1,048,576 (1024*1024) bytes in UserlandProcess 
	public static byte[] physicalMemory = new byte[1024 * 1024]; 
	// address 
    byte  virtualAddress; 
	static byte physicalAddress; 
	
	//page number 
	static Integer virtualPageNumber; 
	static Integer physicalPageNumber; 
	
	// offset 
	static int pageOffset; 
	
	//instance of classes 
	//Kernel kernel = new Kernel(); 
	KernalandProcess klp = new KernalandProcess(); 
	int val = -1; 
	
	
	/**
	 * constructor 
	 */
	public UserlandProcess() {
		//set all array's entry as -1 (initalizing)
		Arrays.fill(virtualAddressList, val);
		Arrays.fill(physicalAddressList, val);
		Kernel.isUsed = false; // not yet used.
	
	}
	
	
	/**
	 * Gets virtual page, gets physical page, checks TLB and returns data correctly
	 * @param address (virtual address)
	 * @return
	 */
public static byte Read(int virtual_address) {
		//virtual_address /1024 = virtual page 
		virtualPageNumber = virtual_address/1024; 

		//virtual_address %1024 = page_offset 
		pageOffset = virtual_address % 1024;  
		
		//physical_page will be stored in same index as virtual_page  because of mapping
		
		// check virtualArrayList
		for(int i=0; i < virtualAddressList.length; i++) {
			if(virtualAddressList[i] == virtualPageNumber) {
				// we can get physical address 
				physicalPageNumber = getPhysicalPageNumber(virtualPageNumber); 
				// chck physical page number, if it is - 1 then its empty 
				if(physicalPageNumber == -1) {
					// check KLP's page table 
					// check KernalandProcess's pageTable, its index = virtual page number  , its content = physical page number 
					if(KernalandProcess.pageTable[virtualPageNumber]!= -1){
								physicalPageNumber = KernalandProcess.pageTable[virtualPageNumber];
					}
					else {
							// case pageTable doesn't contain physicalPageNumber
								System.out.println("Physical page number does not exist at pageTable index : " + virtualPageNumber ); 
								// case when pageTable doesn't have physical number. -> TLB miss -> call OS.GetMapping(virtual page number) void 
								OS.GetMapping(virtualPageNumber); // take care TLB, KLP page table, Kernel boolean array isUsed 
								physicalPageNumber = getPhysicalPageNumber(virtualPageNumber);  // get physical page number 		
					    }		
				}
			}
	}
		//calculate physical_address = phsical_page * page size(1024) + page_offset 
		physicalAddress = (byte) ((physicalPageNumber *1024) + pageOffset); 
		System.out.println("Read on physicalMemory physical address: " + physicalAddress); 
		return physicalMemory[physicalAddress]; 
	} 
		
		

	/**
	 * returns physical page number 
	 */
	public static int getPhysicalPageNumber (int virtualPageNumber) {
		// iterate over virtualArrayList and find idnex where virtualPageNumber is stored 
	   physicalPageNumber = 0; 
		for(int i=0; i < virtualAddressList.length; i++) {
			if(virtualAddressList[i] == virtualPageNumber) {  // index i will store physical page in physicalAddressList
				physicalPageNumber = physicalAddressList[i]; 
				System.out.println("virtual Page Number : " + virtualPageNumber + " physical page Number: " + physicalPageNumber); 
				return physicalPageNumber; 
				 
			}
		}
		return physicalPageNumber;
	}
	
	/**
	 * Gets virtual page, gets physical page, checks TLB and writes data into phyiscal memory correctly
	 * @param address (virtual address) 
	 * @param value
	 */
	public static void Write(int virtual_address, byte value) {
	    virtualPageNumber = virtual_address / 1024;
	    pageOffset = virtual_address % 1024;

	    // Check TLB to get physical page number 
	    int tlbIndex = -1;  // default
	    for (int i = 0; i < virtualAddressList.length; i++) {
	        if (virtualAddressList[i] == virtualPageNumber) {
	            tlbIndex = i;
	            break;
	        }
	    }
    // case : we found index position in virtualAddressList. -> find physical page number
	    if (tlbIndex != -1) {
	        //  Get the physical page number from TLB
	        physicalPageNumber = physicalAddressList[tlbIndex];
	    } else {
	        // Get the physical page number from page table or OS.
	        if (KernalandProcess.pageTable[virtualPageNumber] != -1) {
	            physicalPageNumber = KernalandProcess.pageTable[virtualPageNumber];
	        } else {
	            // call OS.GetMapping
	        	System.out.println("\n" + "Uable to find Physical page Number -> Call OS.GetMapping(" + virtualPageNumber + ")"); 
	            OS.GetMapping(virtualPageNumber); //  update TLB and page table.
	            physicalPageNumber = getPhysicalPageNumber(virtualPageNumber);  // error? 
	        }
	    }

	    // Calculate the physical address 
	    physicalAddress = (byte) ((physicalPageNumber * 1024) + pageOffset);
	    physicalMemory[physicalAddress] = value; // Write value to the physical memory at the calculated physical address.
	    System.out.println("[Write] In physicalMemory physical address: " + physicalAddress+ " write value : " + value );
	}

}
