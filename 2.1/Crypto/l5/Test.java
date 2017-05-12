public class Test {
	public static void main(String[] args) {
		ICoder rsa = new RSA();
		rsa.createKey();
		System.out.println("key created");
		ICoder rsa2 = new RSA();
		rsa2.setPublicKey(rsa.getPublicKey());
		
		byte[] msg = "random msg".getBytes();
		byte[] c = rsa2.encrypt(msg);
		byte[] m = rsa.decrypt(c);
		System.out.println(new String(m));
	}
}