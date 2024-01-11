import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//instance of classes
		HelloWorld hWorld = new HelloWorld(); 
		GoodbyeWorld gWorld = new GoodbyeWorld(); 
		//OS.StarUp(hWorld);
	   //OS.CreateProcess(gWorld, PriorityEnum.Realtime); 
		
		
		TestUserland tul = new TestUserland(); 
	//	OS.StarUp(tul);  // we will call Userland Process to open, read, write, seek, close devices. 
        Ping ping = new Ping(); 
        Pong pong = new Pong(); 
        
       // OS.StarUp(ping);
      //  OS.CreateProcess(pong);  
        
        TestPaging tp = new TestPaging(); 
        OS.CreateProcess(tp, 2048); 
        
        TestPaging2 tp2 = new TestPaging2(); 
        OS.StarUp(tp2, 3072);
	
		
	}

}
