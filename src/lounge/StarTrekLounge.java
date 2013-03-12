package lounge;

import java.util.ArrayList;

import lounge_guests.Guest;
import synchronisation_primitives.Semaphore;

public class StarTrekLounge{
	private ArrayList<Guest> guests = new ArrayList<Guest>();
	private int guest_count = 0;
	private int androids_inside = 0;
	private int aliens_inside = 0;
	private int humans_inside = 0;
	private boolean lounge_closed = false;
	private Semaphore mutex = new Semaphore(1);
	private Semaphore human = new Semaphore(0);
	private Semaphore android = new Semaphore(0);
	private Semaphore alien = new Semaphore(0);
	private Semaphore guest_mutex = new Semaphore(1);
	
	//TODO print transactions happening!
	public void enter_lounge(Guest g){
		if(guest_count == 30){
			lounge_closed = true;
		}
		if(lounge_closed){
			System.out.println("Sorry! We are closed! Come back tomorrow =]");
			return;
		}
		switch(g.getType()){
		case 0: {
			if(humans_inside == 8){
				human.down();
			}
			if(humans_inside + androids_inside + aliens_inside == 10){
				human.down();
			}
			mutex.down();
			humans_inside++;
			guests.add(g);
			mutex.up();
			break;
		}
		case 1: {
			if(androids_inside == 8){
				android.down();
			}
			if(humans_inside + androids_inside + aliens_inside == 10){
				android.down();
			}
			mutex.down();
			androids_inside++; 
			guests.add(g);
			mutex.up();
			break;
		}
		case 2: {
			if(aliens_inside == 8){
				alien.down();
			}
			if(humans_inside + androids_inside + aliens_inside == 10){
				alien.down();
			}
			mutex.down();
			aliens_inside++;
			guests.add(g);
			mutex.up();
			break;
		}
		default: return;
		}
		guest_mutex.down();
		guest_count++;
		guest_mutex.up();
	}
	
	public void leave_lounge(Guest g){
		
	}
}
