package q2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeQueue<T> extends MyQueue<T> {
	
	private AtomicStampedReference<Node> head;
	private AtomicStampedReference<Node> tail;
	
	private class Node {
		public T val;
		public AtomicStampedReference<Node> next;
		public Node(T x) {
			val = x;
			next = new AtomicStampedReference<Node>(null, 0);
		}
	}
	
	public LockFreeQueue() {
		Node node = new Node(null);
		head = new AtomicStampedReference<Node>(node, 0);
		tail = new AtomicStampedReference<Node>(node, 0);;
	}

	@Override
	public void enq(T x) {
		Node new_node = new Node(x);
		Node tail_node = null;
		int[] tail_holder = new int[1];
		while (true) {
			tail_node = this.tail.get(tail_holder);
			int[] next_holder = new int[1];
			Node next_node = tail.getReference().next.get(next_holder);
			if (tail_node == this.tail.getReference()) {
				if (next_node == null) {
					if (tail_node.next.compareAndSet(next_node, new_node, next_holder[0], next_holder[0]+1)) {
						break;
					}
				}
				else {
					this.tail.compareAndSet(tail_node, next_node, tail_holder[0], tail_holder[0]+1);
				}
			}
		}
		this.tail.compareAndSet(tail_node, new_node, tail_holder[0], tail_holder[0]+1);
	}

	@Override
	public T deq() {
		T val = null;
		while (true) {
			int[] head_holder = new int[1];
			Node head_node = this.head.get(head_holder);
			int[] tail_holder = new int[1];
			Node tail_node = this.tail.get(tail_holder);
			int[] next_holder = new int[1];
			Node next_node = head.getReference().next.get(next_holder);
			if (head_node == this.head.getReference()) {
				if (head_node == tail_node) {
					if (next_node == null) { continue; }
					this.tail.compareAndSet(tail_node, next_node, tail_holder[0], tail_holder[0]+1);
				}
				else {
					val = next_node.val;
					if (this.head.compareAndSet(head_node, next_node, head_holder[0], head_holder[0]+1)) {
						break;
					}
				}
			}
		}
		return val;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		AtomicStampedReference<Node> current_node = head;
		if (head == tail) { return "Queue is empty!"; }
		int n_items = 0;
		while (current_node.getReference() != tail.getReference()) {
			current_node = current_node.getReference().next;
			sb.append(current_node.getReference().val+" ");
			n_items++;
		}
		System.out.println("number of items = " + n_items);
		return sb.toString();
	}
	
	public Queue<T> toQueue() {
		Queue<T> q = new LinkedList<T>();
		AtomicStampedReference<Node> current_node = head;
		while (current_node.getReference() != tail.getReference()) {
			current_node = current_node.getReference().next;
			q.add(current_node.getReference().val);
		}
		return q;
	}

}
