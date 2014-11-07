package q2;

import java.util.Random;
import java.util.concurrent.TimeoutException;

public class EliminationArray<T> {
	private static final int duration = 20000;
	LockFreeExchanger<T>[] exchanger;
	Random rgen;
	
	public EliminationArray(int capacity) {
		exchanger = (LockFreeExchanger<T>[]) new LockFreeExchanger[capacity];
		for (int i = 0; i < capacity; i++) {
			exchanger[i] = new LockFreeExchanger<T>();
		}
		rgen = new Random();
	}
	
	public T visit(T value, int range) throws TimeoutException {
		int slot = rgen.nextInt(range);
		return (exchanger[slot].exchange(value, duration));
	}

}
