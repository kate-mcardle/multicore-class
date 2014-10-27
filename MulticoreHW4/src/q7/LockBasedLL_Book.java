package q7;

/**
Based on implementation provided in Herlihy's Art of Multiprocessor Programming
*/

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedLL_Book extends MyLinkedList {
	private Node sentinel;
	
	public LockBasedLL_Book() {
		sentinel = new Node(Integer.MIN_VALUE);
		sentinel.next = new Node(Integer.MAX_VALUE);
	}
	
	public class Node {
		Integer val;
		Node next;
		boolean marked;
		Lock lock;
		
		Node(Integer x) {
			val = x;
			next = null;
			marked = false;
			lock = new ReentrantLock();
		}
		
		void lock() { lock.lock(); }
		void unlock() { lock.unlock(); }
	}
	
	private boolean validate(Node prev, Node curr) {
		return (!prev.marked) && (!curr.marked) && (prev.next == curr);
	}
	
	public boolean add(Integer x) {
		while (true) {
			Node prev = sentinel;
			Node curr = prev.next;
			while (curr.val < x) {
				prev = curr; curr = curr.next;
			}
			prev.lock();
			try {
				curr.lock();
				try {
					if (validate(prev, curr)) {
						if (curr.val.equals(x)) {
							return false;
						}
						else {
							Node new_node = new Node(x);
							new_node.next = curr;
							prev.next = new_node;
							return true;
						}
					}
				} finally {
					curr.unlock();
				}
			} finally {
				prev.unlock();
			}
		}
	}
	
	public boolean remove(Integer x) {
		while (true) {
			Node prev = sentinel;
			Node curr = prev.next;
			while (curr.val < x) {
				prev = curr; curr = curr.next;
			}
			prev.lock();
			try {
				curr.lock();
				try {
					if (validate(prev, curr)) {
						if (curr.val.equals(x)) {
							curr.marked = true;
							prev.next = curr.next;
							return true;
						}
						else {
							return false;
						}
					}
				} finally {
					curr.unlock();
				}
			} finally {
				prev.unlock();
			}
		}
	}
	
	public boolean contains(Integer x) {
		Node curr = sentinel;
		while (curr.val < x) {
			curr = curr.next;
		}
		return curr.val.equals(x) && !curr.marked;
	}
}
