package q3;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

// TODO
// Implement the bakery algorithm

public class BakeryLock implements MyLock {
	
	private volatile AtomicIntegerArray label;
	private volatile AtomicReferenceArray<Boolean> flag;
	
    public BakeryLock(int numThread) {
    	label = new AtomicIntegerArray(numThread);
    	flag = new AtomicReferenceArray<Boolean>(numThread);
    	for (int i = 0; i < numThread; i++) {
    		flag.set(i, false);
    	}
    }

    @Override
    public void lock(int myId) {
    	flag.set(myId, true);
    	for (int i = 0; i < label.length(); i++) {
    		if (label.get(i) > label.get(myId)) { label.set(myId, label.get(i)); }
    	}
    	label.incrementAndGet(myId);
    	while(true) {
    		boolean exists = false;
    		for (int k = 0; k < label.length(); k++) {
    			if ((k != myId) && (flag.get(k) == true) && 
    					( (label.get(k) < label.get(myId)) || 
    							( (label.get(k) == label.get(myId)) && (k < myId) ) )) {
    				exists = true;
    			}
    		}
    		if (!exists) {
    			break;
    		}
    	}
    }

    @Override
    public void unlock(int myId) {
    	flag.set(myId, false);
    }
}
