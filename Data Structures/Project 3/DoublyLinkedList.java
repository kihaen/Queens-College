import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class DoublyLinkedList<AnyType> implements List<AnyType>
{
  private static class Node<AnyType>
  {
    private AnyType data;
    private Node<AnyType> prev;
    private Node<AnyType> next;

    public Node(AnyType d, Node<AnyType> p, Node<AnyType> n)
    {
      setData(d);
      setPrev(p);
      setNext(n);
    }

    public AnyType getData() { return data; }

    public void setData(AnyType d) { data = d; }

    public Node<AnyType> getPrev() { return prev; }

    public void setPrev(Node<AnyType> p) { prev = p; }

    public Node<AnyType> getNext() { return next; }

    public void setNext(Node<AnyType> n) { next = n; }
  }

  private int theSize;
  private int modCount;
  private Node<AnyType> header;
  private Node<AnyType> trailer;

  public DoublyLinkedList()
  {
    header = new Node<AnyType>(null, null, null);
    trailer = new Node<AnyType>(null, null, null);
    modCount = 0;
    clear();
  }

  public void clear()
  {
    header.setNext(trailer);
    trailer.setPrev(header);
    theSize = 0;
  }

  public int size()
  {
    return theSize;
  }

  public boolean isEmpty()
  {
    return (size() == 0);
  }

  public AnyType get(int index)
  {
	AnyType data = (AnyType) this.getNode(index).data;
	return data;
  }

  public AnyType set(int index, AnyType newValue)
  {
	  Node a = new Node(null,null,null);
	  int counter = 0; //does one less to place at index-1
	  a.setNext(header.getNext());// setting the data
	  while(index>counter){
		  a= a.getNext();
		  counter++;
	  }
	  if(a!=trailer){
		  a.setData(newValue);
	  }
	return null; // there isn't really a need to return anything
  }

  public boolean add(AnyType newValue)
  {
    add(size(), newValue);
    return true;
  }

  public void add(int index, AnyType newValue)
  {
	  Node before;
	  theSize++; // its important to increment the size before using get node method, as it will conflict with size to index
	  if(index == 0){// if the index we want to add is 0 then we want the header 
		  before = header;
	  }
	  else{ // otherwise we want to get the data previous to index
		  before = getNode(index-1);
	  }
	  Node after = before.getNext();// the rest of this is setting pointers for the doublylinked list
	  Node b = new Node(newValue,before,after);
	  before.setNext(b);
	  after.setPrev(b);
  }

  public AnyType remove(int index)
  {
    return remove(getNode(index));
  }

  public Iterator<AnyType> iterator()
  {
    return new LinkedListIterator();    
  }

  private Node<AnyType> getNode(int index)
  {
    return (getNode(index, 0, size()-1));
  }

  private Node<AnyType> getNode(int index, int lower, int upper)
  {
	  Node a = header.getNext();// while using a iterator may seem nice, it was often more difficult to do so.
	  if(index==0){
		  return a; // returns header because add wants index 0
	  }
	  //System.out.println(index+" "+upper);
	  if(index == -1 || lower>index || upper<index){throw new IllegalArgumentException(" bad index ");}
	  for(int i=lower; i<upper;i++){
		  if(i==index){
			  break;
		  }
		  a = a.getNext();
	  }
	  return a;
  }

  private AnyType remove(Node<AnyType> currNode)
  {
	  if(header.getNext()==trailer){
		  throw new IllegalArgumentException(" empty linkedlist ");
	  }
	  Node before = currNode.getPrev(); // the pointers are reassigned and the current node loses all of its pointers as well
	  Node After = currNode.getNext();
	  currNode.setNext(null);
	  currNode.setPrev(null);
	  before.setNext(After);
	  After.setPrev(before);
	  theSize--; // decreased the total size, so this does not conflict with future calculations of add function.
	  return null;
  }

  private class LinkedListIterator implements Iterator<AnyType>
  {
    private Node<AnyType> current;
    private int expectedModCount;
    private boolean okToRemove;

    LinkedListIterator()
    {
      current = header.getNext();
      expectedModCount = modCount;
      okToRemove = false;
    }

    public boolean hasNext()
    {
      return (current != trailer);
    }

    public AnyType next()
    {
      if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
      if (!hasNext())
        throw new NoSuchElementException();

      AnyType nextValue = current.getData();
      current = current.getNext();
      okToRemove = true;
      return nextValue;
    }

    public void remove()
    {
      if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
      if (!okToRemove)
        throw new IllegalStateException();

      DoublyLinkedList.this.remove(current.getPrev());
      expectedModCount++;
      okToRemove = false;
    }
  }
}