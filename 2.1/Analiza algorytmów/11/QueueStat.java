import java.util.*;
import java.security.*;

public class QueueStat {
	private Random r = new SecureRandom();
	private List<Integer>[] returnTimes;
	private int[] lastVisitTimes;
	private int[] numberOfVisits;
	private int size;
	private int actualStatus = 0;
	private int stepsTotal = 0;
	private double backChance;
	private double forwardChance;
	
	public QueueStat(int queueSize, double backChance, double forwardChance) {
		size = queueSize;
		returnTimes = new List[size];
		lastVisitTimes = new int[size];
		numberOfVisits = new int[size];
		
		for (int i=0;i<size;i++) {
			returnTimes[i] = new LinkedList<Integer>();
			lastVisitTimes[i] = 0;
			numberOfVisits[i] = 0;
		}
		this.backChance = backChance;
		this.forwardChance = forwardChance;
	}
	
	private void step() {
		boolean changed = false;
		double chance = r.nextDouble();
		if (actualStatus == 0) {
			if (chance < forwardChance) {
				actualStatus++;
				changed = true;
			}
		} else if (actualStatus == size-1) {
			if (chance < backChance) {
				actualStatus--;
				changed = true;
			}
		} else {
			if (chance < forwardChance) {
				actualStatus++;
				changed = true;
			} else if (chance > 1.0-backChance) {
				actualStatus--;
				changed = true;
			}
		}
		if (changed && lastVisitTimes[actualStatus] > 0) {
			returnTimes[actualStatus].add(stepsTotal-lastVisitTimes[actualStatus]);
		}
		lastVisitTimes[actualStatus] = stepsTotal;
		numberOfVisits[actualStatus]++;
		stepsTotal++;
	}
	
	public void calculate(int steps) {
		for (int i=0;i<steps;i++) step();
	}
	
	public int getTotalSteps() {
		return stepsTotal;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Queue size: ").append(size).append("\n") //
			.append("Chance of going back: ").append(backChance).append("\n") //
			.append("Chance of going forward: ").append(forwardChance).append("\n") //
			.append("Total steps: ").append(stepsTotal).append("\n") //
			.append("Actual position: ").append(actualStatus).append("\n") //
			.append("Last visits: ").append("\n");
		
		for (int i=0;i<size;i++) {
			s.append(i).append(": ").append(lastVisitTimes[i]).append("\n");
		}
		
		s.append("Number of visits: ").append("\n");
		for (int i=0;i<size;i++) {
			s.append(i).append(": ").append(numberOfVisits[i]).append("\n");
		}
		
		s.append("Mean time between visits: ").append("\n");
		for (int i=0;i<size;i++) {
			int sum = 0;
			for (Integer t : returnTimes[i]) sum += t;
			s.append(i).append(": ").append(returnTimes[i].size() > 0 ? sum/returnTimes[i].size() : -1).append("\n");
		}
		
		return s.toString();
	}
}