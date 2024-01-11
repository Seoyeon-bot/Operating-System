
public class Ping  extends UserlandProcess{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	
		while(true) {
			KernelMessage km = new KernelMessage(); 
			km.setSenderPid(OS.getOS().GetPid()); // return currently running process Pid 
			System.out.println("In ping: sender pid : " + km.getSenderPid()); 
			
			// Ping want to send message to Pong. 
			km.setReceiverPid(OS.getOS().GetPidByName("Pong")); // set reciver's pid with Pong's class process 
			System.out.println("In ping: receiver pid : " + km.getReceiverPid()); 
			
			int what = 0; 
			String message = "Ping: from " + km.getSenderPid() + " to: " + km.getReceiverPid() + " what: " + km.what; 
			km.setMsg(message);
			km.dataArray[0] = 1;   // in KernelMessage data array store 1 in index 0 

		
			System.out.println("I am PING, pong = " + km.getReceiverPid()); 
			
			OS.getOS().SendMessage(km);  // send message 
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			OS.getOS().WaitForMessage();  // wait for message 
			km.what+=1;  // increment by one
			
		}
		
	
	}

}
