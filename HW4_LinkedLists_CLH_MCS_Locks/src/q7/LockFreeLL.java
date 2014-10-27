package q7;

/**
 Based on implementation provided in Herlihy's Art of Multiprocessor Programming
 */

//import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeLL extends MyLinkedList {
	private Node sentinel;
	
	public LockFreeLL() {
		sentinel = new Node(Integer.MIN_VALUE);
		Node tail = new Node(Integer.MAX_VALUE);
		while (!sentinel.next.compareAndSet(null, tail, false, false));
	}
	
	public class Node {
		private Integer val;
		private AtomicMarkableReference<Node> next;
		
		public Node(Integer val) {
			this.val = val;
			next = new AtomicMarkableReference<Node>(null, false);
		}
	}
	
	public class Window {
		public Node prev, curr;
		public Window(Node myPrev, Node myCurr) {
			prev = myPrev;
			curr = myCurr;
		}
	}
	
	public Window find(Node head, Integer val) {
		Node prev = null, curr = null, next = null;
		boolean[] marked = {false};
		boolean toBeDeleted;
		retry: while (true) {
			prev = head;
			curr = prev.next.getReference();
			while (true) {
				next = curr.next.get(marked);
				while (marked[0]) {
					toBeDeleted = prev.next.compareAndSet(curr, next, false, false);
					if (!toBeDeleted) continue retry;
					curr = prev.next.getReference();
					next = curr.next.get(marked);
				}
				if (curr.val >= val) {
					return new Window(prev, curr);
				}
				prev = curr;
				curr = next;
			}
		}
	}
	
	public boolean add(Integer x) {
//		long t = System.currentTimeMillis();
		while (true) {
			Window window = find(sentinel, x);
			if (window.curr.val.equals(x)) {
//				t = System.currentTimeMillis() - t;
//				add_time.getAndAdd(t);
				return false;				
			}
			else {
				Node new_node = new Node(x);
				new_node.next = new AtomicMarkableReference(window.curr, false);
				if (window.prev.next.compareAndSet(window.curr, new_node, false, false)) {
//					t = System.currentTimeMillis() - t;
//					add_time.getAndAdd(t);
					return true;
				}
			}
		}
	}
	
	public boolean remove(Integer x) {
//		long t = System.currentTimeMillis();
		boolean markedAsDeleted = false;
		while (true) {
			Window window = find(sentinel, x);
			if (window.curr.val != x) {
//				t = System.currentTimeMillis() - t;
//				remove_time.getAndAdd(t);
				return false;
			}
			else {
				Node next = window.curr.next.getReference();
				markedAsDeleted = window.curr.next.attemptMark(next, true);
				if (!markedAsDeleted) { continue; }
				window.prev.next.compareAndSet(window.curr, next, false, false);
//				t = System.currentTimeMillis() - t;
//				remove_time.getAndAdd(t);
				return true;
			}
		}
	}
	
	public boolean contains(Integer x) {
//		long t = System.currentTimeMillis();
		Window window = find(sentinel, x);
		if (window.curr.val.equals(x)) {
//			t = System.currentTimeMillis() - t;
//			contains_time.getAndAdd(t);
			return true;
		}
		else {
//			t = System.currentTimeMillis() - t;
//			contains_time.getAndAdd(t);
			return false;
		}
	}

}
