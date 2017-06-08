public class ArrayQueue<AnyType> implements Queue<AnyType>
{
public static final int DEFAULT_CAPACITY = 1024;
	AnyType[]data;
	int front;
	int theSize;
	public ArrayQueue() { this(DEFAULT_CAPACITY); }
	
	public ArrayQueue(int capacity){
		front = 0;
		theSize = 0;
		data = (AnyType[]) new Object[capacity];
	}
	
	public void enqueue(AnyType a){
		data[(front+size())%data.length] = a;
		theSize++;
	}
	
	public AnyType dequeue(){
		AnyType temp = data[front];
		data[(front)%data.length] = null;
		front = (front+1)%data.length;
		theSize--;
		return temp;
	}

	public int size() {
		return theSize;
	}

	public boolean isEmpty() {
		return (theSize == 0); 
	}

	public AnyType first(){
		return data[front];
	}
}