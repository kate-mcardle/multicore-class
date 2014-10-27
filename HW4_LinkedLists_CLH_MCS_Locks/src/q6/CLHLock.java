package q6;

/**
Based on implementation provided in Herlihy's Art of Multiprocessor Programming
*/

import java.util.concurrent.atomic.AtomicReference;

public class CLHLock implements MyLock {
	private AtomicReference<Qnode> tail = new AtomicReference<Qnode>(new Qnode());
	private ThreadLocal<Qnode> myNode;
	private ThreadLocal<Qnode> myPrev;
	
	public CLHLock() {
		myNode = new ThreadLocal<Qnode>() {
			protected Qnode initialValue() {
				return new Qnode();
			}
		};
		myPrev = new ThreadLocal<Qnode>() {
			protected Qnode initialValue() {
				return null;
			}
		};
	}
	
    @Override
    public void lock() {
    	Qnode mine = myNode.get();
    	mine.locked = true;
    	Qnode prev = tail.getAndSet(mine);
    	myPrev.set(prev);
    	while (prev.locked) { }
    }
    
    @Override
    public void unlock() {
    	Qnode mine = myNode.get();
    	mine.locked = false;
    	myNode.set(myPrev.get());
    }
    
    public class Qnode {
    	private volatile boolean locked;
    }

}
