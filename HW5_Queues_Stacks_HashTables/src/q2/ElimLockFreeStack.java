package q2;

import java.util.concurrent.TimeoutException;

public class ElimLockFreeStack<T> extends SimpleLockFreeStack<T> {
	private int capacity;
	EliminationArray<T> eliminationArray;
	private ThreadLocal<RangePolicy> policy;
	
	public ElimLockFreeStack(int n_threads) {
		capacity = n_threads/2;
		eliminationArray = new EliminationArray<T>(capacity);
		policy = new ThreadLocal<RangePolicy>() {
			protected synchronized RangePolicy initialValue() {
				return new RangePolicy(capacity);
			}
		};
	}

	public void push(T x) {
		RangePolicy range_policy = policy.get();
		Node new_node = new Node(x);
		while (true) {
			if (tryPush(new_node)) {
				return;
			}
			else try {
				T other_val = eliminationArray.visit(x, range_policy.getRange());
				if (other_val == null) {
					range_policy.recordEliminationSuccess();
					return;
				}
			} catch (TimeoutException ex) {
				range_policy.recordEliminationTimeout();
			}
		}
	}

	public T pop() {
		RangePolicy range_policy = policy.get();
		while (true) {
			Node popped_node = tryPop();
			if (popped_node != null) {
				return popped_node.val;
			}
			else try {
				T other_val = eliminationArray.visit(null, range_policy.getRange());
				if (other_val != null) {
					range_policy.recordEliminationSuccess();
					return other_val;
				}
			} catch (TimeoutException ex) {
				range_policy.recordEliminationTimeout();
			}
		}
	}

}
