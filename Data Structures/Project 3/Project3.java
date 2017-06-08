import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

//Kihaen Baik CS313
public class Project3 {

	public static void main(String[] args) {
		try{
			FileInputStream fstream = new FileInputStream("project3.txt");
			FileInputStream quicker = new FileInputStream("project3.txt");
			Scanner scan = new Scanner(fstream);
			Scanner quickcount = new Scanner(quicker);
			int qc = 0;
			while(quickcount.hasNextLine()){ // need to know what size to initialize array
				quickcount.nextLine();
				qc++;
			}
			quickcount.close();
			int counter = 0;
			char[][] datatemp = new char[qc][qc-1];// creates a array based on the number of lines, one less column.
			while(scan.hasNextLine()){
				char character[] = scan.nextLine().toCharArray();
				int tr=0;
				for(int i=0;i<character.length;i++){
					if(character[i]!=' '){
						datatemp[counter][tr] = character[i];
						tr++;
					}
				}
				counter++;
			}
			scan.close();
			Convert aba = new Convert(datatemp);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
