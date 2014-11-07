package q2;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleLockFreeStack<T> extends MyStack<T> {
	private AtomicReference<Node> top;
	
	public SimpleLockFreeStack() {
		top = new AtomicReference<Node>(null);
	}
	
	public boolean tryPush(Node new_node) {
		Node old_top = top.get();
		new_node.next = old_top;
		return(top.compareAndSet(old_top, new_node));		
	}
	
	public void push(T x) {
		Node new_node = new Node(x);
		while (!tryPush(new_node)) { }
	}

	public Node tryPop() {
		Node old_top = top.get();
		Node new_top = old_top.next;
		if (top.compareAndSet(old_top, new_top)) {
			return old_top;
		}
		return null;
	}
	
	public T pop() {
		while (true) {
			Node popped_node = tryPop();
			if (popped_node != null) {
				return popped_node.val;
			}
		}
	}

	@Override
	public Stack<T> toStack() {
		Stack<T> s = new Stack<T>();
		Node current_node = top.get();
		while (current_node != null) {
			s.push(current_node.val);
			current_node = current_node.next;
		}
		return s;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Node current_node = top.get();
		while (current_node != null) {
			sb.append(current_node.val+" ");
			current_node = current_node.next;
		}
		return sb.toString();
	}

}
