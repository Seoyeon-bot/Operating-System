/**
 * This is userland process class to test out read write seek close open methods. 
 * @author choeseoyeon
 *
 */
public class TestUserland extends UserlandProcess{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		
		// test open() 
		int randomID = OS.getOS().open("random,100"); 
		System.out.println("Where we stored file data.dat in Kerneland int array : " + randomID); 
		System.out.println("\n"); 
		
		// test read() 
		byte randomRead[] = OS.getOS().Read(randomID, 100); 
		System.out.println("Random device read : " + randomRead.toString() );
		System.out.println("Generate random bytes if read() was successed. ");
		for(byte b : randomRead) {
			System.out.println(b);
		}
		System.out.println("\n"); 
		
		// test write() 
		int numWrite1 = OS.getOS().Write(randomID, randomRead); 
		System.out.println("Write Was Success.\nWrote " + numWrite1 + " bytes to random device");
		System.out.println("\n");
		
 
		// Seek 
		OS.getOS().Seek(randomID, 30);
		System.out.println("\n");
		
		//test close() 
		OS.getOS().Close(randomID);
		System.out.println("\n"); 
		
		
		
		
		// Time to test FakeFileClass 
		System.out.println("Test FakeFileClass"); 
		// test open() 
				int fakefileID = OS.getOS().open("file,data.txt");
				System.out.println("Where we stored file data.dat in Kerneland int array : " + fakefileID); 
				System.out.println("\n"); 
				
				// test read() 
				byte fakefileRead[] = OS.getOS().Read(fakefileID, 100); 
				System.out.println("Fakefilesystem read : " + fakefileRead.toString() );
				System.out.println("Generate fakefile if read() was successed. ");
				for(byte f : fakefileRead) {
					System.out.println(f);
				}
				System.out.println("\n"); 
				
				
				// test write() 
				int numWrite2 = OS.getOS().Write(fakefileID, fakefileRead); 
				System.out.println("Write Was Success.\nWrote " + numWrite2 + " bytes to fake file system device");
				System.out.println("\n");
				
		 
				// Seek 
				OS.getOS().Seek(fakefileID, 30);
				System.out.println("\n");
				
				// Test new random process 
				System.out.println("Try new random device, before closing fakefile device to see whether we store this device in empty spot. ");
				int randomID2 = OS.getOS().open("random2,50"); 
				System.out.println("Where we stored file data.dat in Kerneland int array : " + randomID2); 
				System.out.println("\n"); 
				
				//test close() 
				System.out.println("Test fake file system close()"); 
				OS.getOS().Close(fakefileID);
				System.out.println("\n"); 
		       
				System.out.println("Test random device 2 close()"); 
				OS.getOS().Close(randomID2);
				System.out.println("\n"); 
				
				OS.Sleep(500);
		
	}
	
}
