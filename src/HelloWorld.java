import java.util.Timer;
import java.util.TimerTask;

public class HelloWorld extends UserlandProcess {
	
	
		// TODO Auto-generated method stub
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// do process 
			while (true) {
				try {
					
					System.out.println("Hello world");
					OS.Sleep(250); // instead of this use OS.stop() 
					
				}catch(Exception e) {
					
				}
				
			}
		}

	
}
