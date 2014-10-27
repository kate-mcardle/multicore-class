package q6;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantCounter extends Counter {
	private final ReentrantLock lock = new ReentrantLock();
	
 @Override
 public void increment() {
 	lock.lock();
 	try {
 		count++;
 	} finally {
 		lock.unlock();
 	}
 }

 @Override 
 public int getCount() {
     return count;
 }
}
