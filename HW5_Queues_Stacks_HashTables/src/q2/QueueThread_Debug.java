package q2;

import java.util.LinkedList;
import java.util.Queue;

public class QueueThread_Debug extends Thread {
	private volatile MyQueue<Integer> q;
	private int n_ops;
	private int thread_no;
	protected int n_enq;
	protected int n_deq;
	protected long enq_time;
	protected long deq_time;
	protected Queue<Integer> local_q;
	
	public QueueThread_Debug(MyQueue<Integer> q, int n_ops, int thread_no) {
		this.q = q;
		this.n_ops = n_ops;
		this.thread_no = thread_no;
		n_enq = n_deq = 0;
		enq_time = deq_time = 0;
		local_q = new LinkedList<Integer>();
	}
	
	public void run() {
		for (int i = 0; i < n_ops; i++) {
			double p = Math.random();
			if (p < 0.6) {
				n_enq++;
				local_q.add(thread_no*25000+i);
				long t = System.nanoTime();
				q.enq(thread_no*25000+i);
				enq_time += System.nanoTime() - t;
			}
			else {
				n_deq++;
				long t = System.nanoTime();
				q.deq();
				deq_time += System.nanoTime() - t;
			}
		}
	}
}
