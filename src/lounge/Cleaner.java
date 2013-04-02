package lounge;

import synchronisation_primitives.Semaphore;

public class Cleaner extends Thread {
	
	public Semaphore changed;
	public boolean has_run;

	public Cleaner() {
		super("Cleaner");
		changed = new Semaphore(0);
		has_run= false;
	}
	
	public void run(){
		has_run=true;
		while(true){
			changed.down();
			Lounge.mutex.down();
			if(Math.min(Lounge.aliens_inside, Math.min(Lounge.humans_inside, Lounge.androids_inside)) == 0 && Lounge.has_finished()) {
				Lounge.leave_all();
				Lounge.mutex.up();
				break;
			}
			Lounge.mutex.up();
		}
	}

}
