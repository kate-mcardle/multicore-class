package q7;

import java.util.Random;

public class Main {
	public static void main (String[] args) {
		MyLinkedList l = null;
		String l_type = args[0];

		int[] n_threads_arr = {1, 2, 3, 4, 5, 6, 7, 8 };
		for (int t = 0; t < n_threads_arr.length; t++) {
			if (l_type.equals("lock-based")) {
				l = new LockBasedLL();
			}
			else if (l_type.equals("lock-free")){
				l = new LockFreeLL();
			}
			else if (l_type.equals("book")) {
				l = new LockBasedLL_Book();
			}
			else {
				System.err.println("ERROR: no such algorithm implemented");
				System.exit(-1);
			}

			int n_adds = 0;
			int n_removes = 0;
			int n_contains = 0;
			int initial_adds = 5000;
			int total_ops = 25000;
			
			long add_time = 0;
			long remove_time = 0;
			long contains_time = 0;

			int n_threads = n_threads_arr[t];
			int r = initial_adds % n_threads;

			LLThread[] add_threads = new LLThread[n_threads];

			for (int i = 0; i < add_threads.length; i++) {
				int n_adds_per_thread = initial_adds/n_threads;
				if (i < r) { 
					n_adds_per_thread++; 
				}
				add_threads[i] = new LLThread(l, "add", n_adds_per_thread);
			}
			
			long t1 = System.currentTimeMillis();
			for (int i = 0; i < add_threads.length; i++) {
				add_threads[i].start();
			}

			for (int i = 0; i < add_threads.length; i++) {
				try {
					add_threads[i].join();
					n_adds += add_threads[i].n_adds;
					add_time += add_threads[i].add_time;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			t1 = System.currentTimeMillis() - t1;
			System.out.println("Time to do first adds: " + t1);
				
			r = total_ops % n_threads;

			LLThread[] threads = new LLThread[n_threads];

			for (int i = 0; i < threads.length; i++) {
				int n_ops_per_thread = total_ops/n_threads;
				if (i < r) { 
					n_ops_per_thread++; 
				}
				threads[i] = new LLThread(l, "rand", n_ops_per_thread);
			}

			long t2 = System.currentTimeMillis();
			for (int i = 0; i < threads.length; i++) {
				threads[i].start();
			}

			for (int i = 0; i < threads.length; i++) {
				try {
					threads[i].join();
					n_adds += threads[i].n_adds;
					n_removes += threads[i].n_removes;
					n_contains += threads[i].n_contains;
					add_time += threads[i].add_time;
					remove_time += threads[i].remove_time;
					contains_time += threads[i].contains_time;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			t2 = System.currentTimeMillis() - t2;
			long overall_time = t1 + t2;

			System.out.println("For " + n_threads + " threads: ------------------------");
			System.out.println("Total ops done = " + (n_adds + n_removes + n_contains));
//			double l_add = l.add_time.get()/(n_adds+0.0);
			double l_add = add_time/(n_adds+0.0);
			System.out.println("Latency for add: " + l_add + " ms/adds");

//			double l_remove = l.remove_time.get()/(n_removes+0.0);
			double l_remove = remove_time/(n_removes+0.0);
			System.out.println("Latency for remove: " + l_remove + " ms/remove");

//			double l_contains = l.contains_time.get()/(n_contains+0.0);
			double l_contains = contains_time/(n_contains+0.0);
			System.out.println("Latency for contains: " + l_contains + " ms/contains");

			double throughput = 1000*total_ops/(overall_time + 0.0);
			System.out.println("Overall time taken = " + overall_time + " ms");
			System.out.println("Overall throughput: " + throughput + " ops/s");
		}
	}
	
    public static class LLThread extends Thread {
    	private volatile MyLinkedList l;
    	private String op;
    	private int n_ops;
    	
    	public int n_adds = 0;
    	public int n_contains = 0;
    	public int n_removes = 0;
    	
    	public long add_time = 0;
    	public long remove_time = 0;
    	public long contains_time = 0;
    	
    	public LLThread(MyLinkedList l, String op, int n_ops) {
    		this.l = l;
    		this.op = op;
    		this.n_ops = n_ops;
    	}
    	
    	public void run() {
    		if (this.op.equals("add")) {
    			Random rgen = new Random();
    			for (int i = 0; i < n_ops; i++) {
    				n_adds++;
    				long t = System.currentTimeMillis();
    				l.add(rgen.nextInt(100001));
    				add_time += System.currentTimeMillis() - t;
    			}
    		}
    		
    		if (this.op.equals("rand")) {
    			Random rgen = new Random();
    			for (int i = 0; i < n_ops; i++) {
    				double p = Math.random();
    				if (p < 0.4) {
    					n_adds++;
    					long t = System.currentTimeMillis();
    					l.add(rgen.nextInt(100001));
    					add_time += System.currentTimeMillis() - t;
    				}
    				else if (p < 0.9) {
    					n_removes++;
    					long t = System.currentTimeMillis();
    					l.remove(rgen.nextInt(100001));
    					remove_time += System.currentTimeMillis() - t;
    				}
    				else {
    					n_contains++;
    					long t = System.currentTimeMillis();
    					l.contains(rgen.nextInt(100001));
    					contains_time += System.currentTimeMillis() - t;
    				}    				
    			}
    		}
    	}
    }
}
