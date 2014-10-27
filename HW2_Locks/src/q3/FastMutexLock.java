package q3;

// TODO 
// Implement Fast Mutex Algorithm
public class FastMutexLock implements MyLock {
    private volatile int x, y;
    private volatile boolean[] flag;
	
	public FastMutexLock(int numThread) {
		x = -1;
		flag = new boolean[numThread];
		for(int i = 0; i < numThread; i++) {
			flag[i] = false;
		}
		y = -1;
    }

    @Override
    public void lock(int myId) {
    	while(true) {
    		flag[myId] = true;
    		x = myId;
    		if (y != -1) {
    			flag[myId] = false;
    			while(y != -1) {
    				// no-op()
    			}
    			continue;
    		} 
    		else {
    			y = myId;
    			if (x == myId) {
    				return;
    			}
    			else {
    				flag[myId] = false;
    				boolean allDown;
    				do { // wait until all flags are down
    					allDown = true;
    					for(int i = 0; i < flag.length; i++) {
    						if(flag[i] == true) {
    							allDown = false;
    							break;
    						}
    					}
    				} while ( !allDown );
    				if (y == myId) {
    					return;
    				}
    				else {
    					while (y != -1) {
    						// no-op()
    					}
    					continue;
    				}
    			}
    		}
    		
    	}
    }

    @Override
    public void unlock(int myId) {
    	y = -1;
    	flag[myId] = false;
    }
}
