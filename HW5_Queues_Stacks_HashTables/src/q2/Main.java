package q2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Main {

	public static void main(String[] args) {
//		evaluate_queue("lock-based");
//		System.out.println("\n");
//		evaluate_queue("lock-free");
//		System.out.println("\n");
//		debug_stack_single_thread();
		evaluate_stack("simple-lock-free");
		

	}

	public static void evaluate_queue(String type) {
		System.out.println("Evaluating " + type + " queue +++++++++++++++++++");
		int total_ops = 25000;
		int[] n_threads_arr = {2, 3, 4, 5, 6};
		for (int t = 0; t < n_threads_arr.length; t++) {
			int n_threads = n_threads_arr[t];
			System.out.println("For " + n_threads + " threads: ------------------------");
			MyQueue<Integer> q = null;
			
			if (type.equals("lock-based")) {
				q = new LockBasedQueue<Integer>();
			}
			else if (type.equals("lock-free")) {
				q = new LockFreeQueue<Integer>();
			}
			else {
				System.err.println("ERROR: no such queue implemented");
				System.exit(-1);
			}
			
			int n_enq = 0;
			int n_deq = 0;
			long enq_time = 0;
			long deq_time = 0;
			
			for (int i = 0; i < 100; i++) {
				q.enq(-i);
			}
			
			int r = total_ops % n_threads;
			QueueThread[] threads = new QueueThread[n_threads];
			for (int i = 0; i < threads.length; i++) {
				int n_ops_per_thread = total_ops/n_threads;
				if (i < r) {
					n_ops_per_thread++;
				}
				threads[i] = new QueueThread(q, n_ops_per_thread);
			}
			
			for (int i = 0; i < threads.length; i++) {
				threads[i].start();
			}
			for (int i = 0; i < threads.length; i++) {
				try {
					threads[i].join();
					n_enq += threads[i].n_enq;
					n_deq += threads[i].n_deq;
					enq_time += threads[i].enq_time;
					deq_time += threads[i].deq_time;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long q_time = enq_time + deq_time;

			double throughput = (Math.pow(10, 9))*(n_deq+n_enq)/(q_time + 0.0);
			System.out.println("Overall time taken = " + q_time + " ns");
			System.out.println("Overall throughput: " + throughput + " ops/s");			
		}
	}
	
	public static void debug_queue_single_thread() {
		MyQueue<Integer> q = new LockFreeQueue<Integer>();
		for (int i = 0; i < 10; i++) {
			q.enq(i);
		}
		System.out.println(q);

//		q.deq();
//		System.out.println(q);
//		q.deq();
//		System.out.println(q);
//		q.deq();
//		System.out.println(q);
//		q.enq(10);
//		System.out.println(q);

	}
	
	@SuppressWarnings("unused")
	private static void debug_queue_mult_threads() {
		int total_ops = 25000;
		int[] n_threads_arr = {2, 3, 4, 5, 6};
		for (int t = 0; t < n_threads_arr.length; t++) {
			int n_threads = n_threads_arr[t];
			System.out.println("For " + n_threads + " threads: ------------------------");
			MyQueue<Integer> q = new LockFreeQueue<Integer>();
			Queue<Integer> union_q = new LinkedList<Integer>();
			
			for (int i = 0; i < 100; i++) {
				q.enq(-i);
				union_q.add(-i);
			}
			
			int n_enq = 0;
			int n_deq = 0;
			long enq_time = 0;
			long deq_time = 0;
			
			int r = total_ops % n_threads;
			QueueThread_Debug[] threads = new QueueThread_Debug[n_threads];
			for (int i = 0; i < threads.length; i++) {
				int n_ops_per_thread = total_ops/n_threads;
				if (i < r) {
					n_ops_per_thread++;
				}
				threads[i] = new QueueThread_Debug(q, n_ops_per_thread, i);
			}
			
			for (int i = 0; i < threads.length; i++) {
				threads[i].start();
			}
			for (int i = 0; i < threads.length; i++) {
				try {
					threads[i].join();
					n_enq += threads[i].n_enq;
					n_deq += threads[i].n_deq;
					enq_time += threads[i].enq_time;
					deq_time += threads[i].deq_time;
					union_q.addAll(threads[i].local_q);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long q_time = enq_time + deq_time;
			
//			System.out.println(q);
//			System.out.println(union_q);
			if (union_q.containsAll(((LockFreeQueue<Integer>) q).toQueue())) {
				System.out.println("q matches union q");
			}
			else { System.out.println("bug!!! ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"); }
			System.out.println("Size of queue = " + ((LockFreeQueue<Integer>) q).toQueue().size());
			System.out.println("N dequeues = " + n_deq + "; N enqueues = " + n_enq);
			System.out.println("Total ops done = " + (n_deq + n_enq));
			double throughput = (Math.pow(10, 9))*(n_deq+n_enq)/(q_time + 0.0);
			System.out.println("Overall time taken = " + q_time + " ns");
			System.out.println("Overall throughput: " + throughput + " ops/s");			
		}
	}
	
	public static void debug_stack_single_thread() {
		MyStack<Integer> s = new SimpleLockFreeStack<Integer>();
		for (int i = 0; i < 10; i++) {
			s.push(i);
		}
		System.out.println(s);

//		Integer val = s.pop();
//		System.out.println(val);
//		System.out.println(s);
//		val = s.pop();
//		System.out.println(val);
//		System.out.println(s);
//		val = s.pop();
//		System.out.println(val);
//		System.out.println(s);
	}
	
	public static void evaluate_stack(String type) {
		System.out.println("Evaluating " + type + " stack +++++++++++++++++++");
		int total_ops = 25000;
		int[] n_threads_arr = {2, 3, 4, 5, 6};
		for (int t = 0; t < n_threads_arr.length; t++) {
			int n_threads = n_threads_arr[t];
			System.out.println("For " + n_threads + " threads: ------------------------");
			MyStack<Integer> s = null;
			// debug:
			Stack<Integer> union_s = new Stack<Integer>();
			Stack<Integer> popped_s = new Stack<Integer>();
			
			if (type.equals("lock-based")) {
				s = new LockBasedStack<Integer>();
			}
			else if (type.equals("simple-lock-free")) {
				s = new SimpleLockFreeStack<Integer>();
			}
			else {
				System.err.println("ERROR: no such queue implemented");
				System.exit(-1);
			}
			
			int n_push = 0;
			int n_pop = 0;
			long push_time = 0;
			long pop_time = 0;
			
			for (int i = 1; i < 100; i++) {
				s.push(-i);
				union_s.push(-i);
			}
			
			int r = total_ops % n_threads;
			StackThread[] threads = new StackThread[n_threads];
			for (int i = 0; i < threads.length; i++) {
				int n_ops_per_thread = total_ops/n_threads;
				if (i < r) {
					n_ops_per_thread++;
				}
				threads[i] = new StackThread(s, n_ops_per_thread, i);
			}
			
			for (int i = 0; i < threads.length; i++) {
				threads[i].start();
			}
			for (int i = 0; i < threads.length; i++) {
				try {
					threads[i].join();
					n_push += threads[i].n_push;
					n_pop += threads[i].n_pop;
					push_time += threads[i].push_time;
					pop_time += threads[i].push_time;
					// debug:
					union_s.addAll(threads[i].local_s);
					popped_s.addAll(threads[i].local_popped);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long s_time = push_time + pop_time;

			double throughput = (Math.pow(10, 9))*(n_push+n_pop)/(s_time + 0.0);
			System.out.println("Overall time taken = " + s_time + " ns");
			System.out.println("Overall throughput: " + throughput + " ops/s");
			
			// debug:
			union_s.removeAll(popped_s);
			if (union_s.containsAll(s.toStack())) {
				System.out.println("s matches union s");
			}
			else { System.out.println("bug!!! ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"); }
//			System.out.println("shared s: " + s);
//			System.out.println("union s: " + union_s);
//			System.out.println("popped s: " + popped_s);
//			System.out.println("Size of stack = " + s.toStack().size());
//			System.out.println("N pushes = " + n_push + "; N pops = " + n_pop);
		}
	}
	

}
