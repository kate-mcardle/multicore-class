package q2;

public class RangePolicy {
	private int range;
	private int capacity;
	
	public RangePolicy(int capacity) {
		range = 1;
		this.capacity = capacity;
	}
	
	public int getRange() {
		return range;
	}
	
	public void recordEliminationSuccess() {
		if (range < capacity) {
			range++;
		}
	}
	
	public void recordEliminationTimeout() {
		if (range > 1) {
			range--;
		}
	}
}
