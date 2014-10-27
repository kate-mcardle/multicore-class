package q3;

// TODO 
// Use synchronized to protect count
public class SynchronizedCounter extends Counter {
    @Override
    public void increment() {
    	synchronized(this) {
    		count = count + 1;
    	}
    }

    @Override 
    public int getCount() {
        return count;
    }
}
