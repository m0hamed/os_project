package lounge_guests;

import lounge.Lounge;
import lounge.Table;

public class Guest extends Thread {

	public static final int HUMAN = 0;
	public static final int ALIEN = 1;
	public static final int ANDROID = 2;
	
	private int type;
	private String name;
	public int table_number = -1;
	public Table assigned_table;
	public boolean kicked_out;
	
	public Guest() {
		type = HUMAN;
		name = "";
	}

	public Guest(int type, String name) {
		super(name);
		this.type = type;
		this.name = name;
		kicked_out = false;
	}

	public int getType() {
		return type;
	}

	public String getGuestName() {
		return name;
	}
	
	public void run() {
		if(Lounge.enter(this)) {
			Lounge.leave(this);
		}
	}
}
