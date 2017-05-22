import java.math.*;
import java.util.*;

public class Test {
	public static void main(String[] args) {
		BigInteger n = new BigInteger("122351821");
		BigInteger c = new BigInteger("67625338");
		BigInteger m = BigInteger.ZERO;
		while (!m.equals(n)) {
			if (m.multiply(m).mod(n).equals(c)) {
				System.out.print("m: "+m);
				System.out.println(" msg: "+new String(m.toByteArray()));
			}
			m = m.add(BigInteger.ONE);
		}
	}
}