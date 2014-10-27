package q5;

//import java.util.Random;
//
//import q5.BathroomLockProtocol.FemaleThread;
//import q5.BathroomLockProtocol.MaleThread;

// TODO
// Use synchronized, wait(), notify(), and notifyAll() to implement the bathroom
// protocol
public class BathroomSynProtocol implements Protocol {
	// declare the lock and conditions here
	int maleEnters = 0;
	int maleLeaves = 0;
	int femaleEnters = 0;
	int femaleLeaves = 0;
	
	long maleInLine = -1; // -1 indicates no male is in line; else, thread ID of last male to get in line
	long femaleInLine = -1;

	public void enterMale() { // delays caller until a male can enter bathroom
		synchronized(this) {
			while ( femaleInLine != -1 ) {
				try {
					this.wait();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} // no female in line
			maleInLine = Thread.currentThread().getId(); // male has made it in the line
			while ( femaleEnters != femaleLeaves ) { // female is in the bathroom
				try {
					this.wait(); // male waits in line
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} // no females in bathroom
			maleEnters++;
			if ( maleInLine == Thread.currentThread().getId() ) { // if I was the last male to get in line
				maleInLine = -1;
				this.notifyAll();
			}
//			System.out.println("male " + Thread.currentThread().getId() + " entered bathroom");
		}
	}

	public void leaveMale() {  // called when a male leaves the bathroom
		synchronized(this) {
			maleLeaves++;
			if (maleEnters == maleLeaves) {
				this.notifyAll();
			}
			System.out.println("male " + Thread.currentThread().getId() + " left bathroom");
		}
	}

	public void enterFemale() { // delays caller until a female can enter bathroom
		synchronized(this) {
			while ( maleInLine != -1 ) {
				try {
					this.wait();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			femaleInLine = Thread.currentThread().getId();
			while ( maleEnters != maleLeaves ) { // male is in the bathroom
				try {
					this.wait(); // female waits in line
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			femaleEnters++;
			if ( femaleInLine == Thread.currentThread().getId() ) { // if I was the last female to get in line
				femaleInLine = -1;
				this.notifyAll();
			}
//			System.out.println("female " + Thread.currentThread().getId() + " entered bathroom");
		}

	}

	public void leaveFemale() { // called when a female leaves the bathroom
		synchronized(this) {
			femaleLeaves++;
			if (femaleEnters == femaleLeaves) {
				this.notifyAll();
			}
			System.out.println("female " + Thread.currentThread().getId() + " left bathroom");
		}
	}
	
//	static BathroomLockProtocol p = new BathroomLockProtocol();
//	
//	public static class MaleThread extends Thread {
//		public void run() {
//			p.enterMale();
//			try {
//				sleep(2);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			p.leaveMale();
//		}
//	}
//
//	public static class FemaleThread extends Thread {
//		public void run() {
//			p.enterFemale();
//			try {
//				sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			p.leaveFemale();
//		}
//	}
	
//    public static void main (String[] args) {
//    	// randomly have male/female threads go to the bathroom
//    	Thread[] threads = new Thread[20];
////        Random randomGenerator = new Random();
////    	for (int i = 0; i < 20; i++) {
////    		int gender = randomGenerator.nextInt(2);
////    		if (gender == 0) {
////    			threads[i] = new MaleThread();		
////    		} else {
////    			threads[i] = new FemaleThread();
////    		}
////    		System.out.println("Starting thread " + threads[i].getId());
////    		threads[i].start();
////    	}
//    	
//    	// have mostly females and a few males go to the bathroom
//    	for (int i = 0; i < 20; i++) {
//    		if (i == 6 || i == 12 || i == 19) {
//    			threads[i] = new MaleThread();
//    		} else {
//    			threads[i] = new FemaleThread();
//    		}
//    		System.out.println("Starting thread " + threads[i].getId());
//    		threads[i].start();
//    	}
//    	
//    	// have only males go to the bathroom = done! no starvation
//    	
//    }


}
