import java.math.*;
import java.util.*;

public class PrimeCount {
	public static int probablePrimes() {
		return (int)(1000.0/Math.log(2)/64.0);
	}
	
	public static void main(String[] args) {
		BigInteger b = BigInteger.ONE;
		b = BigInteger.ONE.shiftLeft(64).add(BigInteger.ONE);
		int primesCount = 0;
		List<BigInteger> primes = new ArrayList<BigInteger>();
		
		for (int i=0;i<500;i++) {
			if (b.isProbablePrime(100)) {
				primesCount++;
				primes.add(b);
			}
			b = b.add(new BigInteger("2"));
		}
		
		System.out.println("aproximate primes count: "+PrimeCount.probablePrimes());
		System.out.println("primes count: "+primesCount);
		for (BigInteger i : primes) System.out.println(i.toString());
		
	}
}