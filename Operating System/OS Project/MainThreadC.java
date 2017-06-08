import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Semaphore;

//Kihaen Baik

public class MainThreadC {
	
	private static int NumberStudent = 14; // Expected Threads
	private static int ClassCapacity = 8;
	private static int Group_Size = 3;
	private static Semaphore group = new Semaphore(0);
	public static Semaphore mutex =  new Semaphore(1); // Binary Semaphore without queue. 
	public static Semaphore Classroom = new Semaphore(0,true); // Change to Counting semaphore with Queue.
	public static Semaphore nextSession = new Semaphore(0);
	public static ArrayDeque<StudentThread> ClassOrder = new ArrayDeque<StudentThread>();
	public static long time;
	public static volatile int allowedIN = 0; //this counter should never exceed the class capacity. 
	public static int classCount = 0; //necessary for grouping???
	public static volatile int ExamCount = 0;
	public static int groupsize = 0;
	private static int groupc = 0;
	private static InstructorThread inst;
	private static StudentThread remember;// This is the last student to be made, everyone remembers one previous student to join
	
	private static void instThr(){
		// start all desired threads...
		remember = new StudentThread("Student1",1,null);
		remember.start();
		for(int inis = 2; inis <= NumberStudent; inis++){ 
			remember = new StudentThread("Student"+inis,inis,remember);
			remember.start();
		}
		inst = new InstructorThread("Instructor",remember);
		inst.start();
	}
	public static void reset(){
		ClassOrder = new ArrayDeque<StudentThread>();
		classCount=0;
	}
	public static boolean enterInstr(InstructorThread Instru){
		//unlock the instructor lock 10 times
		try {
			mutex.acquire();
		} catch (InterruptedException e) { }
		ExamCount++;
		for(int i=0; i<ClassCapacity; i++){
			if (allowedIN >= ClassCapacity){break;} // i do not want want more than 10 people allowed in the class.
			Classroom.release(); // allow up to 10 students to enter the classroom. S(CLASSROOM)
			allowedIN++; // i need to make sure that even if i release 10 times, this does not affect the following exam allowing more than 10 students because the exam started before 10 students could enter.
		}
		mutex.release();
		Instru.msg("The Instructor entered the class");
		Instru.msg("Instructor just started exam");
		Instru.takeTest(); // Instructor determines when the exam starts!!
		Instru.msg("Instructor just finished exam");
		for(int i =0; i<classCount; i++){
			StudentThread temp = ClassOrder.removeFirst();
			Instru.msg(temp.getName() +" is about to be allowed to leave the exam.");
			temp.interrupt(); // wake the student if he is still taking exam
		}
		return true;
	}
	public static boolean InsRelease(InstructorThread Instructor){
		for(int j=0;j<NumberStudent;j++){
			nextSession.release();
		}
		return true;
	}
	public static boolean enterClass(StudentThread threadname) throws InterruptedException{ // This will be my critical section!!
		threadname.msg("attempts to enter the Class");
		if(classCount>ClassCapacity){ // Theoretically this should never happen
			threadname.msg("Fails to enter the Class");
			return false;
		}
		mutex.acquire();
		classCount++;
		ClassOrder.add(threadname);
		mutex.release();
		threadname.msg("has successfully entered the class");
		return true;
	}
	public static boolean formGroup(StudentThread Sname){
		try {
			mutex.acquire();
		} catch (InterruptedException e1) {
			//something
		}
		groupsize++;
		Sname.msg("Enters the group");
		if(groupsize%Group_Size==0||groupsize==NumberStudent){
			for(int i=0;i<Group_Size;i++){
				group.release();
			}
			Sname.msg("leaves the group");
			mutex.release();
		}
		else{
			try {
				mutex.release();
				group.acquire();
				Sname.msg("leaves the group");
			} catch (InterruptedException e) {
				// something
			}
		}
		return true;
	}
	public static void main(String[] args) {
		time = System.currentTimeMillis();
		//Obtain the number of Students and Capacity from console
		if(args.length > 0){ // condition must be double checked for errors
			NumberStudent = Integer.parseInt(args[0]); // to do ... surround with try and catch for exceptions..
			ClassCapacity = Integer.parseInt(args[1]);
		}
		instThr(); // Instantiate the threads
		try {
			inst.join();
		} catch (InterruptedException e) {
			System.out.println("Instructor join failed");
		}
	}

}
