public class Test {
	private static void testQueue(QueueStat q, int steps) {
		q.calculate(steps);
		System.out.println(q.toString());
	}
	
	public static void main(String[] args) {
		int n = 999;
		double alpha = 0.1;
		double diff = 0.05;
		int steps = 100000;
		System.out.println("beta > alpha");
		testQueue(new QueueStat(30, alpha+diff, alpha), steps);
		System.out.println("beta = alpha");
		testQueue(new QueueStat(1000, alpha, alpha), steps);
		System.out.println("beta < alpha");
		testQueue(new QueueStat(10000, alpha-diff, alpha), steps);
	}
}