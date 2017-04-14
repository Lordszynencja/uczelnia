public class MutualExclusionTest {
	int[] r;
	int n;
	int rand;
	
	public MutualExclusionTest(int n) {
		this.n = n;
		this.rand = new Random();
		this.r = new int[n];
	}
	
	private prepareRegisters() {
		for (int i=0;i<n;i++) r[i] = rand.nextInt(n);
	}
	
	private static void printResults(int[] times, int tests) {
		System.out.println(tests + " tests");
	}
	
	private isStable() {
		boolean isStable = false;
		if (r[0] == r[n-1]) isStable = true;
		for (int i=1;i<n;i++) {
			if (r[i] != r[i-1]) {
				if (!isStable) isStable = true;
				else return false;
			}
		}
		return isStable;
	}
	
	private int[] randomPermutation() {
		int[] permutation = new int[n];
		for (int i=0;i<n;i++) permutation[i] = i;
		for (int i=0;i<n*10;i++) {
			int x1 = rand.nextInt(n);
			int x2 = rand.nextInt(n);
			int val = permutation[x1];
			permutation[x1] = permutation[x2];
			permutation[x2] = val;
		}
		return permutation;
	}
	
	private void runProcessor(int i) {
		if (i == 0) {
			if (r[0] == r[n-1]) r[0] = (r[n-1]+1)%n;
		} else {
			if (r[i] != r[i-1]) r[i] = r[i-1];
		}
	}
	
	private int test1() {
		int time = 0;
		while (!this.isStable() && time<n*n*n+1000) {
			for (int i=0;i<n;i++) this.runProcessor(i);
			time++;
		}
	}
	
	private int randomTest() {
		int time = 0;
		while (!this.isStable() && time<n*n*n+1000) {
			int[] permutation = this.randomPermutation();
			for (int i=0;i<n;i++) this.runProcessor(permutation[i]);
			time++;
		}
	}
	
	public int deterministicTest() {
		int time = 0;
		while (!this.isStable() && time<n*n*n+1000) {
			for (int i=0;i<n;i++) this.runProcessor(permutation[i]);
			time++;
		}
	}
	
	public executeTest(boolean shouldBeRandom) {
		int tests = 1000;
		if (shouldBeRandom) {
			int[] times = new int[tests];
			for (int i=0;i<tests;i++) {
				this.prepareRegisters();
				times[i] = this.test1();
			}
			System.out.println("printing test1 results");
			MutualExclusonTest.printResult(times, tests);
			for (int i=0;i<tests;i++) {
				this.prepareRegisters();
				times[i] = this.randomTest();
			}
			System.out.println("printing random test results");
			MutualExclusonTest.printResult(times, tests);
		} else {
			int[] times = new int[tests];
			for (int i=0;i<tests;i++) {
				this.prepareRegisters();
				times[i] = this.deterministicTest();
			}
			System.out.println("printing deterministic test results");
			MutualExclusonTest.printResult(times, tests);
		}
	}
	
	public static void main(Strin[] args) {
		MutualExclusionTest test = new MutualExclusionTest(5);
		
	}
}