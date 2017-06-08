
public class Container {
	private Product prod;
	private String comd;
	
	Container(Product o, String c){
		prod = o;
		comd = c;
	}
	public Product getProd(){
		return prod;
	}
	public String getcomd(){
		return comd;
	}
}
