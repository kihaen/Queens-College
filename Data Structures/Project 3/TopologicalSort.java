
public class TopologicalSort<AnyType> {
	
	private ArrayQueue ToDo; // i used a queue to organize the directed graph, it would load vertex's to the sorted list
	private Vertex[] OVertex;
	private int avc = 0;

	
	TopologicalSort(char a[][]){ //accepts a adjacency matrix.
		errorCheck(a);// checks for loops
		ArrayQueue ToDo = new ArrayQueue(a.length);
		Vertex[] OVertex = new Vertex[a.length-1]; // the whole for loop below is to find starting points for the sort.
		for(int i=0;i<a.length-1;i++){ // because length would be a bad index for a array
			for(int j=1;j<a.length;j++){// it is length-1 because of the first column which is the variable names
				//System.out.println(j + " " + i); debugging 
				if(a[j][i] == '1'){
					break; // checking the columns for variables that are directed to the row
				}
				if( j==a.length-1 && a[j][i]!='1'){ // if we are checking the last number and it is not '1' then there are no adjacent to this variable.
					Vertex insert = new Vertex(a[0][i]); // create a vertex with the name.
					char[] adj = new char[a.length-1];
					for(int k=0;k<a.length-1;k++){
						adj[k] = a[i+1][k]; // the column and row difference is by 1, copying over adjacent to data.
					}
					insert.setAdj(adj); //sets the data as the vertex's data
					ToDo.enqueue(insert); // insert that vertex into a queue
				}
			}
		}
		avc = 0; // inner array vertex counter
		while(true){
			if(ToDo.isEmpty()){
				System.out.println("No starting varibles");
				break;
			}
			Vertex temp = (Vertex) ToDo.dequeue();
			if(doesExisting(temp,OVertex)){// check the ArrayVertex to see if it is already in the arrayVertex and removes from previous.
				avc--;
			}
			if(avc == a.length-1 && ToDo.isEmpty()){
				break;
			}
			 OVertex[avc]=temp;// adds it to the officially organized array
			 //System.out.println(temp);
			 avc++; // keeps track of the # of elements in OVertex.
			char[] qArray = temp.getAdj();
			for(int i=0;i < qArray.length;i++){
				if(qArray[i] == '1'){
					Vertex init = new Vertex(a[0][i]);
					char[] adj = new char[qArray.length];
					for(int k=0;k<qArray.length;k++){
						adj[k] = a[i+1][k]; // the column and row difference is by 1, copying over adjacent to data.
					}
					init.setAdj(adj);
					ToDo.enqueue(init); // adds adjacent to variables, to the queue.
				}
			}
		}
		printOrdered(OVertex,a);
		
	}
	public boolean doesExisting(Vertex t,Vertex[] ref){// the point of this method is to check if the vertex already exists on the list.
		if( avc == 0){
			return false;
		}
		for(int i=0;i<avc;i++){
			//System.out.println(avc +" "+ i); //debug
			if( t == ref[i]){// if the vertex is already on the list, remove it from where it is.
				for(int j=i;j<ref.length-1;j++){
					ref[j]= ref[j+1]; //shifts everything one left
				}
				avc--;
				return true;
			}
		}
		return false;
	}
	public void check(char a[][]){ //used as a debug tool to see if array was consistent with data
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a.length-1;j++){
				System.out.println(a[i][j]);
			}
		}
	}
	private void printOrdered(Vertex[]o,char[][]a){ // simply prints the vertex is string with relevant data.
		//String[] b = new String[o.length];
		 System.out.println("Sorted Configuration");
		String holder ="";
		for(int i=0;i<o.length;i++){
			holder = binarytoString(o[i],a);
			System.out.println(o[i].getName()+" : "+holder);
		}
	}
	private String binarytoString(Vertex b,char[][]a){ // same method i used for covert.
		String temp ="";
		char[] data = b.getAdj();
		for(int i=0;i<avc;i++ ){
			if(data[i] =='1'){
				temp += a[0][i];
				temp+= " ";
			}
		}
		return temp;
	}
	private void errorCheck(char[][]a){ //in order for this topological sort to end there must be a end point.
		char b=' ';
		boolean c = false;
		for(int i =1;i<a.length;i++){
			int count =0;
			for(int j =0;j<a.length-1;j++){
				b = a[i][j];
				if(b=='0'){
					count++;
				}
				if(count == a.length-1){c = true;}// it counts the number of 0's necessary to be a endpoint, if one is found we can exit
			}
		}
		if(c == false){ throw new IllegalArgumentException("Infinite loop detected");}
	}
}
	

