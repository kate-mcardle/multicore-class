package q2;

import java.util.concurrent.locks.ReentrantLock;

public class LockBasedQueue<T> extends MyQueue<T> {
	private final ReentrantLock enqLock;
	private final ReentrantLock deqLock;
	
	private Node head;
	private Node tail;
	
	private class Node {
		public T val;
		public Node next;
		public Node(T x) {
			val = x;
			next = null;
		}
	}
	
	public LockBasedQueue() {
		enqLock = new ReentrantLock();
		deqLock = new ReentrantLock();
		head = new Node(null);
		tail = head;
	}
	
	@Override
	public void enq(T x) {
		enqLock.lock();
		try {
			Node newNode = new Node(x);
			tail.next = newNode;
			tail = newNode;
		} finally {
			enqLock.unlock();
		}
		
	}

	@Override
	public T deq() {
		T result;
		deqLock.lock();
		try {
			while (head.next == null) { }
			result = head.next.val;
			head = head.next;
		} finally {
			deqLock.unlock();
		}
		return result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Node current_node = head;
		while (current_node != tail) {
			sb.append(current_node.next.val+" ");
			current_node = current_node.next;
		}
		return sb.toString();
	}

}
