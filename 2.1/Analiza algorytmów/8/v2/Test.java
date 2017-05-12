import java.util.*;

public class Test {
	private static boolean debug = false;
	private static boolean printRes = false;
	
	private static List<int[]> makeGrid(int n) {
		List<int[]> pairs = new LinkedList<int[]>();
		for (int j=0;j<n;j++) {
			for (int i=0;i<n;i++) {
				if (i>0) pairs.add(new int[]{j*n+i, j*n+i-1});
				if (i<n-1) pairs.add(new int[]{j*n+i, j*n+i+1});
				if (j>0) pairs.add(new int[]{j*n+i, j*n+i-n});
				if (j<n-1) pairs.add(new int[]{j*n+i, j*n+i+n});
			}
		}
		return pairs;
	}
	
	private static String procToStr(Proc[] proc, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<n;i++) {
			for (int j=0;j<n;j++) {
				sb.append((proc[j*n+i].getVal() ? '#' : ' '));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		if (args.length > 1) {
			if (args.length > 2 && args[0].equals("grid")) {
				try {
					int n = Integer.valueOf(args[1]);
					int procNo = n*n;
					List<int[]> pairs = Test.makeGrid(n);
					int minimum = procNo;
					String minimumS = "";
					int maximum = 0;
					String maximumS = "";
					
					Proc[] processors = new Proc[procNo];
					for (int t=0;t<1000;t++) {
						if (t%10 == 0) System.out.println(t/10 + "%");
					
						for (int i=0;i<procNo;i++) processors[i] = new Proc();
						for (int[] pair : pairs) {
							processors[pair[0]].addNeighbour(processors[pair[1]]);
							if (debug) System.out.println(pair[0] + ","+pair[1]);
						}
						
						for (Proc p : processors) p.start();
						
						boolean ready = false;
						int c = 0;
						while (!ready) {
							if (printRes) {
								System.out.println();
								System.out.println(procToStr(processors, n));
							} else if (debug) {
								c++;
								if (c%2000 == 0) {
									System.out.println();
									System.out.println(procToStr(processors, n));
								}
							}
							
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								System.err.println("INTERRUPTED");
							}
							ready = true;
							for (int i=0;i<procNo;i++) {
								if (!processors[i].isReady()) {
									ready = false;
								}
							}
						}
						int count = 0;
						for (Proc p : processors) if (p.getVal()) count++;
						if (count < minimum) {
							minimum = count;
							minimumS = procToStr(processors, n);
						}
						if (count > maximum) {
							maximum = count;
							maximumS = procToStr(processors, n);
						}
					}
					
					StringBuilder result = new StringBuilder();
					result.append("minimum:").append(minimum).append("\n").append(minimumS).append("\n");
					result.append("maximum:").append(maximum).append("\n").append(maximumS).append("\n");
					result.append("\n");
					RW.write(args[2], result.toString());
				} catch (NumberFormatException e) {
					System.err.println("wrong grid size: '"+args[1]+"'");
				}
			} else {
				Integer[] graph = Splitter.splitToNumbers(RW.read(args[0]));
				int n = graph[0];
				Proc[] processors = new Proc[n];
				for (int i=0;i<n;i++) processors[i] = new Proc();
				for (int i=1;i<graph.length;i += 2) {
					processors[graph[i]].addNeighbour(processors[graph[i+1]]);
				}
				for (int i=0;i<n;i++) processors[i].start();
				boolean ready = false;
				while (!ready) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						System.err.println("INTERRUPTED");
					}
					ready = true;
					for (int i=0;i<n;i++) {
						if (!processors[i].isReady()) {
							ready = false;
						}
					}
				}
				
				StringBuilder result = new StringBuilder("chosen processors:\n");
				for (int i=0;i<n;i++) {
					if (processors[i].getVal()) result.append(i).append("\n");
				}
				RW.write(args[1], result.toString());
			}
		} else {
			System.out.println("Usage: java Test <input> <output>");
		}
	}
}