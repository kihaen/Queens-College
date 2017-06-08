import java.util.concurrent.Semaphore;

public class StudentThread extends Thread {
	
	public static long time = System.currentTimeMillis();
	public int Exam[] = new int[3];
	private static Semaphore mutex2 = new Semaphore(1);
	public static int studentN;
	StudentThread previous = null;
	
	public void msg(String m){
		System.out.println("["+(System.currentTimeMillis()-time)+"]"+getName()+": "+m);
	}
	public void takeTest(){
		try {
			sleep(500);
		} catch (InterruptedException e) {
			msg("Did not get to finish their exam");
		}
	}
	public StudentThread(String Sname, int N, StudentThread prev){
		super(Sname);
		studentN = N;
		previous = prev;
	}
	public void run(){
		// main task for students
		for(int runs=0 ; runs<3; runs++){
			try {
				sleep((int)Math.random() * 80 + 1); // Sleep for a random time to simulate coming to school
			} catch (InterruptedException e) {
				System.out.println("Error sleeping");
			} 
			try {
				MainThreadC.Classroom.acquire(); // P (CLASSROOOM) ------------------------------------------------------
			} catch (InterruptedException e1) {
				System.out.println("Something went wrong acquiring by Student");
			}
			//MainThreadC.simulateRush(this); // Establishes a priority among arrived students.
			try {
				MainThreadC.mutex.acquire();
			} catch (InterruptedException e2) { }
			MainThreadC.allowedIN--;// This means less are allowed through, because this student just got in.
			//System.out.println(MainThreadC.allowedIN);
			if(MainThreadC.allowedIN<0){
				msg("failed to take the exam");
				MainThreadC.allowedIN++;
				MainThreadC.mutex.release();
				break;
			}
			MainThreadC.mutex.release();
			try {
				MainThreadC.enterClass(this);
			} catch (InterruptedException e1) {
				//do nothing
			}
			msg("Starts taking Exam");
			takeTest();
			msg("Finishes taking Exam");
			Exam[MainThreadC.ExamCount-1]= (int )(Math.random() * 100 + 1);
			while(true){
				try {
					sleep(100); // Sleeps can be interrupted creating a easily controlled scenario to escape. 
				} catch (InterruptedException e) { break; }
			}
			try {
				MainThreadC.nextSession.acquire();
			} catch (InterruptedException e) {
				//...
			}
			//msg("Has successfull left");
		}
		try {
			sleep(100); // This is just a final sleep to keep concurrency high
		} catch (InterruptedException e) {  }
		for (int jk=0; jk<3;jk++){
			msg("got a "+Exam[jk]+" on Exam "+(jk+1));
		}
		try {
			sleep(100); // This is just a final sleep to keep concurrency high
		} catch (InterruptedException e) {  }
		MainThreadC.formGroup(this);
		if(previous!=null){
			try {
				previous.join();
			} catch (InterruptedException e) {
				System.out.println("Student join failed");
			}
		}
	}
}
