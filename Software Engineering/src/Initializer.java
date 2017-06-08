import java.io.IOException;
import java.util.Hashtable;

public class Initializer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String Input=null; // input text file that will hold the data of inventory
		String Output=null; // some output that is necessary
		HashData Database; // pointer outside of individual scopes
			if(args.length == 2){// If we have both input and output parameters
				Input = args[0]; 
				Output = args[1];
			}
			if(args.length == 1){// If we have only input parameters, the program can still run
				Input = args[0];
			}
		try {
			if(Input!=null){
				Database = new HashData(Input);// creates my database
				GUI table = new GUI(Database); // initialize the GUI, for clearing purposes i need input name.
			}
			if(Input!=null&&Output!=null){
				Database = new HashData(Input);
				GUI table = new GUI(Database);
				Database.saveOutputText(Output); // creates a file based on output name with every action and database after transaction
			}
			else{
				Database = new HashData();
				GUI table = new GUI(Database); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
