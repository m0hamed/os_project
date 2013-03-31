package synchronisation_primitives;

public class Barrier {
	private int initial;
	private int count;
	private final int upperBound;
	
	public Barrier(int initial) {
		this.initial = initial;
		count = initial;
		upperBound = initial;
	}
	
	public synchronized void wait_on_barrier() {
		count --;
		if(count==0){
			this.notifyAll();
			count = upperBound;
		} else {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void reset() {
		count = initial;
		this.notifyAll();
	}
}
