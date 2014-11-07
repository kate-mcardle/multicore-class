package q2;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeExchanger<T> {
	static final int EMPTY = 1, WAITING = 2, BUSY = 3;
	AtomicStampedReference<T> slot = new AtomicStampedReference<T>(null, 0);
	
	public T exchange(T my_item, long nanos) throws TimeoutException {
		long time_bound = System.nanoTime() + nanos;
		int[] stamp_holder = {EMPTY};
		while (true) {
			if (System.nanoTime() > time_bound) {
				throw new TimeoutException();
			}
			T other_item = slot.get(stamp_holder);
			int stamp = stamp_holder[0];
			switch(stamp) {
			case EMPTY:
				if (slot.compareAndSet(other_item, my_item, EMPTY, WAITING)) {
					while (System.nanoTime() < time_bound) {
						other_item = slot.get(stamp_holder);
						if (stamp_holder[0] == BUSY) {
							slot.set(null, EMPTY);
							return other_item;
						}
					}
					if (slot.compareAndSet(my_item, null, WAITING, EMPTY)) {
						throw new TimeoutException();
					} else {
						other_item = slot.get(stamp_holder);
						slot.set(null,  EMPTY);
						return other_item;
					}
				}
				break;
			case WAITING:
				if (slot.compareAndSet(other_item, my_item, WAITING, BUSY)) {
					return other_item;
				}
				break;
			case BUSY:
				break;
			default:
				break;
			}
		}
		
	}

}
