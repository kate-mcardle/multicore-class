package q5;

//import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// TODO
// Use locks and condition variables to implement the bathroom protocol
public class BathroomLockProtocol implements Protocol {
	// declare the lock and conditions here
	Lock metaLock = new ReentrantLock();
	Condition maleCondition = metaLock.newCondition();
	Condition femaleCondition = metaLock.newCondition();
	int maleEnters = 0;
	int maleLeaves = 0;
	int femaleEnters = 0;
	int femaleLeaves = 0;
	
	Condition lineCondition = metaLock.newCondition();
	long maleInLine = -1; // -1 indicates no male is in line; else, thread ID of last male to get in line
	long femaleInLine = -1;

	public void enterMale() { // delays caller until a male can enter bathroom
		metaLock.lock();
		try {
			while ( femaleInLine != -1 ) {
				try {
					lineCondition.await();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} // no female in line
			maleInLine = Thread.currentThread().getId(); // male has made it in the line
			while ( femaleEnters != femaleLeaves ) { // female is in the bathroom
				try {
					maleCondition.await(); // male waits in line
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} // no females in bathroom
			maleEnters++;
			if ( maleInLine == Thread.currentThread().getId() ) { // if I was the last male to get in line
				maleInLine = -1;
				lineCondition.signalAll();
			}
//			System.out.println("male " + Thread.currentThread().getId() + " entered bathroom");
		} finally {
			metaLock.unlock();
		}
	}

	public void leaveMale() {  // called when a male leaves the bathroom
		metaLock.lock();
		try {
			maleLeaves++;
			if (maleEnters == maleLeaves) {
				femaleCondition.signalAll();
			}
//			System.out.println("male " + Thread.currentThread().getId() + " left bathroom");
		} finally {
			metaLock.unlock();
		}
	}

	public void enterFemale() { // delays caller until a female can enter bathroom
		metaLock.lock();
		try {
			while ( maleInLine != -1 ) {
				try {
					lineCondition.await();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			femaleInLine = Thread.currentThread().getId();
			while ( maleEnters != maleLeaves ) { // male is in the bathroom
				try {
					femaleCondition.await(); // female waits in line
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			femaleEnters++;
			if ( femaleInLine == Thread.currentThread().getId() ) { // if I was the last female to get in line
				femaleInLine = -1;
				lineCondition.signalAll();
			}
//			System.out.println("female " + Thread.currentThread().getId() + " entered bathroom");
		} finally {
			metaLock.unlock();
		}

	}

	public void leaveFemale() { // called when a female leaves the bathroom
		metaLock.lock();
		try {
			femaleLeaves++;
			if (femaleEnters == femaleLeaves) {
				maleCondition.signalAll();
			}
//			System.out.println("female " + Thread.currentThread().getId() + " left bathroom");
		} finally {
			metaLock.unlock();
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
//	
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
