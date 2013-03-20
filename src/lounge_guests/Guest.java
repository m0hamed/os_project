package lounge_guests;

public class Guest extends Thread {
	private int type;
	private static final int HUMAN = 0;
	private String name;

	public Guest() {
		super();
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
}
