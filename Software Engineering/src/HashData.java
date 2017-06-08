import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Stack;

public class HashData {
	
	protected static Hashtable inventory;
	private static Stack Commands;
	
	public HashData(){
		inventory = new Hashtable();
		Commands = new Stack();
	}
	public HashData(String input) throws IOException{ // accepts the name of the file meant to be read
		inventory = new Hashtable(); // Initialized Hashtable inside the constructor, mainly adopted the same code from main function.
		Commands = new Stack();
		try {
			FileInputStream fstream = new FileInputStream(input);
			Scanner read = new Scanner(fstream);
			while(read.hasNextLine()){
				String currentline = read.nextLine();
				SortData(currentline,Commands);
			}
			fstream.close();
			//print();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public static int Hashline(String line){ // for the purpose of the GUI and individual line hashing and not textfile line by line
		int isbn;
		String temp = line;
		StringTokenizer seperate = new StringTokenizer(temp ,"|");
		String commd = seperate.nextToken();// all for the sole purpose of obtaining a isbn that is later used by GUI
		isbn = Integer.parseInt(seperate.nextToken());
		SortData(line,Commands);
		saveText("TraceLog"); // Saves a new text everytime hashline is called with the aggregate of all commands called.
		return isbn;
	}
	
	public static Product Query(int Key){
		Product RM = (Product) inventory.get(Key);
		return RM;
	}
	//private Hashtable inventory = new Hashtable();
	public static void delete(Product P){
		int keyfield = ((Product) P).getISBN();
		inventory.remove(keyfield);
	}
	public static void insert(Product P){
		int keyfield = ((Product) P).getISBN();
		if(Query(((Product) P).getISBN())==null){
			P.setDateEntered(currentTime());
			P.setDateLastModified(currentTime());
		}
		inventory.put(keyfield,P);
	}
	public static void modify(Product P){ // only handles total inputs, replaces 
		int keyfield = ((Product) P).getISBN();
		Object temp = inventory.get(keyfield);
		inventory.remove(temp);
		P.setDateLastModified(currentTime());
		inventory.put(keyfield,P);
	}
	public static void saveText(String outname){
		//File F = new File(System.getProperty("user.dir")+"/"+outname+".txt"); // find the file
		Stack ordered = new Stack();
		Stack Save = new Stack();
		Stack Temp = new Stack();
		while(!Commands.isEmpty()){
			Object s = Commands.pop();
			ordered.push(s); // order the stack to match the output
			Temp.push(s);
		}
		while(!Temp.isEmpty()){
			Save.push(Temp.pop());
		}
		Commands = Save; //i want to save the commands up to now, for actions used during program to be counted in procedure.
		try{
			BufferedWriter outside = new BufferedWriter(new FileWriter(outname+".txt"));// This will write a Tracelog of all actions made.
			while(!ordered.isEmpty()){
				Container cp = (Container) ordered.pop();
				outside.write(cp.getcomd() +"|");
				outside.write(cp.getProd().getISBN() + "|");
				outside.write(cp.getProd().getFirstName() + "|");
				outside.write(cp.getProd().getTitle() + "|");
				outside.write(cp.getProd().getYearPub() + "|");
				outside.write(cp.getProd().getDatePurchased() + "|");
				outside.write(cp.getProd().getDateEntered() + "|");
				outside.write(cp.getProd().getDateLastModified() + "|"); // this is mainly for backup scenario
				outside.write(currentTime() + System.lineSeparator());
			}
			outside.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void saveOutputText(String outname){
		Stack ordered = new Stack();
		Stack Save = new Stack();
		Stack Temp = new Stack();
		Enumeration id = inventory.keys();
		while(!Commands.isEmpty()){
			Object s = Commands.pop();
			ordered.push(s); // order the stack to match the output
			Temp.push(s);
		}
		while(!Temp.isEmpty()){
			Save.push(Temp.pop());
		}
		Commands = Save; //i want to save the commands up to now, for actions used during program to be counted in procedure.
		int str;
		try{
			BufferedWriter outside = new BufferedWriter(new FileWriter(outname+".txt"));// This will write a Tracelog of all actions made.
			while(!ordered.isEmpty()){
				Container cp = (Container) ordered.pop();
				outside.write(cp.getcomd() +"|");
				outside.write(cp.getProd().getISBN() + "|");
				outside.write(cp.getProd().getFirstName() + "|");
				outside.write(cp.getProd().getTitle() + "|");
				outside.write(cp.getProd().getYearPub() + "|");
				outside.write(cp.getProd().getDatePurchased() + "|");
				outside.write(cp.getProd().getDateEntered() + "|");
				outside.write(cp.getProd().getDateLastModified() + "|"); // this is mainly for backup scenario
				outside.write(currentTime() + System.lineSeparator());
				outside.write(System.lineSeparator());	
				while(id.hasMoreElements()){
			         str =  (int) id.nextElement();
			         outside.write(str + " : " + ((Product) (inventory.get(str))).getTitle());
			    } 
				outside.write(System.lineSeparator());	
			}
			outside.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public static boolean SortData(String entries,Stack S){
		String keyfield;
		StringTokenizer seperate = new StringTokenizer(entries ,"|");
		Product item = new Product(); // makes a new product for data to be stored in
		String commd = seperate.nextToken();
		String temp = seperate.nextToken();
		item.setISBN(Integer.parseInt(temp)); // each part of the tuple is inserted separately 
		try {//-------------------------------------------------------
			if(Query(item.getISBN())==null){ // null exception may be necessary, indicates that the database does not have previous entry
				GetURLInfo trier = new GetURLInfo(temp);// collect URL information into text files.
				priceBuilder(item);
				BiblioBuilder(item);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}//--------------------------------------------------------------
		if(!seperate.hasMoreTokens()){ // this is a shortcut to find
			if(commd.equalsIgnoreCase("Find")){
				Container C = new Container(item,"Find");
				S.push(C);
				Query(item.getISBN());
				return true;
			}
		}
		if(commd.equalsIgnoreCase("Delete")){ // If the case is that we want to Delete, we don't need any further information, just go ahead and delete.
			Container C = new Container(item,"Delete");
			S.push(C);
			if((Query(item.getISBN())!=null) && Query(item.getISBN()).getQ()>1){
				Query(item.getISBN()).decQ();
				return true;
			}
			delete(item);
			return true;
		}
		item.setFirstName(seperate.nextToken());
		item.setTitle(seperate.nextToken());
		item.setYearPub((seperate.nextToken()));
		item.setDatePurchased(seperate.nextToken());
		
		// At this point all of the important information directly given by user is stored, now what do we do with it is below
		//try {
			//finalParser(item); // This asks to parse-in the final implicit values obtained from the URL before we send it inside the hashtable
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		if(commd.equalsIgnoreCase("Insert")){// The case Insert is handled .. 
			Container C = new Container(item,"Insert");
			S.push(C);
			if((Query(item.getISBN())!=null) && Query(item.getISBN()).getQ()>=1){
				Query(item.getISBN()).incQ();
				return true;
			}
			insert(item);
			return true;
		}
		else if(commd.equalsIgnoreCase("Modify")){
			Container C = new Container(item,"Modify");
			S.push(C);
			modify(item);
			return true;
		}
		else if(commd.equalsIgnoreCase("Find")){
			Container C = new Container(item,"Find");
			S.push(C);
			Query(item.getISBN());
			return true;
		}
		else{
			System.out.println("Expected a Command");
			return false;
		}
	}
	public static void print(){
		Enumeration id = inventory.keys();
		int str;
		while(id.hasMoreElements()) {
	         str =  (int) id.nextElement();
	         System.out.println(str + " : " + ((Product) (inventory.get(str))).getTitle());
	      } 
	}
	public static String currentTime() {
	        Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("'recorded on: 'yyy.MM.dd 'at' HH:mm:ss z");
	        return sdf.format(cal.getTime());
	}
	public static void priceBuilder(Product modifiable) throws IOException{ // happens with only isbn inside product during hashdata call
		String currentline,input1 ="FinalPrice.txt";
		String input = input1; // Default
		FileInputStream fstream;
		try {
			String output = "";
			fstream = new FileInputStream(input);
			Scanner read = new Scanner(fstream);
			while(read.hasNextLine()){
				currentline = read.nextLine();
				StringTokenizer seperate = new StringTokenizer(currentline ,"|");
				while(seperate.hasMoreTokens()){
					String temp = seperate.nextToken();
					if(temp.equals("Kindle")){ 
						String kindleprice = seperate.nextToken();
						modifiable.setKindlePrice(kindleprice);
					}
					if(temp.equals("Hardcover")){ 
						String retailprice = seperate.nextToken();
						modifiable.setrealHPrice(retailprice);
						String usedprice = seperate.nextToken();
						modifiable.setUsedHPrice(usedprice);
						if(!seperate.nextToken().equals("Paperback")&&seperate.hasMoreElements()){
							String Collectableprice = seperate.nextToken(); // throw out value..
						}
					}
					if(temp.equals("Paperback")){
						String retailprice = seperate.nextToken();
						modifiable.setrealPPrice(retailprice);
						String usedprice = seperate.nextToken();
						modifiable.setUsedPPrice(usedprice);
						if(seperate.hasMoreElements()){
							String Collectableprice = seperate.nextToken(); // throw out value..
						}
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public static void BiblioBuilder(Product modifiable) throws IOException{ // happens with only isbn inside product during hashdata call
		String currentline,input1 ="FirstFilter.txt",input2 = "FinalBiblio.txt";
		int iteration = 0;
		String regex = "(\\([a-zA-Z\\s\\d,]+\\))"; //matches the dates within '('')'
		FileInputStream fstream,fstream2;
		try {
			String output = "";
			fstream = new FileInputStream(input1);
			Scanner read = new Scanner(fstream);
			while(read.hasNextLine()){
				currentline = read.nextLine();
				StringTokenizer seperate = new StringTokenizer(currentline ,":");
				while(seperate.hasMoreTokens()){
					iteration++;
					String currentitem = seperate.nextToken();
					if(iteration==1){
						String subitem ="<title>";
						modifiable.setTitle(currentitem.substring(subitem.length()));
					}
					if(iteration==2){
						modifiable.setFirstName(currentitem);
					}
					if(iteration>2){ //i dont need anymore information out of this textfile..
						break;
					}
				}
			}
			fstream.close();
			fstream2 = new FileInputStream(input2); // work on the second part of the bibliography
			Scanner read2 = new Scanner(fstream2);
			while(read2.hasNextLine()){
				currentline = read2.nextLine();
				Matcher M = Pattern.compile(regex).matcher(currentline);
				if(M.group(1)!=null){
					modifiable.setYearPub(M.group(1).substring(1,M.group(1).length()-1)); // getting rid of the parenthesis matched.
				}
				StringTokenizer seperate = new StringTokenizer(currentline ,";");
				while(seperate.hasMoreTokens()){
					String subitem2 = "<li><b>Publisher:</b> "; 
					String lazyfind = seperate.nextToken();
					modifiable.setPublisher(lazyfind.substring(subitem2.length()));
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}



