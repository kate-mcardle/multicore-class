package q2;

import java.util.Stack;

public abstract class MyStack<T> {
	public abstract void push(T x);
	public abstract T pop();
	
	protected class Node {
		public T val;
		public Node next;
		public Node(T x) {
			val = x;
			next = null;
		}
	}
	
	// debug:
	public abstract Stack<T> toStack();
	public abstract String toString();
}
