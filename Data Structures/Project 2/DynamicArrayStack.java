public class DynamicArrayStack<AnyType> implements Stack<AnyType>
{
  public static final int DEFAULT_CAPACITY = 1024;
  AnyType[] data;
  int topOfStack;

  public DynamicArrayStack() { this(DEFAULT_CAPACITY); }

  public DynamicArrayStack(int capacity)
  {
    topOfStack = -1;
    data = (AnyType[]) new Object[capacity];
  }

  public int size()
  {
	  return topOfStack+1;
  }

  public boolean isEmpty()
  {
	  return size()<1;// boolean statement only needs boolean operator
  }

  public void push(AnyType newValue)
  {
	  if(data.length-1==topOfStack){// The stack is already full, then we need to resize!
		  Object[] temp = new Object[(data.length)*2];
		  for(int i=0;i<=topOfStack;i++){
			  temp[i] = data[i];
		  }
		data = (AnyType[]) temp;  //reassignment of pointers so that data will point to temp.
	  }
	  topOfStack++;// the stack pointer increases to new position
	  data[topOfStack] = newValue;//new position is given a new value
  }

  public AnyType top()
  {
	  return data[topOfStack];
  }

  public AnyType pop()
  {
	 if(topOfStack<(data.length/4)){// if the top of the stack is less than 1/4 of the total array size than we want to shrink the array and open up space
		 Object[] tempa = new Object[(data.length)/2];// the new array will be half the size of the original, there should be no conflict with top of stack either.
		  for(int i=0;i<=topOfStack;i++){// stack is safely within the boundaries of the array
			  tempa[i] = data[i];
		  }
		data = (AnyType[]) tempa; 
	 }
	AnyType temp = data[topOfStack];
	data[topOfStack]=null;//current pointer is set to null
	topOfStack--;//stack pointer decreases to point to new position
	return temp;
  }
}