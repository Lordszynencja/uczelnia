import java.math.BigInteger;

public class BigIntAlg {
	public static BigInteger[] extendedGCD(final BigInteger a, final BigInteger b) {
		BigInteger s = BigInteger.ZERO;
		BigInteger old_s = BigInteger.ONE;
		BigInteger t = BigInteger.ONE;
		BigInteger old_t = BigInteger.ZERO;
		BigInteger r = b;
		BigInteger old_r = a;
		while (!(r.equals(BigInteger.ZERO))) {
			BigInteger quotient = old_r.divide(r);
			BigInteger tmp = old_r;
			old_r = r;
			r = tmp.subtract(quotient.multiply(r));
			tmp = old_s;
			old_s = s;
			s = tmp.subtract(quotient.multiply(s));
			tmp = old_t;
			old_t = t;
			t = tmp.subtract(quotient.multiply(t));
		}
		return new BigInteger[] { old_s, old_t };
	}
	
	public static int legendre(final BigInteger a, final BigInteger p) {
		if (a.equals(BigInteger.ZERO)) {
			return 0;
		}
		if (a.equals(BigInteger.ONE)) {
			return 1;
		}
		int result;
		if (a.mod(BigInteger.ZERO.setBit(1)).equals(BigInteger.ZERO)) {
			result = legendre(a.divide(BigInteger.ZERO.setBit(1)), p);
			if (p.multiply(p).subtract(BigInteger.ONE).testBit(3)) {
				result = -result;
			}
		} else {
			result = legendre(p.mod(a), a);
			if (a.subtract(BigInteger.ONE).multiply(p.subtract(BigInteger.ONE)).testBit(2)) {
				result = -result;
			}
		}
		return result;
	}

	public static BigInteger modSqrt(BigInteger n, BigInteger p) {
		BigInteger pMinusOne = p.subtract(BigInteger.ONE);
		int S = trailingZeros(pMinusOne);
		if (S == 1) {
			BigInteger exp = p.add(new BigInteger("1")).divide(new BigInteger("4"));
			return n.modPow(exp, p);
		}
		if (legendre(n, p) != 1) {
			return null;
		}
		BigInteger Q = pMinusOne.divide(BigInteger.ZERO.setBit(S));
		BigInteger z = BigInteger.ONE;
		while (legendre(z, p) != -1) {
			z = z.add(BigInteger.ONE);
		}
		
		BigInteger c = z.modPow(Q, p);
		BigInteger R = n.modPow(Q.add(BigInteger.ONE).shiftRight(1), p);
		BigInteger t = n.modPow(Q, p);
		BigInteger M = BigInteger.valueOf(S);
		while (!t.mod(p).equals(BigInteger.ONE)) {
			BigInteger i = BigInteger.ONE;
			BigInteger ti = t.multiply(t);
			while (!ti.mod(p).equals(BigInteger.ONE)) {
				ti = ti.multiply(ti);
				i = i.add(BigInteger.ONE);
			}
			BigInteger b = pow(c, BigInteger.ONE.shiftLeft(M.subtract(i).subtract(BigInteger.ONE).intValue()));
			R = R.multiply(b);
			c = b.multiply(b);
			t = t.multiply(c);
			M = i;
		}

		return R;
	}

	public static BigInteger pow(final BigInteger a, final BigInteger b) {
		if (BigInteger.ZERO.equals(b)) {
			return BigInteger.ONE;
		}
		if (BigInteger.ONE.equals(b)) {
			return a;
		}
		final BigInteger aToB2 = pow(a, b.shiftRight(1));
		final BigInteger remainder = pow(a, b.mod(BigInteger.ZERO.setBit(1)));
		return aToB2.multiply(aToB2).multiply(remainder);
	}

	public static void test() {
		System.out.println("\ntrailingZeros:");
		final int zero1 = trailingZeros(BigInteger.ZERO);
		final int zero2 = trailingZeros(BigInteger.ONE);
		final int b1 = trailingZeros(BigInteger.ONE.shiftLeft(1));
		final int b10 = trailingZeros(BigInteger.ONE.shiftLeft(10));

		if ((zero1 == 0) && (zero2 == 0) && (b1 == 1) && (b10 == 10)) {
			System.out.println("SUCCESS");
		} else {
			System.out.println("FAILURE");
		}

		System.out.println("\nmodSqrt:");
		final BigInteger a = modSqrt(BigInteger.ONE, BigInteger.ONE.setBit(2).setBit(4));
		if (BigInteger.ONE.equals(a)) {
			System.out.println("SUCCESS");
		} else {
			System.out.println("FAILURE");
		}

		System.out.println("\npow:");
		final BigInteger pow2_1 = pow(BigInteger.ZERO.setBit(1), BigInteger.ONE);
		final BigInteger pow2_11 = pow(BigInteger.ZERO.setBit(1), BigInteger.ONE.setBit(3).setBit(1));
		if (new BigInteger("2").equals(pow2_1) && new BigInteger("2048").equals(pow2_11)) {
			System.out.println("SUCCESS");
		} else {
			System.out.println("FAILURE");
		}
	}

	public static int trailingZeros(final BigInteger a) {
		if (a.equals(BigInteger.ZERO)) {
			return 0;
		}
		int count = 0;
		while (!a.testBit(count)) {
			count++;
		}
		return count;
	}
}
