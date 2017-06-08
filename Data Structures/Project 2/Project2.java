import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Project2 {

	public static void main(String[] args) {
		try {
			FileInputStream fstream = new FileInputStream("project2.txt");
			Scanner read = new Scanner(fstream);//scanner allows us to know when the final line has been read
			DynamicArrayStack stack = new DynamicArrayStack(25);
			while(read.hasNextLine()){ // while the txt file has more lines to read it will loop
				char character[] = read.nextLine().toCharArray(); // turns the line into a character array, which can be easily interpreted.
				for(int i=0;i<character.length;i++){
					System.out.println(character[i]);
					if(character[i]=='{'){ // The stack pushes the left curly bracket if it sees a left bracket.
						stack.push('{');
					}
					if(character[i]=='('){ // the stack pushes the left parenthesis if it sees a left parenthesis
						stack.push('(');
					}
					if(character[i]=='['){// the stack pushes the left brace bracket if it sees a left brace bracket
						stack.push('[');
					}
					if(character[i]=='}'){
						if(stack.isEmpty()){
							throw new IllegalArgumentException(" unbalanced bracket, right heavy and item is not well-balanced");
						}
						if((char)stack.top()!='{'){
							throw new IllegalArgumentException(" mismatched curly bracket with "+(char)stack.top());
						}
						stack.pop();
					}
					if(character[i]==']'){// it is easier to expect a character to implement many if case scenarios
						if(stack.isEmpty()){ // if the stack is empty then we cannot pop anything, and a error has been made on the input
							throw new IllegalArgumentException(" unbalanced bracket, right heavy");
						}
						if((char)stack.top()!='['){ // anything other than the expected type of bracket is a mismatch
							throw new IllegalArgumentException(" mismatched brace bracket with "+(char)stack.top());
						}
						stack.pop();// the stack is not empty and it is safe to pop
					}
					if(character[i]==')'){
						if(stack.isEmpty()){
							throw new IllegalArgumentException(" unbalanced bracket, right heavy");
						}
						if((char)stack.top()!='('){
							throw new IllegalArgumentException(" mismatched parenthesis with "+(char)stack.top());
						}
						stack.pop();
					}
				}
				if(!stack.isEmpty()){
					throw new IllegalArgumentException("Remaining items in stack, item is not well-balanced"); // at this point the loop is done with every line, and we check for remaining items in stack
				}
				else{
					System.out.println("Successful Completion!");// otherwise if stack is empty, we have successfully completed without errors
				}
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File cannot be found, please check again");// if file cannot be found, tell user.
		}

	}

}
