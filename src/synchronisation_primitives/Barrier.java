package synchronisation_primitives;

public class Barrier {
	private int count;
	
	public Barrier(int initial) {
		count = initial;
	}
	
	public synchronized void wait_on_barrier() {
		count --;
		if(count==0){
			this.notifyAll();
		} else {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
