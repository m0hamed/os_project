package lounge_guests;

public class Guest extends Thread {
	private int type; // 0,1,2 are Humans, Androids and Aliens Respectively
	private String name;

	public Guest() {
		super();
		type = 0;
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
