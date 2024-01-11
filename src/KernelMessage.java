/**
 * holds values for message
 * @author choeseoyeon
 *
 */
public class KernelMessage {

	//message oject 
		private String message ; 
		private int senderPid; 
		private int receiverPid; 
		
		
		//it should have an integer indicating “what” this message is; 
		int what = 0; // increment this by 1. 
		int size = 30; 
		// Byte[] array of data size = 10
		byte[] dataArray = new byte[size] ;
		// copiedData will stored in copyArray 
		byte[] copyArray = new byte[size]; 
		
		
		/**
		 * Create a copy constructor – 
		 * a constructor that accepts a Kernel message and makes a copy of it. 
		 */
		
	   public KernelMessage() {
		   
	   }
		public KernelMessage(KernelMessage km) {
			// mesage will be like 0 12 3 integer
			// make usre you are copyin  dat ( byte[] array ) 
			this.receiverPid = km.receiverPid; 
			this.dataArray = km.dataArray; 
			this.copyArray = km.copyArray; 
			this.message = km.getMsg(); 
			this.senderPid = km.senderPid;
			
			String inputMessage = km.getMsg(); 
			km.dataArray = inputMessage.getBytes();
			
			for(int i = 0; i < km.dataArray.length; i++) {
				//iterate over and copy all data 
				Byte data = km.dataArray[i]; 
				copyArray[i] = data; // store in copyArray index i 
			}
		}
		
		public KernelMessage(int senderPid, int receiverPid, String message) {
			this.senderPid = senderPid; 
			this.receiverPid = receiverPid; 
			this.message = message; 
		}

		/**
		 * Create ToString() for debuging purpose. 
		 * 
		 */
		public void ToString () {
			//iterate over copy array and print it out 
			System.out.println("Iterate over copy array in KernelMessage and print out the elements"); 
			for(int i =0; i < copyArray.length; i++) {
				System.out.println(copyArray[i]); 
			}
		}
		/**
		 * mutators for senderpid / receiverpid
		 */
		public int getSenderPid() {
			return senderPid; 
		}
		public int getReceiverPid() { 
			return receiverPid;
		}
		public String getMsg() {
			return message; 
		}
		
		public void setMsg(String message) {
			this.message = message;
		}
		public void setSenderPid(int senderPid) {
			this.senderPid = senderPid; 
		}
		public void setReceiverPid(int receiverPid) {
			this.receiverPid = receiverPid;
		}
}
