
public interface Queue<AnyType> {
	int size();
	//returns the number of data values in the queue.
	
	boolean isEmpty();
	//returns a boolean indicating whether the queue is empty.
	
	void enqueue(AnyType newValue);
	//insets a data value to the back of the queue.
	
	AnyType first();
	//returns the data value at the front of the queue without removing it.
	
	AnyType dequeue();
	//remove 1 data value from the front of the queue.
}
