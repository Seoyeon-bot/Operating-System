import java.util.Timer;
import java.util.TimerTask;

public class GoodbyeWorld extends UserlandProcess {
		// create infinite loop that prints Goodbye world 
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// do process 
		while (true) {
			try {
				
				System.out.println("Goodbye world");
				OS.Sleep(250);
				
				
				// Thread.interrupted(); 
				 // sleep for 50ms 
			}catch(Exception e) {
				
			}
			
		}
	}

}
