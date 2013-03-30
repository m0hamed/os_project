package lounge_guests;

import lounge.Lounge;

public class Guest extends Thread {
	private int type;
	private String name;
	public static final int HUMAN = 0;
	public static final int ALIEN = 1;
	public static final int ANDROID = 2;
	
	public Guest() {
		type = HUMAN;
		name = "";
	}

	public Guest(int type, String name) {
		super();
		this.type = type;
		this.name = name;
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
