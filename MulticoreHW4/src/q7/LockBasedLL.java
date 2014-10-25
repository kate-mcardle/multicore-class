package q7;

import java.util.concurrent.locks.ReentrantLock;

public class LockBasedLL {
	private final ReentrantLock lock = new ReentrantLock();
	private Node sentinel;
	
	public class Node {
		private Integer val;
		private Node next;
		
		public Node(Integer val) {
			this.val = val;
			next = null;
		}
	}
	
	public LockBasedLL() {
		sentinel = new Node(0);
	}
	
	public boolean add(Integer x) {
		lock.lock();
		try {
			Node new_node = new Node(x);
			Node current_node = sentinel; 
			Node next_node;
			while (current_node.next != null) {
				next_node = current_node.next;
				if (next_node.val.equals(x)) { return false; }
				if (next_node.val > x) {
					new_node.next = next_node;
					break;
				}
				current_node = next_node;
			}
			current_node.next = new_node;
			return true;
		} finally {
			lock.unlock();
		}
	}
	
	public boolean remove(Integer x) {
		lock.lock();
		try {
			Node current_node = sentinel;
			Node next_node;
			while (current_node.next != null) {
				next_node = current_node.next;
				if (next_node.val.equals(x)) {
					if (next_node.next != null) {
						current_node.next = next_node.next;
					}
					else {
						current_node.next = null;
					}
					next_node.next = null;
					return true;
				}
				current_node = next_node;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}
	
	public boolean contains(Integer x) {
		lock.lock();
		try {
			Node current_node = sentinel;
			Node next_node;
			while (current_node.next != null) {
				next_node = current_node.next;
				if (next_node.val.equals(x)) {
//					System.out.println(x + " found");
					return true;
				}
				current_node = next_node;
			}
//			System.out.println(x + " NOT found");
			return false;
		} finally {
			lock.unlock();
		}
		
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
		Integer[] vals = new Integer[] {5, 3, 7, 5, 1, 4};		
        LLThread[] threads = new LLThread[vals.length];
        for (int i = 0; i < vals.length; i++) {
        	threads[i] = new LLThread(l, "add", vals[i]);
        	threads[i].start();
        }
        for(int i = 0; i < threads.length; i++) {
        	try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
        System.out.println(l);
        
        LLThread[] contains_threads = new LLThread[vals.length];
        for (int i = 0; i < vals.length; i++) {
        	contains_threads[i] = new LLThread(l, "contains", i);
        	contains_threads[i].start();
        }
        for(int i = 0; i < contains_threads.length; i++) {
        	try {
				contains_threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		
		System.out.println(l);
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
