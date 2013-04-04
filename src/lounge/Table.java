package lounge;

import java.util.ArrayList;

import lounge_guests.Guest;
import synchronisation_primitives.Barrier;

public class Table extends Barrier {

	private int guests_at_table;
	private ArrayList<Guest> guests;
	public boolean is_closed;

	public Table(int initial) {
		super(initial);
		guests = new ArrayList<>(initial);
		is_closed = false;
	}

	public void wait_on_barrier() {
		if (!is_closed) {
			super.wait_on_barrier();
		}
	}

	public void sit_at_table(Guest g) {
		guests.add(g);
		guests_at_table++;
	}

	public void leave_table(Guest g) {
		guests.remove(g);
	}

	public void print_occupancy() {
		String s = "";
		if (guests.size() == 1) {
			s = guests.get(0).getGuestName() + " is sitting alone";
		} else {
			for (int i = 0; i < guests.size() - 1; i++) {
				s += guests.get(i).getGuestName() + ", ";
			}
			s += guests.get(guests.size() - 1).getGuestName()
					+ " are sitting together and talking";
		}
		System.out.println(s);
	}

	public int get_guest_count() {
		return guests_at_table;
	}

	public void reset() {
		is_closed = true;
		for (int i = 0; i < guests_at_table; i++) {
			guests.get(i).kicked_out = true;
		}
		super.reset();
	}

}
