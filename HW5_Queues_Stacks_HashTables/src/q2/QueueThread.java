package q2;

import java.util.Random;

public class QueueThread extends Thread {
	private volatile MyQueue<Integer> q;
	private int n_ops;
	protected int n_enq;
	protected int n_deq;
	protected long enq_time;
	protected long deq_time;
	
	public QueueThread(MyQueue<Integer> q, int n_ops) {
		this.q = q;
		this.n_ops = n_ops;
		n_enq = n_deq = 0;
		enq_time = deq_time = 0;
	}
	
	public void run() {
		Random rgen = new Random();
		for (int i = 0; i < n_ops; i++) {
			double p = Math.random();
			if (p < 0.6) {
				n_enq++;
				long t = System.currentTimeMillis();
				q.enq(rgen.nextInt(100001));
				enq_time += System.currentTimeMillis() - t;
			}
			else {
				n_deq++;
				long t = System.currentTimeMillis();
				q.deq();
				deq_time += System.currentTimeMillis() - t;
			}
		}
	}
}
