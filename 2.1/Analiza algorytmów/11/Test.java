public class Test {
	public static void main(String[] args) {
		QueueStat q = new QueueStat(5, 0.002, 0.002);
		q.calculate(10000);
		System.out.println(q.toString());
		System.out.println();
	}
}