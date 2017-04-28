import java.util.*;

public class MaxIndependentSet {
	private Map<Integer, List<Integer>> neighbours = new HashMap<Integer, List<Integer>>();
	
	public MaxIndependentSet(String filename) {
		Integer[] numbers = Splitter.splitToNumbers(RW.read(filename));
		for (int i=0;i<numbers.length/2;i++) {
			Integer v1 = numbers[2*i];
			Integer v2 = numbers[2*i+1];
			this.addNeighbours(v1, v2);
		}
	}
	
	private void addNeighbours(Integer v1, Integer v2) {
		this.addNeighbour(v1, v2);
		this.addNeighbour(v2, v1);
	}
	
	private void addNeighbour(Integer v1, Integer v2) {
		List<Integer> vertexNeighbours = this.neighbours.get(v1);
		if (vertexNeighbours == null) {
			vertexNeighbours = new LinkedList<Integer>();
			this.neighbours.put(v1, vertexNeighbours);
		}
		vertexNeighbours.add(v2);
	}
	
	private Integer pickVertexV1() {
		return (Integer)this.neighbours.keySet().toArray()[0];
	}
	
	private Integer pickVertexV2() {
		Set<Integer> vertexesSet = this.neighbours.keySet();
		Integer best = null;
		int bestScore = Integer.MAX_VALUE;
		for (Integer v : vertexesSet) {
			int score = this.neighbours.get(v).size();
			if (score < bestScore) {
				best = v;
				bestScore = score;
			}
		}
		return best;
	}
	
	public List<Integer> resolve() {
		List<Integer> vertexes = new LinkedList<Integer>();
		while (!this.neighbours.isEmpty()) {
			Integer v = this.pickVertexV2();
			List<Integer> vertexNeighbours = this.neighbours.remove(v);
			for (Integer i : vertexNeighbours) {
				this.neighbours.remove(i);
			}
			vertexes.add(v);
		}
		return vertexes;
	}
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("wrong arguments");
		} else {
			MaxIndependentSet setting = new MaxIndependentSet(args[0]);
			List<Integer> vertexes = setting.resolve();
			String outputFilename = args[0].split("\\.")[0]+"_out.txt";
			StringBuilder result = new StringBuilder();
			for (Integer v : vertexes) {
				result.append(v.toString()).append("\n");
			}
			RW.write(outputFilename, result.toString());
		}
	}
}