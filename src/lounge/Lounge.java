package lounge;

import java.util.ArrayList;

import lounge_guests.Guest;
import synchronisation_primitives.Barrier;
import synchronisation_primitives.Semaphore;

public class Lounge {
	private static ArrayList<Guest> guests = new ArrayList<Guest>();
	private static int lounge_guests = 0; // currently
	private static int served_guests = 0; // all time

	private static int androids_inside = 0;
	private static int androids_waiting_to_enter = 0;
	private static int androids_next_table = 0;

	private static int aliens_inside = 0;
	private static int aliens_waiting_to_enter = 0;
	private static int aliens_next_table = 0;

	private static int humans_inside = 0;
	private static int humans_waiting_to_enter = 0;
	private static int humans_next_table = 0;

	private static int guests_waiting_to_enter = 0;

	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore guest = new Semaphore(1);
	private static Semaphore human_entering = new Semaphore(1);
	private static Semaphore android_entering = new Semaphore(1);
	private static Semaphore alien_entering = new Semaphore(1);
	private static Semaphore printing_mutex = new Semaphore(1);

	private static final int GUEST_COUNT_MAX = 30;
	private static final int LOUNGE_SIZE = 10;
	private static final int CREATED_GUESTS = 10;

	private static ArrayList<Table> tables = new ArrayList<Table>(LOUNGE_SIZE);

	// TODO print transactions happening!
	public static boolean enter(Guest g) {
		int index = 0;

		printing_mutex.down();
		System.out.println(g.getGuestName() + " is trying to enter the lounge");
		printing_mutex.up();
		mutex.down();
		if (served_guests >= GUEST_COUNT_MAX) {
			printing_mutex.down();
			System.out.println("Sorry! We are closed! Come back tomorrow =]");
			printing_mutex.up();
			mutex.up();
			return false;
		}
		if (served_guests == GUEST_COUNT_MAX - 1) {
			leave_all();
		}
		while (lounge_guests >= LOUNGE_SIZE
				&& served_guests >= GUEST_COUNT_MAX) {
			System.out.println("Too many guests in the lounge, "+g.getGuestName()+" is waiting at the door");
			guests_waiting_to_enter++;
			mutex.up();
			guest.down();
			mutex.down();
		}
		mutex.up();
		switch (g.getType()) {
		case Guest.HUMAN: {
			mutex.down();
			while ((humans_inside == LOUNGE_SIZE - 2
					|| (lounge_guests >= LOUNGE_SIZE - 2 && humans_inside != 0))
					&& served_guests < GUEST_COUNT_MAX) {
				humans_waiting_to_enter++;
				System.out.println("Too many humans "+g.getGuestName()+" is waiting");
				mutex.up();
				human_entering.down(); // block
				mutex.down();
			}
			if (served_guests >= GUEST_COUNT_MAX) {
				leave_all();
				mutex.up();
				human_entering.up();
				return false;
			}
			index = humans_next_table;
			humans_inside++;
			lounge_guests++;
			humans_next_table++;
			break;
		}
		case Guest.ANDROID: {
			mutex.down();
			while ((androids_inside >= LOUNGE_SIZE - 2 || (lounge_guests >= LOUNGE_SIZE - 2 && androids_inside != 0))
					&& served_guests >= GUEST_COUNT_MAX) {
				androids_waiting_to_enter++;
				mutex.up();
				android_entering.down();
				mutex.down();
			}
			if (served_guests >= GUEST_COUNT_MAX) {
				leave_all();
				mutex.up();
				android_entering.up();
				return false;
			}
			index = androids_next_table;
			androids_inside++;
			lounge_guests++;
			androids_next_table++;
			break;
		}
		case Guest.ALIEN: {
			while ((aliens_inside == LOUNGE_SIZE - 2
					|| (lounge_guests >= LOUNGE_SIZE - 2 && aliens_inside != 0))
					&& served_guests >= GUEST_COUNT_MAX) {
				aliens_waiting_to_enter++;
				mutex.up();
				alien_entering.down();
				mutex.down();
			}
			if (served_guests >= GUEST_COUNT_MAX) {
				leave_all();
				alien_entering.up();
				mutex.up();
				return false;
			}
			index = aliens_next_table;
			aliens_inside++;
			lounge_guests++;
			aliens_next_table++;
			break;
		}
		default:
			return false;
		}
		served_guests++;
		g.table_number = index;
		if (tables.size() <= index) {
			tables.add(new Table(3));
		}
		g.assigned_table = tables.get(index);
		tables.get(index).sit_at_table(g);
		printing_mutex.down();
		System.out.println(g.getGuestName()
				+ " has entered the lounge and is seated at table " + index);
		tables.get(index).print_occupancy();
		printing_mutex.up();
		mutex.up();
		tables.get(index).wait_on_barrier();
		return true;
	}

	public static boolean leave(Guest g) {
		printing_mutex.down();
		System.out.println(g.getGuestName() + " is trying to leave the lounge");
		printing_mutex.up();
		
		if(served_guests>= GUEST_COUNT_MAX) {
			System.out.println(g.getGuestName() + " was kicked out");
			return false;
		}
		mutex.down();
		while (guests_waiting_to_enter>0){
			guest.up();
			guests_waiting_to_enter++;
		}
		switch (g.getType()) {
		case Guest.HUMAN: {
			humans_inside--;
			lounge_guests--;
			while (humans_waiting_to_enter > 0) {
				human_entering.up(); // allow another human to enter
				humans_waiting_to_enter--;
			}
			break;
		}
		case Guest.ANDROID: {
			androids_inside--;
			lounge_guests--;
			while (androids_waiting_to_enter > 0) {
				android_entering.up();
				androids_waiting_to_enter--;
			}
			break;
		}
		case Guest.ALIEN: {
			aliens_inside--;
			lounge_guests--;
			while (aliens_waiting_to_enter > 0) {
				alien_entering.up();
				aliens_waiting_to_enter--;
			}
			break;
		}
		default:
			return false;
		}

		// if (g.assigned_table.get_guest_count() == 3) {
		// tables.remove(g.assigned_table);
		// aliens_next_table--;
		// androids_next_table--;
		// humans_next_table--;
		// tables.add(LOUNGE_SIZE - 1, new Table(3)); // add a new table at
		// // the end
		// }
		g.assigned_table.leave_table(g);
		mutex.up();
		printing_mutex.down();
		System.out.println(g.getGuestName() + " has left the lounge");
		printing_mutex.up();

		return true;
	}

	public static void leave_all() {
		if(true){
			return;
		}
		System.out.println("----------------------------------------------------------------------------");
		for(int i=0; i<tables.size();i++){
			tables.get(i).reset();
		}
		
		while (guests_waiting_to_enter>0){
			guest.up();
			guests_waiting_to_enter++;
		}
		
		while (androids_waiting_to_enter > 0) {
			android_entering.up();
			androids_waiting_to_enter--;
		}
		
		while (aliens_waiting_to_enter > 0) {
			alien_entering.up();
			aliens_waiting_to_enter--;
		}
		
		while (humans_waiting_to_enter > 0) {
			human_entering.up(); // allow another human to enter
			humans_waiting_to_enter--;
		}
		System.out.println("===================================================================");
		
	}

	public static void main(String[] args) {
		for (int i = 0; i < LOUNGE_SIZE; i++) {
			tables.add(new Table(3));
		}
		for (int i = 0; i < CREATED_GUESTS; i++) {
			new Guest(Guest.HUMAN, "Human   #" + i).start();
			new Guest(Guest.ALIEN, "Alien   #" + i).start();
			new Guest(Guest.ANDROID, "Android #" + i).start();
		}
		for (int i = CREATED_GUESTS; i < CREATED_GUESTS*2; i++) {
			//new Guest(Guest.HUMAN, "Human   #" + i).start();
			//new Guest(Guest.ALIEN, "Alien   #" + i).start();
			//new Guest(Guest.ANDROID, "Android #" + i).start();
		}
		//new Guest(Guest.ALIEN, "Alien").start();
		//new Guest(Guest.ANDROID, "Android").start();
	}

}
