package q2;

// debug:
import java.util.Stack;

public class StackThread extends Thread {
	private volatile MyStack<Integer> s;
	private int n_ops;
	private int thread_no;
	protected int n_push;
	protected int n_pop;
	protected long push_time;
	protected long pop_time;
	
	// debug:
	protected Stack<Integer> local_s;
	protected Stack<Integer> local_popped;
	
	public StackThread(MyStack<Integer> s, int n_ops, int thread_no) {
		this.s = s;
		this.n_ops = n_ops;
		this.thread_no = thread_no;
		n_push = n_pop = 0;
		push_time = pop_time = 0;
		
		// debug:
		local_s = new Stack<Integer>();
		local_popped = new Stack<Integer>();
	}
	
	public void run() {
		for (int i = 0; i < n_ops; i++) {
			double p = Math.random();
			if (p < 0.6) {
				n_push++;
				// debug:
				local_s.push(thread_no*250000+i);
				// end debug
				long t = System.nanoTime();
				s.push(thread_no*250000+i);
				push_time += System.nanoTime() - t;
			}
			else {
				n_pop++;
				long t = System.nanoTime();
				Integer val = s.pop();
				pop_time += System.nanoTime() - t;
				// debug:
				local_popped.push(val);
			}
		}
	}
}
