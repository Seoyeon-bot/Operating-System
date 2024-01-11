
public class Pong  extends UserlandProcess{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	
		while(true) {
			
			// send message to ping 
			KernelMessage km2 = new KernelMessage(); 
			km2.setSenderPid(OS.getOS().GetPid());  // set sender's pid 
			System.out.println("\nIn pong: sender pid : " + km2.getSenderPid());  // print sender's pid 
			
			km2.setReceiverPid(OS.getOS().GetPidByName("Ping")); 
			System.out.println("In pong: receiver pid : " + km2.getReceiverPid());  // print receiver's pid 
			
			
			km2.what = 0; 
			//String msg = "Ping"; 
			km2.dataArray[0] = 2;   // in KernelMessage data array store 1 in index 0 
			String message = "Ping: from " + km2.getSenderPid() + " to: " + km2.getReceiverPid() + " what: " + km2.what; 
			km2.setMsg(message);
			System.out.println("I am PONG, ping = " + km2.getReceiverPid()); 
			
			
			OS.getOS().WaitForMessage();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OS.getOS().SendMessage(km2);
			km2.what+=1;
		}
	
		
	}

}
