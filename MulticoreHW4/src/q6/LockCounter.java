package q6;

public class LockCounter extends Counter {
	MyLock lock;
	
    public LockCounter(MyLock lock) {
    	super();
    	this.lock = lock;
    }

    @Override
    public void increment() {
//    	int myId = Integer.valueOf(Thread.currentThread().getName());
    	try {
    		lock.lock();
        	count = count + 1;
    	} finally {
    		lock.unlock();	
    	}
    }

    @Override
    public int getCount() {
        return count;
    }
}
