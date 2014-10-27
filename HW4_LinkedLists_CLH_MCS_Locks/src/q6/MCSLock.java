package q6;

/**
Based on implementation provided in Herlihy's Art of Multiprocessor Programming
*/

import java.util.concurrent.atomic.AtomicReference;

public class MCSLock implements MyLock {
	private AtomicReference<Qnode> tail = new AtomicReference<Qnode>(null);
	private ThreadLocal<Qnode> myNode;
	
	public MCSLock() {
		myNode = new ThreadLocal<Qnode>() {
			protected Qnode initialValue() {
				return new Qnode();
			}
		};
	}
	
    @Override
    public void lock() {
    	Qnode mine = myNode.get();
    	Qnode prev = tail.getAndSet(mine);
    	if (prev != null) {
    		mine.locked = true;
    		prev.next = mine;
    		while (mine.locked) {}
    	}
    }
    
    @Override
    public void unlock() {
    	Qnode mine = myNode.get();
    	if (mine.next == null) {
    		if (tail.compareAndSet(mine, null)) {
    			return;
    		}
    		while (mine.next == null) { }
    	}
    	mine.next.locked = false;
    	mine.next = null;
    }
    
    private class Qnode {
    	private volatile boolean locked = false;
    	private Qnode next = null;
    }
}
