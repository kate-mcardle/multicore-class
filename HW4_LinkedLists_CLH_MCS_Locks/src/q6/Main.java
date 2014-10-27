package q6;

public class Main {

    public static void main (String[] args) {
        Counter counter = null;
        MyLock lock;
        long executeTimeMS = 0;
        int numThread = 6;
        int numTotalInc = 1200000;

        if (args.length < 3) {
            System.err.println("Provide 3 arguments");
            System.err.println("\t(1) <algorithm>: CLH/MCS/reentrant");
            System.err.println("\t(2) <numThread>: the number of test thread");
            System.err.println("\t(3) <numTotalInc>: the total number of "
                    + "increment operations performed");
            System.exit(-1);
        }

        if (args[0].equals("CLH")) {
            lock = new CLHLock();
            counter = new LockCounter(lock);
        } else if (args[0].equals("MCS")) {
            lock = new MCSLock();
            counter = new LockCounter(lock);
        } else if (args[0].equals("reentrant")) {
            counter = new ReentrantCounter();
        } else {
            System.err.println("ERROR: no such algorithm implemented");
            System.exit(-1);
        }

        numThread = Integer.parseInt(args[1]);
        numTotalInc = Integer.parseInt(args[2]);

        // Please create numThread threads to increment the counter
        // Each thread executes numTotalInc/numThread increments
        // Please calculate the total execute time in millisecond and store the
        // result in executeTimeMS
        
        int n_increments = numTotalInc/numThread;
        CounterThread[] threads = new CounterThread[numThread];
        for (int i = 0; i < numThread; i++) {
        	threads[i] = new CounterThread(counter, n_increments, String.valueOf(i));
        }
        executeTimeMS = System.currentTimeMillis();
        for (int i = 0; i < numThread; i++) {
        	threads[i].start();
        }
        for(int i = 0; i < numThread; i++) {
        	try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        executeTimeMS = System.currentTimeMillis() - executeTimeMS;
        System.out.println("counter = " + counter.getCount());

        System.out.println(executeTimeMS);
    }
    
    public static class CounterThread extends Thread {
    	private volatile Counter c;
    	private int n_increments;
    	
    	public CounterThread(Counter c, int n_increments, String name) {
    		super(name);
    		this.c = c;
    		this.n_increments = n_increments;
    	}
    	
    	public void run() {
    		for(int i = 0; i < n_increments; i++) {
    			c.increment();
    		}
    	}
    }
}


