import java.util.*;

public class Proc extends Thread implements Runnable {
	private boolean val = true;
	private StatusEnum status = StatusEnum.READY;
	private List<Proc> neighbours = new LinkedList<Proc>();
	private boolean ready = false;
	
	public void addNeighbour(Proc neighbour) {
		neighbours.add(neighbour);
		neighbour.addNeighbourNoRecurrence(this);
	}
	
	public void addNeighbourNoRecurrence(Proc neighbour) {
		neighbours.add(neighbour);
	}
	
	public boolean getVal() {
		return val;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void process() {
		if (val) {
			status = StatusEnum.SINGLE;
			for (Proc p : neighbours) {
				if (p.getVal()) {
					status = StatusEnum.FIGHT;
				}
			}
		} else {
			status = StatusEnum.READY;
			for (Proc p : neighbours) {
				if (p.getVal()) {
					val = false;
					status = StatusEnum.VALID;
				}
			}
		}
		if (status == StatusEnum.FIGHT) val = false;
		else if (status == StatusEnum.READY) val = true;
	}
	
	private boolean calcReadiness() {
		if (status == StatusEnum.SINGLE) return true;
		if (status == StatusEnum.VALID) {
			for (Proc p : neighbours) {
				if (p.isReady() && p.getVal()) return true;
			}
		}
		return false;
	}
	
	public void run() {
		while (!ready) {
			process();
			ready = calcReadiness();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
	}
}