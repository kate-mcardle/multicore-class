package q7;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedLL {
//	private final ReentrantLock current_node_lock = new ReentrantLock();
//	private final ReentrantLock next_node_lock = new ReentrantLock();
	private Node sentinel;
	public AtomicLong add_time = new AtomicLong();
	public AtomicLong remove_time = new AtomicLong();
	public AtomicLong contains_time = new AtomicLong();
	
	public class Node {
		private Integer val;
		private Node next;
		private boolean deleted;
		private final ReentrantLock node_lock = new ReentrantLock();
		
		public Node(Integer val) {
			this.val = val;
			next = null;
			deleted = false;
		}
		
		void lock() {
			node_lock.lock();
		}
		void unlock() {
			node_lock.unlock();
		}
	}
	
	public LockBasedLL() {
		sentinel = new Node(0);
	}
	
	public boolean validate(Node current, Node next) {
		return (!current.deleted) && (!next.deleted) && (current.next == next);
	}
	
	public boolean validate_end(Node current) {
		return (!current.deleted) && (current.next == null);
	}
	
	public boolean add(Integer x) {
		long t = System.currentTimeMillis();
		Node new_node = new Node(x);
		Node current_node = sentinel; 
		Node next_node;
		while (current_node.next != null) {
			next_node = current_node.next;
			if (next_node.val >= x) {
				current_node.lock();
				try {
					next_node.lock();
					try {
						if (validate(current_node, next_node)) {
							if (next_node.val.equals(x)) {
								t = System.currentTimeMillis() - t;
								add_time.getAndAdd(t);
								return false;	
							}
							else {
								new_node.next = next_node;
								current_node.next = new_node;
								t = System.currentTimeMillis() - t;
								add_time.getAndAdd(t);
								return true;
							}
						}
					} finally {
						next_node.unlock();
					}
				} finally {
					current_node.unlock();
				}
				// If we get here, validate failed, so start over
				current_node = sentinel;
				continue;				
			}	
			current_node = next_node;
		}
		// If we get here, we traversed the whole list, so add to end
		current_node.lock();
		try {
			if (validate_end(current_node)) {
				current_node.next = new_node;
				t = System.currentTimeMillis() - t;
				add_time.getAndAdd(t);
				return true;
			}
		} finally {
			current_node.unlock();
		}
		// If we get here, validate failed, so start over
//		System.out.println("Validate on end of list failed, so starting over");
		return this.add(x);
	}
	
	public boolean remove(Integer x) {
		long t = System.currentTimeMillis();
		Node current_node = sentinel;
		Node next_node;
		while (current_node.next != null) {
			next_node = current_node.next;
			if (next_node.val >= x) {
				current_node.lock();
				try {
					next_node.lock();
					try {
						if (validate(current_node, next_node)) {
							if (next_node.val.equals(x)) {
								next_node.deleted = true;
								if (next_node.next != null) {
									current_node.next = next_node.next;
								}
								else {
									current_node.next = null;
								}
								next_node.next = null;
								t = System.currentTimeMillis() - t;
								remove_time.getAndAdd(t);
								return true;
							}
							else {
								t = System.currentTimeMillis() - t;
								remove_time.getAndAdd(t);
								return false;
							}
						}
					} finally {
						next_node.unlock();
					}
				} finally {
					current_node.unlock();
				}
				// If we get here, validate failed, so start over
				current_node = sentinel;
				continue;
			}
			current_node = next_node;
		}
		t = System.currentTimeMillis() - t;
		remove_time.getAndAdd(t);
		return false;
	}
	
	public boolean contains(Integer x) {
		long t = System.currentTimeMillis();
		Node current_node = sentinel;
		Node next_node;
		while (current_node.next != null) {
			next_node = current_node.next;
			if (next_node.val.equals(x)) {
//				System.out.println(x + " found");
				t = System.currentTimeMillis() - t;
				contains_time.getAndAdd(t);
				return true;
			}
			else if (next_node.val > x) {
//				System.out.println(x + " NOT found");
				t = System.currentTimeMillis() - t;
				contains_time.getAndAdd(t);
				return false;
			}
			current_node = next_node;
		}
//		System.out.println(x + " NOT found");
		t = System.currentTimeMillis() - t;
		contains_time.getAndAdd(t);
		return false;
	}
	
	@Override
	public String toString() {
		Node current_node = sentinel;
		StringBuilder sb = new StringBuilder();
		while (current_node.next != null) {
			Node next_node = current_node.next;
			sb.append(next_node.val+" ");
			current_node = next_node;
		}
		return sb.toString();
	}

	public static void main (String[] args) {
		LockBasedLL l = new LockBasedLL();
		
		int n_adds = 0;
		int n_removes = 0;
		int n_contains = 0;
		int total_ops = 25000;
		Random rgen = new Random();
		
		int n_threads = 1;
		
		LLThread[] add_threads = new LLThread[5000];
		for (int i = 0; i < add_threads.length; i++) {
			Integer x = rgen.nextInt(100001);
			add_threads[i] = new LLThread(l, "add", x);
			add_threads[i].start();
		}
		
		for (int i = 0; i < add_threads.length; i++) {
			try {
				add_threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
//		System.out.println("Time to add first 5000: " + l.add_time);
		l.add_time.set(0);
		
		LLThread[] threads = new LLThread[25000];
		for (int i = 0; i < threads.length; i++) {
			Integer x = rgen.nextInt(100001);
			double p = Math.random();
			if (p < 0.4) {
				n_adds++;
				threads[i] = new LLThread(l, "add", x);
			}
			else if (p < 0.9) {
				n_removes++;
				threads[i] = new LLThread(l, "remove", x);
			}
			else {
				n_contains++;
				threads[i] = new LLThread(l, "contains", x);
			}
			threads[i].start();
		}
		
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
//		System.out.println("Time for " + n_adds + " adds = " + l.add_time);
//		System.out.println("Time for " + n_removes + " removes = " + l.remove_time);
//		System.out.println("Time for " + n_contains + " contains = " + l.contains_time);
		
		double l_add = l.add_time.get()/(n_adds+0.0);
		System.out.println("Latency for add: " + l_add + " ms/adds");
		
		double l_remove = l.remove_time.get()/(n_removes+0.0);
		System.out.println("Latency for remove: " + l_remove + " ms/remove");
		
		double l_contains = l.contains_time.get()/(n_contains+0.0);
		System.out.println("Latency for contains: " + l_contains + " ms/contains");
		
		double throughput = 1000*total_ops/(l.contains_time.get() + l.remove_time.get() + l.add_time.get() + 0.0);
		System.out.println("Overall throughput: " + throughput + " ops/ms");
		
		
//		l.add(3);
//		l.add(3);
//		Integer[] vals = new Integer[] {2, 3, 2, 10, 3, 4};		
//        LLThread[] threads = new LLThread[vals.length];
//        for (int i = 0; i < vals.length; i++) {
//        	threads[i] = new LLThread(l, "add", vals[i]);
//        	threads[i].start();
//        }
//        for(int i = 0; i < threads.length; i++) {
//        	try {
//				threads[i].join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//        }
//        
//        System.out.println(l);
//        
//        LLThread[] contains_threads = new LLThread[vals.length];
//        for (int i = 0; i < vals.length; i++) {
//        	contains_threads[i] = new LLThread(l, "remove", i);
//        	contains_threads[i].start();
//        }
//        for(int i = 0; i < contains_threads.length; i++) {
//        	try {
//				contains_threads[i].join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//        }
//		
//		System.out.println(l);
	}
	
    public static class LLThread extends Thread {
    	private volatile LockBasedLL l;
    	private String op;
    	private Integer val;
    	
    	public LLThread(LockBasedLL l, String op, Integer val) {
    		this.l = l;
    		this.op = op;
    		this.val = val;
    	}
    	
    	public void run() {
    		if (this.op.equals("add")) {
    			l.add(val);
    		}
    		
    		if (this.op.equals("remove")) {
    			l.remove(val);
    		}
    		
    		if (this.op.equals("contains")) {
    			l.contains(val);
    		}
    	}
    }

}
