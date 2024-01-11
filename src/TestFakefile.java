
public class TestFakefile extends UserlandProcess{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		// test open() 
		System.out.println("**** Userland Class : TestFakeFile **** "); 
		int fakefileID2 = OS.getOS().open("file,data.txt");
		System.out.println("Where we stored file data.dat in Kerneland int array : " + fakefileID2); 
		System.out.println("\n"); 
		
		// test read() 
		byte fakefileRead[] = OS.getOS().Read(fakefileID2, 100); 
		System.out.println("Fakefilesystem read : " + fakefileRead.toString() );
		System.out.println("Generate fakefile if read() was successed. ");
		for(byte f : fakefileRead) {
			System.out.println(f);
		}
		System.out.println("\n"); 
		
		
		// test write() 
		int numWrite2 = OS.getOS().Write(fakefileID2, fakefileRead); 
		System.out.println("Write Was Success.\nWrote " + numWrite2 + " bytes to fake file system device");
		System.out.println("\n");
		

		// Seek 
		OS.getOS().Seek(fakefileID2, 30);
		System.out.println("\n");
		
		// Test new random process 
		System.out.println("Try new random device, before closing fakefile device to see whether we store this device in empty spot. ");
		int randomID2 = OS.getOS().open("random2,50"); 
		System.out.println("Where we stored file data.dat in Kerneland int array : " + randomID2); 
		System.out.println("\n"); 
		
		//test close() 
		System.out.println("Test fake file system close()"); 
		OS.getOS().Close(fakefileID2);
		System.out.println("\n"); 
		
	}

	
}
