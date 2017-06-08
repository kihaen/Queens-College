
public class InstructorThread extends Thread {
	
	public static long time = System.currentTimeMillis();
	private static StudentThread lastStudent = null;
	
	public void msg(String m){
		System.out.println("["+(System.currentTimeMillis()-time)+"]"+getName()+": "+m);
	}
	
	public InstructorThread(String Tname,StudentThread last){ // My constructor.. might consider changing to show actual new threads
		super(Tname);
		lastStudent = last;
	}
	public void takeTest(){
		try {
			sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void run(){
		// main task for the instructor
		for(int runs=0 ; runs<3; runs++){
			try {
				sleep((int)Math.random() * 150 + 100); // The instructor takes longer than students to arrive
			} catch (InterruptedException e) {
				System.out.println("Error sleeping");
			} // arriving to school
			
			//MainThreadC.toggleClass(); // Should set waitClass to false, letting students in. 
			msg("The Instructor has arrived and opened the class!");
			MainThreadC.enterInstr(this);
			MainThreadC.reset();
			msg("-------------------------------------------------------------------------");
			MainThreadC.InsRelease(this);
		}
		while(MainThreadC.Classroom.hasQueuedThreads()){
			MainThreadC.Classroom.release();
		}
		while(MainThreadC.mutex.hasQueuedThreads()){// note that the mutex is holding people
			MainThreadC.mutex.release();
		}
		try {
			lastStudent.join();
		} catch (InterruptedException e) {
			System.out.println("Last Student couldn't join");
		}
		
	}
}
