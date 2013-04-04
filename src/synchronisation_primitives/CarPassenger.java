package synchronisation_primitives;

/*
 * CSEN602 - Operating Systems
 * This is an implemented solution to Submission Assignment 3 part b.
 * You should be able to use this file to test the Semaphore that you implemented for the first project.
 * Replace the import line with that of the package that you created.
 * Java's Semaphore uses down and up as an analogy for down and up respectively.
 * Make sure that you replace those analogies with that of your implementation.
 */

//import java.util.concurrent.*;


public class CarPassenger {
	
	int c;
	
	Semaphore allBoarded;
	Semaphore allUnboarded;
	Semaphore boardNow;
	Semaphore unboardNow;
	Semaphore mutex;
	

	
	public CarPassenger (int c)
	{
		this.c = c;
		this.allBoarded = new Semaphore(0);
		this.allUnboarded = new Semaphore(0);
		this.boardNow = new Semaphore(0);
		this.unboardNow = new Semaphore(0);
		this.mutex = new Semaphore(1);
		
	}
	
	public void startPassenger(int i)
	{
		new Passenger(this.c, this.allBoarded, this.allUnboarded, this.boardNow, this.unboardNow, this.mutex, String.valueOf(i));
		
	}
	
	
	public void startCar()
	{
		new Car(this.c, this.allBoarded, this.allUnboarded, this.boardNow, this.unboardNow);
		
	}
	
	
	public static void main(String args[])
	{
		CarPassenger cp = new CarPassenger(5);
		cp.startCar();
		for (int i = 0; i < 10 ; i++)
			cp.startPassenger(i+1);
		
		
	}
	
}


class Car implements Runnable{

	int c;
	Semaphore allBoarded;
	Semaphore allUnboarded;
	Semaphore boardNow;
	Semaphore unboardNow;
	Thread t;
	
	public Car(int c, Semaphore allBoarded, Semaphore allUnboarded,
						Semaphore boardNow, Semaphore unboardNow )
	{
		this.c = c;
		this.allBoarded = allBoarded;
		this.allUnboarded = allUnboarded;
		this.boardNow = boardNow;
		this.unboardNow = unboardNow;
		this.t = new Thread(this);
		t.start();
	}
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true)
		{
		this.load();
		//equivalent to a for loop iterating c times with the statement boardNow.up
		for (int i= 0; i < c; i++)
		this.boardNow.up(); 
		
		
			this.allBoarded.down();
	
		this.ride();
		this.unload();
		for (int i= 0; i < c; i++)
			this.unboardNow.up();
		
		
			this.allUnboarded.down();
		
		}	
		
	}
	
	public void ride()
	{
		System.out.println("The ride is on.");
	}
	
	public void load()
	{
		System.out.println ("Starting to load passengers.");
	}
	
	public void unload()
	{
		System.out.println("Ride is over.");
	}
}

class Passenger implements Runnable{

	int c;
	Semaphore allBoarded;
	Semaphore allUnboarded;
	Semaphore boardNow;
	Semaphore unboardNow;
	Semaphore mutex;
	static int boarders = 0;
	Thread t;
	String name;
	
	public Passenger(int c, Semaphore allBoarded, Semaphore allUnboarded,
			Semaphore boardNow, Semaphore unboardNow, Semaphore mutex, String v )
	{
		this.c = c;
		this.allBoarded = allBoarded;
		this.allUnboarded = allUnboarded;
		this.boardNow = boardNow;
		this.unboardNow = unboardNow;
		this.mutex = mutex;
		this.name = v;
		this.t = new Thread(this, v);
		t.start();
}
	@Override
	public void run() {
		// TODO Auto-generated method stub
	
			this.boardNow.down();
		
		this.board();
		
			this.mutex.down();
		
			boarders++;
			if (boarders == c)
				this.allBoarded.up();
			this.mutex.up();
			this.ride();
			
				this.unboardNow.down();
			
			this.unboard();
			
				this.mutex.down();
			
			
			boarders--;
			if (boarders == 0)
				this.allUnboarded.up();
			this.mutex.up();
		
	}
	
	public void board()
	{
		System.out.println("From thread " + name + ": Whoo... Getting on the car");
	}
	
	public void ride()
	{
		System.out.println("From thread " + name + ": Woohoo... the ride rocks");
	}
	
	public void unboard()
	{
		System.out.println("From thread " + name + ": That was one cool ride");
	}
}


	
	

