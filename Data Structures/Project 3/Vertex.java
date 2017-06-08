
public class Vertex {
	private Object name;
	private char[] adjacent;
	
	Vertex(char a){
		name = a;
	}
	
	public String getName(){
		return toString();
	}
	
	public void setName(String a){
		name = a;
	}
	public String toString(){
		String aba = name.toString();// object toString used to get string...
		return aba;
	}
	public char[] getAdj(){
		return adjacent;
	}
	public void setAdj(char[] tempc){
		adjacent =tempc;
	}
	
}
