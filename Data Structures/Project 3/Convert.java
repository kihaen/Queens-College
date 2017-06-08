import java.util.Iterator;

public class Convert {
	
	 private int size;
	 private DoublyLinkedList templ = new DoublyLinkedList();
	 private DoublyLinkedList all;
	 
	 Convert(char a[][]){
		 DoublyLinkedList all = new DoublyLinkedList();
		 size = 0;
		 for(int i=0;i<a.length-1;i++){ // this loop creates vertex assigns them names and data relevant
			 Vertex temp = new Vertex(a[0][i]);// first create the vertex
			 char[] holder = new char[a.length-1];// create a temporary storage for my relevant data
			 for(int j=0;j<a.length-1;j++){// using a loop store data on the row for directed to adjacency list
				 holder[j] = a[i+1][j];	// number offsets to account for difference in rows		 
			 }
			 temp.setAdj(holder);// assigning the vertex its relevant data
			 all.add(temp);// adding the vertex to a linked list
			 size++;// im manually keeping track of the list.
		 }
		 print(a,all);
		 TopologicalSort collection = new TopologicalSort(a);// creates a constructor that handles the whole topological sort
	 }
	 private void print(char[][] ar,DoublyLinkedList as){ // simple purpose is to print, however interpreting the data is tough.
		 System.out.println("Unsorted Configuration");
		 Vertex holder;
		 int index = 0;
		 String name;
		 String data ="";
		 while(index<ar.length-1){
			 holder = (Vertex) as.get(index);
			 name = holder.getName(); // calls to get the name
			 data = binarytoString(holder,ar);
			 System.out.println(holder+" : "+data);
			 index++;
		 }
		 System.out.println("---------------------");
	 }
	 private String binarytoString(Vertex b,char[][]a){ // this method is used to turn the relevant character data to the desired string
			String temp ="";
			char[] data = b.getAdj();
			for(int i=0;i<size;i++ ){
				if(data[i] =='1'){
					temp += a[0][i];
					temp+= " ";
			}
		}
		return temp;
	}
	 

}
