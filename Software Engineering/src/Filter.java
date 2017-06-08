import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter {
	
	
	public Filter(){
	}
	public static boolean biblioNarrowFilter() throws IOException{ // filters to find title and author
		String input = "savedSearches.txt";
		String currentline;
		String regex = "\\<(title)\\>";
		boolean write = false;
		//Pattern P = Pattern.compile(regex);
		FileInputStream fstream;
		try {
			BufferedWriter outside = new BufferedWriter(new FileWriter("FirstFilter.txt"));
			fstream = new FileInputStream(input);
			Scanner read = new Scanner(fstream);
			while(read.hasNextLine()){
				currentline = read.nextLine();
				Matcher M = Pattern.compile(regex).matcher(currentline);
				if(M.find()){
					if(M.group(1)!=null){
						//System.out.println(M.group(1));//debug,,,
						if(M.group(1).equals("title")){
							outside.write(currentline + System.lineSeparator());
							//fstream.close();
							//outside.close();
							//return true;
						}
					}
				}
			}
			fstream.close();
			outside.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	return false;
	}
	public static boolean biblioSecondFilter() throws IOException{
		String input = "savedSearches.txt";
		String currentline;
		String regex1 = "(\\<(h2)\\>(Product)\\s(Details))";
		String regex2 = "(\\<(script)\\s(type)\\=\\'(text)\\/(javascript))";
		boolean write = false;
		//Pattern P = Pattern.compile(regex);
		FileInputStream fstream;
		try {
			BufferedWriter outside = new BufferedWriter(new FileWriter("BiblioFilter.txt")); 
			fstream = new FileInputStream(input);
			Scanner read = new Scanner(fstream);
			while(read.hasNextLine()){
				currentline = read.nextLine();
				Matcher M = Pattern.compile(regex1).matcher(currentline);
				if(M.find()&&write==false){
					//System.out.println(M.group(1)+" Found!! ");
					write = true;
				}
				if(write == true){
					outside.write(currentline + System.lineSeparator());
				}
				M = Pattern.compile(regex2).matcher(currentline);
				if(M.find()&&write==true){
					//System.out.println(M.group(1));
					outside.close();
					fstream.close();
					return true;
				}
				
			}
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		}
		return false;
	}
	public static boolean biblioFinalFilter() throws IOException{// ---------continue //  year publisher
		String input = "BiblioFilter.txt";
		String currentline;
		String regex = "((\\<(li)\\>\\<(b)\\>(Publisher:)\\<\\/(b)\\>))";
		boolean write = false;
		//Pattern P = Pattern.compile(regex);
		FileInputStream fstream;
		try {
			BufferedWriter outside = new BufferedWriter(new FileWriter("FinalBiblio.txt"));
			fstream = new FileInputStream(input);
			Scanner read = new Scanner(fstream);
			while(read.hasNextLine()){
				currentline = read.nextLine();
				Matcher M = Pattern.compile(regex).matcher(currentline);
				if(M.find()){
					if(M.group(1)!=null){
						//System.out.println(M.group(1));//debug,,,
						if(M.group(1).equals("title")){
							outside.write(currentline + System.lineSeparator());
						}
					}
				}
			}
			fstream.close();
			outside.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	return false;
	}
	public static boolean PriceNarrowFilter() throws IOException{
		String input = "savedSearches.txt";
		String currentline;
		String regex1 = "(\\<(ul)\\s(class)\\=\\\"(a-nostyle)\\s(a-button-list)\\s(a-horizontal)\\\"\\>)";
		String regex2 = "(\\<(div)\\s(id)\\=\\\"(extraProductInfoFeatureGroup))";
		boolean write = false;
		//Pattern P = Pattern.compile(regex);
		FileInputStream fstream;
		try {
			BufferedWriter outside = new BufferedWriter(new FileWriter("PriceFilter.txt")); 
			fstream = new FileInputStream(input);
			Scanner read = new Scanner(fstream);
			while(read.hasNextLine()){
				currentline = read.nextLine();
				Matcher M = Pattern.compile(regex1).matcher(currentline);
				if(M.find()&&write==false){
				//System.out.println(M.group(1)+" Found!! ");
					write = true;
				}
				if(write == true){
					outside.write(currentline + System.lineSeparator());
				}
				M = Pattern.compile(regex2).matcher(currentline);
				if(M.find()&&write==true){
					//System.out.println(M.group(1));
					outside.close();
					fstream.close();
					return true;
				}
			
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean PriceSecondFilter() throws IOException{ // needs changing to handle the new URL expected..
		String input = "PriceFilter.txt";
		String currentline;
		boolean write = false;
		int i =0;
		//Pattern P = Pattern.compile(regex);
		FileInputStream fstream;
		try {
			BufferedWriter outside = new BufferedWriter(new FileWriter("FinalPrice.txt"));
			fstream = new FileInputStream(input);
			Scanner read = new Scanner(fstream);
			while(read.hasNextLine()){
				currentline = read.nextLine();
				Matcher O = Pattern.compile("(\\<(span)\\>(Kindle))").matcher(currentline); // This is for Kindle
				if(O.find()){// Subgroup search for Kindle
					outside.write("Kindle"+"|");
				}
				Matcher N = Pattern.compile("(Hardcover)").matcher(currentline); // This is for Hardcover
				if(N.find()){// Subgroup search for Hardcover
					outside.write(N.group(1)+"|");
				}
				Matcher M = Pattern.compile("(Paperback)").matcher(currentline); // This is for paper back
				if(M.find()){// Subgroup search for Paperback
					outside.write(M.group(1)+"|");
				}
				Matcher Used = Pattern.compile("((Used)\\s\\<(span)\\s(class))").matcher(currentline); // This is for used
				if(N.find()){// Subgroup search for Used
					outside.write(N.group(1)+"|");
				}
				Matcher New = Pattern.compile("((New)\\s\\<(span)\\s(class))").matcher(currentline); // This is for new
				if(N.find()){// Subgroup search for New
					outside.write(N.group(1)+"|");
				}
				Matcher Collect = Pattern.compile("((Collectible)\\s\\<(span)\\s(class))").matcher(currentline); // This is for collectible
				if(N.find()){// Subgroup search for Collectible
					outside.write(N.group(1)+"|");
				}
				Matcher amazonpriceK = Pattern.compile("([$]([1-9]+\\.?\\d*))").matcher(currentline);// amazonprice for kindle
				if(amazonpriceK.find()){
					i++;
					if(i==1){
						//System.out.println(amazonpriceK.group(1) + " 1st value found");// kindle | hardcover | paperback
						outside.write(amazonpriceK.group(1)+"|");
			
					}
					if(i==2){
						//System.out.println(amazonpriceK.group(1) + " 2nd value found");// hardcover | paperback | paperback used
						outside.write(amazonpriceK.group(1)+"|");
					}
					if(i==3){
						//System.out.println(amazonpriceK.group(1) + " 3rd value found");// hardcover used? | paperback | paperback new | paperback used
						outside.write(amazonpriceK.group(1)+"|");
					}
					if(i==4){
						//System.out.println(amazonpriceK.group(1) + " 4th value found");// hard cover new? | paperback used | paperback new
						outside.write(amazonpriceK.group(1)+"|");
					}
					if(i==5){
						//System.out.println(amazonpriceK.group(1) + " 5th value found");// paperback | paperback new 
						outside.write(amazonpriceK.group(1)+"|");
					}
					if(i==6){
						//System.out.println(amazonpriceK.group(1) + " 6th value found");// paperback used
						outside.write(amazonpriceK.group(1)+"|");
					}
					if(i==7){
						//System.out.println(amazonpriceK.group(1) + " 7th value found");// paperback new
						outside.write(amazonpriceK.group(1)+"|");
					}
					if(i==8){
						//System.out.println(amazonpriceK.group(1) + " 8th value found");// paperback new
						outside.write(amazonpriceK.group(1)+"|");
					}
					//a = true;
				}
			}
			fstream.close();
			outside.close();
		} catch (FileNotFoundException h) {
			h.printStackTrace();
		}
	return false;
	}
}