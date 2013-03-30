package lounge;

import java.util.ArrayList;

import lounge_guests.Guest;
import synchronisation_primitives.Barrier;
import synchronisation_primitives.Semaphore;

public class Lounge{
	private static ArrayList<Guest> guests = new ArrayList<Guest>();
	private static int lounge_guests = 0;
	private static int androids_inside = 0;
	private static int aliens_inside = 0;
	private static int humans_inside = 0;
	private static boolean lounge_closed = false;
	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore human_entering = new Semaphore(1);
	private static Semaphore android_entering = new Semaphore(1);
	private static Semaphore alien_entering = new Semaphore(1);
	private static Semaphore human_leaving = new Semaphore(1);
	private static Semaphore android_leaving = new Semaphore(1);
	private static Semaphore alien_leaving = new Semaphore(1);
	private static Semaphore guest_mutex = new Semaphore(1);
	private static Barrier lounge_door = new Barrier(3);
	
	private static final int GUEST_COUNT_MAX = 30;
	private static final int LOUNGE_SIZE = 10;
	
	//TODO print transactions happening!
	public static boolean enter(Guest g){
		
		if(lounge_guests >= 30){
			lounge_closed = true;
		}
		if(lounge_closed){
			System.out.println("Sorry! We are closed! Come back tomorrow =]");
			return false;
		}
		switch(g.getType()){
			case Guest.HUMAN: {
				human_entering.down();
				mutex.down();
				while(humans_inside >= LOUNGE_SIZE-3 || lounge_guests >= LOUNGE_SIZE || (lounge_guests >=LOUNGE_SIZE-2 && humans_inside != 0)){
					mutex.up();
					human_entering.down(); //block
					mutex.down();
				}
				if(lounge_guests >= GUEST_COUNT_MAX) {
					mutex.up();
					return false;
				}
				humans_inside++;
				lounge_guests++;
				guests.add(g);
				mutex.up();
				human_entering.up();
				break;
			}
			case Guest.ANDROID: {
				android_entering.down();
				mutex.down();
				while(androids_inside >= LOUNGE_SIZE-2 || lounge_guests >= LOUNGE_SIZE || (lounge_guests >=LOUNGE_SIZE-2 && androids_inside != 0)){
					mutex.up();
					android_entering.down();
					mutex.down();
				}
				if(lounge_guests >= GUEST_COUNT_MAX) {
					mutex.up();
					return false;
				}
				androids_inside++; 
				lounge_guests++;
				guests.add(g);
				mutex.up();
				android_entering.up();
				break;
			}
			case Guest.ALIEN: {
				alien_entering.down();
				mutex.down();
				while(aliens_inside == LOUNGE_SIZE-2 || lounge_guests >= LOUNGE_SIZE || (lounge_guests >=LOUNGE_SIZE-2 && aliens_inside != 0)){
					mutex.up();
					alien_entering.down();
					mutex.down();
				}
				if(lounge_guests >= GUEST_COUNT_MAX) {
					mutex.up();
					return false;
				}
				aliens_inside++;
				lounge_guests++;
				guests.add(g);
				mutex.up();
				alien_entering.up();
				break;
			}
			default: return false;
		}
		return true;
	}
	
	public static boolean leave(Guest g){
		if(lounge_closed){
			guest_mutex.down();
			guests.remove(g);
			guest_mutex.up();
			return false;
		}
		switch(g.getType()){
		case Guest.HUMAN: {
			human_leaving.down();
			mutex.down();
			if(humans_inside == LOUNGE_SIZE-2){
				human_entering.up();
			}
			human_leaving.up();
			break;
		}
		case Guest.ANDROID: {
			
			break;
		}
		case Guest.ALIEN: {
			
			break;
		}
		default: return false;
	}
		
		lounge_door.wait_on_barrier();
		guest_mutex.down();
		guests.remove(g);
		guest_mutex.up();
		return true;
	}
	
	public static void main(String[] args){
		//instantiate guests
		//start the threads
		//add prints in the code above
		//enjoy the simulation :P
		System.out.println("Be55");
	}
}
