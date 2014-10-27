package q7;

import java.util.concurrent.atomic.AtomicLong;

public abstract class MyLinkedList {
	public AtomicLong add_time = new AtomicLong();
	public AtomicLong remove_time = new AtomicLong();
	public AtomicLong contains_time = new AtomicLong();
	
	public abstract boolean add(Integer x);
	public abstract boolean remove(Integer x);
	public abstract boolean contains(Integer x);
}