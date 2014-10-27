package q3;

// TODO
// Use MyLock to protect the count

public class LockCounter extends Counter {
	MyLock lock;
	
    public LockCounter(MyLock lock) {
    	super();
    	this.lock = lock;
    }

    @Override
    public void increment() {
    	int myId = Integer.valueOf(Thread.currentThread().getName());
    	try {
    		lock.lock(myId);
        	count = count + 1;
    	} finally {
    		lock.unlock(myId);	
    	}
    }

    @Override
    public int getCount() {
        return count;
    }
}
