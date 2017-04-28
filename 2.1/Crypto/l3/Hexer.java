public class Hexer {
	public static byte byteVal(final String hex) {
		final char c0 = hex.charAt(0);
		final char c1 = hex.charAt(1);
		byte b0;
		byte b1;
		if (c0 < 'A') {
			b0 = (byte) (c0 - '0');
		} else {
			b0 = (byte) ((c0 - 'A') + 10);
		}
		if (c1 < 'A') {
			b1 = (byte) (c1 - '0');
		} else {
			b1 = (byte) ((c1 - 'A') + 10);
		}
		return (byte) ((16 * b0) + b1);
	}

	public static String hexVal(final byte bt) {
		final int b = (bt < 0 ? bt + 256 : bt);
		final int b0 = (byte) (b / 16);
		final int b1 = (byte) (b % 16);
		char c0;
		char c1;
		if (b0 < 10) {
			c0 = (char) (b0 + '0');
		} else {
			c0 = (char) ((b0 + 'A') - 10);
		}
		if (b1 < 10) {
			c1 = (char) (b1 + '0');
		} else {
			c1 = (char) ((b1 + 'A') - 10);
		}
		return "" + c0 + c1;
	}

	public static byte[] fromHex(final String hex) {
		final byte[] bytes = new byte[hex.length() / 2];
		if (hex.length()%2 != 0) {
			System.out.println("wrong length: "+hex.length()+"\n"+hex);
			return new byte[0];
		}
		for (int i = 0; i < hex.length(); i += 2) {
			bytes[i / 2] = Hexer.byteVal(hex.substring(i, i + 2));
		}
		return bytes;
	}

	public static String toHex(final byte[] bytes) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Hexer.hexVal(bytes[i]));
		}
		return sb.toString();
	}
}