package q7;


//import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedLL extends MyLinkedList {
	private Node sentinel;
	
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
//		long t = System.currentTimeMillis();
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
//								t = System.currentTimeMillis() - t;
//								add_time.getAndAdd(t);
								return false;	
							}
							else {
								new_node.next = next_node;
								current_node.next = new_node;
//								t = System.currentTimeMillis() - t;
//								add_time.getAndAdd(t);
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
//				t = System.currentTimeMillis() - t;
//				add_time.getAndAdd(t);
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
//		long t = System.currentTimeMillis();
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
//								t = System.currentTimeMillis() - t;
//								remove_time.getAndAdd(t);
								return true;
							}
							else {
//								t = System.currentTimeMillis() - t;
//								remove_time.getAndAdd(t);
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
//		t = System.currentTimeMillis() - t;
//		remove_time.getAndAdd(t);
		return false;
	}
	
	public boolean contains(Integer x) {
//		long t = System.currentTimeMillis();
		Node current_node = sentinel;
		Node next_node;
		while (current_node.next != null) {
			next_node = current_node.next;
			if (next_node.val.equals(x)) {
//				System.out.println(x + " found");
//				t = System.currentTimeMillis() - t;
//				contains_time.getAndAdd(t);
				return true;
			}
			else if (next_node.val > x) {
//				System.out.println(x + " NOT found");
//				t = System.currentTimeMillis() - t;
//				contains_time.getAndAdd(t);
				return false;
			}
			current_node = next_node;
		}
//		System.out.println(x + " NOT found");
//		t = System.currentTimeMillis() - t;
//		contains_time.getAndAdd(t);
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
}
