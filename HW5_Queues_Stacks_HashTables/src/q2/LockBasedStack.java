package q2;

import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedStack<T> extends MyStack<T> {
	private final ReentrantLock lock;
	private Node top;
	
	public LockBasedStack() {
		lock = new ReentrantLock();
		top = null;
	}
	

	@Override
	public void push(T x) {
		lock.lock();
		try {
			Node new_node = new Node(x);
			new_node.next = top;
			top = new_node;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public T pop() {
		T result;
		lock.lock();
		try {
			while (top == null) { }
			result = top.val;
			top = top.next;
		} finally {
			lock.unlock();
		}
		return result;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Node current_node = top;
		while (current_node != null) {
			sb.append(current_node.val+" ");
			current_node = current_node.next;
		}
		return sb.toString();
	}
	
	// debug:
	public Stack<T> toStack() {
		Stack<T> s = new Stack<T>();
		Node current_node = top;
		while (current_node != null) {
			s.push(current_node.val);
			current_node = current_node.next;
		}
		return s;
	}
	
	

}
